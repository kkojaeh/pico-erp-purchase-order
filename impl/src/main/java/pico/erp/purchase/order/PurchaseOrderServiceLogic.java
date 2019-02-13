package pico.erp.purchase.order;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.audit.AuditService;
import pico.erp.purchase.order.PurchaseOrderPrinter.DraftPrintOptions;
import pico.erp.purchase.order.PurchaseOrderRequests.CancelRequest;
import pico.erp.purchase.order.PurchaseOrderRequests.DetermineRequest;
import pico.erp.purchase.order.PurchaseOrderRequests.GenerateRequest;
import pico.erp.purchase.order.PurchaseOrderRequests.PrintDraftRequest;
import pico.erp.purchase.order.PurchaseOrderRequests.ReceiveRequest;
import pico.erp.purchase.order.PurchaseOrderRequests.RejectRequest;
import pico.erp.purchase.order.PurchaseOrderRequests.SendRequest;
import pico.erp.purchase.request.PurchaseRequestService;
import pico.erp.purchase.request.PurchaseRequestStatusKind;
import pico.erp.purchase.request.item.PurchaseRequestItemService;
import pico.erp.shared.Public;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.data.Address;
import pico.erp.shared.data.ContentInputStream;
import pico.erp.shared.event.EventPublisher;
import pico.erp.warehouse.location.site.SiteService;

@SuppressWarnings("Duplicates")
@Service
@Public
@Transactional
@Validated
public class PurchaseOrderServiceLogic implements PurchaseOrderService {

  @Autowired
  private PurchaseOrderRepository purchaseOrderRepository;

  @Autowired
  private EventPublisher eventPublisher;

  @Autowired
  private PurchaseOrderMapper mapper;

  @Lazy
  @Autowired
  private AuditService auditService;

  @Lazy
  @Autowired
  private PurchaseRequestService purchaseRequestService;

  @Lazy
  @Autowired
  private PurchaseRequestItemService purchaseRequestItemService;

  @Lazy
  @Autowired
  private SiteService siteService;

  @Autowired
  private PurchaseOrderPrinter printer;

  @Override
  public void cancel(CancelRequest request) {
    val purchaseOrder = purchaseOrderRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderExceptions.NotFoundException::new);
    val response = purchaseOrder.apply(mapper.map(request));
    purchaseOrderRepository.update(purchaseOrder);
    auditService.commit(purchaseOrder);
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
    auditService.commit(created);
    eventPublisher.publishEvents(response.getEvents());
    return mapper.map(created);
  }

  @Override
  public void determine(DetermineRequest request) {
    val purchaseOrder = purchaseOrderRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderExceptions.NotFoundException::new);
    val response = purchaseOrder.apply(mapper.map(request));
    purchaseOrderRepository.update(purchaseOrder);
    auditService.commit(purchaseOrder);
    eventPublisher.publishEvents(response.getEvents());
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
    val purchaseRequestItems = request.getRequestItemIds().stream()
      .map(purchaseRequestItemService::get)
      .collect(Collectors.toList());
    val purchaseRequests = purchaseRequestItems.stream()
      .map(requestItem -> requestItem.getRequestId())
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
      new PurchaseOrderEvents.GeneratedEvent(request.getRequestItemIds(), created.getId())
    );

    return created;
  }

  @Override
  public ContentInputStream printDraft(PrintDraftRequest request) {
    val purchaseOrder = purchaseOrderRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderExceptions.NotFoundException::new);
    if (!purchaseOrder.isPrintable()) {
      throw new PurchaseOrderExceptions.CannotPrintException();
    }
    return printer.printDraft(request.getId(), new DraftPrintOptions());
  }

  @Override
  public void receive(ReceiveRequest request) {
    val purchaseOrder = purchaseOrderRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderExceptions.NotFoundException::new);
    val response = purchaseOrder.apply(mapper.map(request));
    purchaseOrderRepository.update(purchaseOrder);
    auditService.commit(purchaseOrder);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void reject(RejectRequest request) {
    val purchaseOrder = purchaseOrderRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderExceptions.NotFoundException::new);
    val response = purchaseOrder.apply(mapper.map(request));
    purchaseOrderRepository.update(purchaseOrder);
    auditService.commit(purchaseOrder);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void send(SendRequest request) {
    val purchaseOrder = purchaseOrderRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderExceptions.NotFoundException::new);
    val response = purchaseOrder.apply(mapper.map(request));
    purchaseOrderRepository.update(purchaseOrder);
    auditService.commit(purchaseOrder);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void update(PurchaseOrderRequests.UpdateRequest request) {
    val purchaseOrder = purchaseOrderRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderExceptions.NotFoundException::new);
    val response = purchaseOrder.apply(mapper.map(request));
    purchaseOrderRepository.update(purchaseOrder);
    auditService.commit(purchaseOrder);
    eventPublisher.publishEvents(response.getEvents());
  }

}
