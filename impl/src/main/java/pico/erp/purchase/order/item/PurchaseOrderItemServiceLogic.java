package pico.erp.purchase.order.item;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.audit.AuditService;
import pico.erp.item.ItemService;
import pico.erp.item.spec.ItemSpecRequests;
import pico.erp.item.spec.ItemSpecService;
import pico.erp.purchase.order.PurchaseOrderId;
import pico.erp.purchase.order.item.PurchaseOrderItemRequests.GenerateRequest;
import pico.erp.purchase.request.PurchaseRequestService;
import pico.erp.purchase.request.item.PurchaseRequestItemId;
import pico.erp.purchase.request.item.PurchaseRequestItemService;
import pico.erp.shared.Public;
import pico.erp.shared.event.EventPublisher;

@SuppressWarnings("Duplicates")
@Service
@Public
@Transactional
@Validated
public class PurchaseOrderItemServiceLogic implements PurchaseOrderItemService {

  @Autowired
  private PurchaseOrderItemRepository purchaseOrderItemRepository;

  @Autowired
  private EventPublisher eventPublisher;

  @Autowired
  private PurchaseOrderItemMapper mapper;

  @Lazy
  @Autowired
  private AuditService auditService;

  @Lazy
  @Autowired
  private PurchaseRequestItemService purchaseRequestItemService;

  @Lazy
  @Autowired
  private ItemSpecService itemSpecService;

  @Lazy
  @Autowired
  private ItemService itemService;

  @Lazy
  @Autowired
  private PurchaseRequestService purchaseRequestService;

  @Override
  public void cancel(PurchaseOrderItemRequests.CancelRequest request) {
    val item = purchaseOrderItemRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    purchaseOrderItemRepository.update(item);
    auditService.commit(item);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public PurchaseOrderItemData create(PurchaseOrderItemRequests.CreateRequest request) {
    val item = new PurchaseOrderItem();
    val response = item.apply(mapper.map(request));
    if (purchaseOrderItemRepository.exists(item.getId())) {
      throw new PurchaseOrderItemExceptions.AlreadyExistsException();
    }
    val created = purchaseOrderItemRepository.create(item);
    auditService.commit(created);
    eventPublisher.publishEvents(response.getEvents());
    return mapper.map(created);
  }

  @Override
  public void delete(PurchaseOrderItemRequests.DeleteRequest request) {
    val item = purchaseOrderItemRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    purchaseOrderItemRepository.deleteBy(item.getId());
    auditService.commit(item);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void determine(PurchaseOrderItemRequests.DetermineRequest request) {
    val item = purchaseOrderItemRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    purchaseOrderItemRepository.update(item);
    auditService.commit(item);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public boolean exists(PurchaseOrderItemId id) {
    return purchaseOrderItemRepository.exists(id);
  }

  @Override
  public boolean exists(PurchaseRequestItemId requestItemId) {
    return purchaseOrderItemRepository.exists(requestItemId);
  }

  @Override
  public void generate(GenerateRequest request) {
    request.getRequestItemIds().stream()
      .map(purchaseRequestItemService::get)
      .map(purchaseRequestItem -> {
        val purchaseRequest = purchaseRequestService.get(purchaseRequestItem.getRequestId());
        val id = PurchaseOrderItemId.generate();
        val itemId = purchaseRequestItem.getItemId();
        val item = itemService.get(itemId);
        val itemSpecId = purchaseRequestItem.getItemSpecId();
        val itemSpec = Optional.ofNullable(itemSpecId)
          .map(itemSpecService::get)
          .orElse(null);
        val quantity = Optional.ofNullable(itemSpec)
          .map(spec -> itemSpecService.calculate(
            new ItemSpecRequests.CalculatePurchaseQuantityRequest(spec.getId(),
              purchaseRequestItem.getQuantity())
          ))
          .orElse(purchaseRequestItem.getQuantity());
        val unit = Optional.ofNullable(itemSpec)
          .map(spec -> spec.getPurchaseUnit())
          .orElse(item.getUnit());
        val unitCost = Optional.ofNullable(itemSpec)
          .map(spec -> spec.getPurchaseUnitCost())
          .orElse(item.getBaseUnitCost());
        val remark = purchaseRequestItem.getRemark();
        val projectId = purchaseRequest.getProjectId();

        return PurchaseOrderItemRequests.CreateRequest.builder()
          .id(id)
          .itemId(itemId)
          .itemSpecId(itemSpecId)
          .quantity(quantity)
          .unit(unit)
          .unitCost(unitCost)
          .remark(remark)
          .projectId(projectId)
          .requestItemId(purchaseRequestItem.getId())
          .build();

      })
      .forEach(this::create);
  }

  @Override
  public PurchaseOrderItemData get(PurchaseOrderItemId id) {
    return purchaseOrderItemRepository.findBy(id)
      .map(mapper::map)
      .orElseThrow(PurchaseOrderItemExceptions.NotFoundException::new);
  }

  @Override
  public PurchaseOrderItemData get(PurchaseRequestItemId requestItemId) {
    return purchaseOrderItemRepository.findBy(requestItemId)
      .map(mapper::map)
      .orElseThrow(PurchaseOrderItemExceptions.NotFoundException::new);
  }

  @Override
  public List<PurchaseOrderItemData> getAll(PurchaseOrderId orderId) {
    return purchaseOrderItemRepository.findAllBy(orderId)
      .map(mapper::map)
      .collect(Collectors.toList());
  }

  @Override
  public void receive(PurchaseOrderItemRequests.ReceiveRequest request) {
    val item = purchaseOrderItemRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    purchaseOrderItemRepository.update(item);
    auditService.commit(item);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void reject(PurchaseOrderItemRequests.RejectRequest request) {
    val item = purchaseOrderItemRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    purchaseOrderItemRepository.update(item);
    auditService.commit(item);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void send(PurchaseOrderItemRequests.SendRequest request) {
    val item = purchaseOrderItemRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    purchaseOrderItemRepository.update(item);
    auditService.commit(item);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void update(PurchaseOrderItemRequests.UpdateRequest request) {
    val item = purchaseOrderItemRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    purchaseOrderItemRepository.update(item);
    auditService.commit(item);
    eventPublisher.publishEvents(response.getEvents());
  }

}
