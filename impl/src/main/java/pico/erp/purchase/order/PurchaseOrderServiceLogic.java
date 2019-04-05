package pico.erp.purchase.order;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import kkojaeh.spring.boot.component.ComponentAutowired;
import kkojaeh.spring.boot.component.ComponentBean;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.company.CompanyService;
import pico.erp.delivery.DeliveryId;
import pico.erp.delivery.DeliveryRequests;
import pico.erp.delivery.DeliveryService;
import pico.erp.document.DocumentId;
import pico.erp.document.DocumentRequests;
import pico.erp.document.DocumentService;
import pico.erp.purchase.order.PurchaseOrderRequests.CancelRequest;
import pico.erp.purchase.order.PurchaseOrderRequests.DetermineRequest;
import pico.erp.purchase.order.PurchaseOrderRequests.GenerateRequest;
import pico.erp.purchase.order.PurchaseOrderRequests.ReceiveRequest;
import pico.erp.purchase.order.PurchaseOrderRequests.RejectRequest;
import pico.erp.purchase.order.PurchaseOrderRequests.SendRequest;
import pico.erp.purchase.request.PurchaseRequestService;
import pico.erp.purchase.request.PurchaseRequestStatusKind;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.data.Address;
import pico.erp.shared.event.EventPublisher;
import pico.erp.warehouse.location.site.SiteService;

@SuppressWarnings("Duplicates")
@Service
@ComponentBean
@Transactional
@Validated
public class PurchaseOrderServiceLogic implements PurchaseOrderService {

  @Autowired
  private PurchaseOrderRepository purchaseOrderRepository;

  @Autowired
  private EventPublisher eventPublisher;

  @Autowired
  private PurchaseOrderMapper mapper;

  @ComponentAutowired
  private PurchaseRequestService purchaseRequestService;

  @ComponentAutowired
  private SiteService siteService;

  @ComponentAutowired
  private DocumentService documentService;

  @ComponentAutowired
  private DeliveryService deliveryService;

  @ComponentAutowired
  private CompanyService companyService;

  @Override
  public void cancel(CancelRequest request) {
    val purchaseOrder = purchaseOrderRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderExceptions.NotFoundException::new);
    val response = purchaseOrder.apply(mapper.map(request));
    purchaseOrderRepository.update(purchaseOrder);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public PurchaseOrderData create(PurchaseOrderRequests.CreateRequest request) {
    val purchaseOrder = new PurchaseOrder();
    val response = purchaseOrder.apply(mapper.map(request));
    if (purchaseOrderRepository.exists(purchaseOrder.getId())) {
      throw new PurchaseOrderExceptions.AlreadyExistsException();
    }
    val created = purchaseOrderRepository.create(purchaseOrder);
    eventPublisher.publishEvents(response.getEvents());
    return mapper.map(created);
  }

  @Override
  public void determine(DetermineRequest request) {
    val purchaseOrder = purchaseOrderRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderExceptions.NotFoundException::new);
    val message = mapper.map(request);
    val previousDraftId = purchaseOrder.getDraftId();
    val draftId = DocumentId.generate();
    val deliveryId = DeliveryId.generate();
    message.setDeliveryId(deliveryId);
    message.setDraftId(draftId);
    val response = purchaseOrder.apply(message);
    purchaseOrderRepository.update(purchaseOrder);
    eventPublisher.publishEvents(response.getEvents());
    if (previousDraftId != null) {
      documentService.delete(
        new DocumentRequests.DeleteRequest(previousDraftId)
      );
    }
    val supplier = companyService.get(purchaseOrder.getSupplierId());
    val name = String.format("PO-%s-%s-%s",
      purchaseOrder.getCode().getValue(),
      supplier.getName(),
      DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now())
    );
    documentService.create(
      DocumentRequests.CreateRequest.builder()
        .id(draftId)
        .subjectId(PurchaseOrderDraftDocumentSubjectDefinition.ID)
        .name(name)
        .key(purchaseOrder.getId())
        .creatorId(purchaseOrder.getChargerId())
        .build()
    );
    deliveryService.create(
      DeliveryRequests.CreateRequest.builder()
        .id(deliveryId)
        .subjectId(PurchaseOrderDraftDeliverySubjectDefinition.ID)
        .key(purchaseOrder.getId())
        .build()
    );
  }

  @Override
  public boolean exists(PurchaseOrderId id) {
    return purchaseOrderRepository.exists(id);
  }

  @Override
  public PurchaseOrderData get(PurchaseOrderId id) {
    return purchaseOrderRepository.findBy(id)
      .map(mapper::map)
      .orElseThrow(PurchaseOrderExceptions.NotFoundException::new);
  }

  @Override
  public PurchaseOrderData generate(GenerateRequest request) {
    val purchaseRequests = request.getRequestIds().stream()
      .map(purchaseRequestService::get)
      .collect(Collectors.toList());
    val supplierEquals = purchaseRequests.stream()
      .map(purchaseRequest -> "" + purchaseRequest.getSupplierId())
      .distinct()
      .limit(2)
      .count() < 2;
    val locationEquals = purchaseRequests.stream()
      .map(purchaseRequest -> "" + purchaseRequest.getReceiverId() + purchaseRequest
        .getReceiveSiteId())
      .distinct()
      .limit(2)
      .count() < 2;
    val allAccepted = purchaseRequests.stream()
      .allMatch(
        purchaseRequest -> purchaseRequest.getStatus() == PurchaseRequestStatusKind.ACCEPTED);

    if (!supplierEquals || !locationEquals || !allAccepted) {
      throw new PurchaseOrderExceptions.CannotGenerateException();
    }

    val dueDate = purchaseRequests.stream()
      .map(purchaseRequest -> purchaseRequest.getDueDate())
      .min(Comparator.comparing(d -> d))
      .orElseGet(() -> OffsetDateTime.now().plusDays(1));
    val supplierId = purchaseRequests.stream().findAny().get().getSupplierId();
    val collectedRemark = purchaseRequests.stream()
      .map(purchaseRequest -> Optional.ofNullable(purchaseRequest.getRemark()).orElse(""))
      .collect(Collectors.joining("\n"));
    val remark = collectedRemark
      .substring(0, Math.min(collectedRemark.length(), TypeDefinitions.REMARK_LENGTH));
    val purchaseRequest = purchaseRequests.get(0);
    val address = new Address();
    if (purchaseRequest.getReceiveSiteId() != null) {
      val siteAddress = siteService.get(purchaseRequest.getReceiveSiteId()).getAddress();
      address.setPostalCode(siteAddress.getPostalCode());
      address.setStreet(siteAddress.getStreet());
      address.setDetail(siteAddress.getDetail());
    }

    val created = create(
      PurchaseOrderRequests.CreateRequest.builder()
        .id(request.getId())
        .dueDate(dueDate)
        .chargerId(request.getChargerId())
        .supplierId(supplierId)
        .receiveAddress(address)
        .receiverId(purchaseRequest.getReceiverId())
        .remark(remark)
        .build()
    );
    eventPublisher.publishEvent(
      new PurchaseOrderEvents.GeneratedEvent(request.getRequestIds(), created.getId())
    );

    return created;
  }

  @Override
  public void receive(ReceiveRequest request) {
    val purchaseOrder = purchaseOrderRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderExceptions.NotFoundException::new);
    val response = purchaseOrder.apply(mapper.map(request));
    purchaseOrderRepository.update(purchaseOrder);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void reject(RejectRequest request) {
    val purchaseOrder = purchaseOrderRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderExceptions.NotFoundException::new);
    val response = purchaseOrder.apply(mapper.map(request));
    purchaseOrderRepository.update(purchaseOrder);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void send(SendRequest request) {
    val purchaseOrder = purchaseOrderRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderExceptions.NotFoundException::new);
    val response = purchaseOrder.apply(mapper.map(request));
    purchaseOrderRepository.update(purchaseOrder);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void update(PurchaseOrderRequests.UpdateRequest request) {
    val purchaseOrder = purchaseOrderRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderExceptions.NotFoundException::new);
    val response = purchaseOrder.apply(mapper.map(request));
    purchaseOrderRepository.update(purchaseOrder);
    eventPublisher.publishEvents(response.getEvents());
  }

}
