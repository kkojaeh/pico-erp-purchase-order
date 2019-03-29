package pico.erp.purchase.order.item;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kkojaeh.spring.boot.component.Give;
import kkojaeh.spring.boot.component.Take;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.item.ItemService;
import pico.erp.item.spec.ItemSpecRequests;
import pico.erp.item.spec.ItemSpecService;
import pico.erp.purchase.order.PurchaseOrderId;
import pico.erp.purchase.order.item.PurchaseOrderItemRequests.GenerateRequest;
import pico.erp.purchase.request.PurchaseRequestId;
import pico.erp.purchase.request.PurchaseRequestService;
import pico.erp.shared.event.EventPublisher;

@SuppressWarnings("Duplicates")
@Service
@Give
@Transactional
@Validated
public class PurchaseOrderItemServiceLogic implements PurchaseOrderItemService {

  @Autowired
  private PurchaseOrderItemRepository purchaseOrderItemRepository;

  @Autowired
  private EventPublisher eventPublisher;

  @Autowired
  private PurchaseOrderItemMapper mapper;

  @Take
  private ItemSpecService itemSpecService;

  @Take
  private ItemService itemService;

  @Take
  private PurchaseRequestService purchaseRequestService;

  @Override
  public void cancel(PurchaseOrderItemRequests.CancelRequest request) {
    val item = purchaseOrderItemRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    purchaseOrderItemRepository.update(item);
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
    eventPublisher.publishEvents(response.getEvents());
    return mapper.map(created);
  }

  @Override
  public void delete(PurchaseOrderItemRequests.DeleteRequest request) {
    val item = purchaseOrderItemRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    purchaseOrderItemRepository.deleteBy(item.getId());
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void determine(PurchaseOrderItemRequests.DetermineRequest request) {
    val item = purchaseOrderItemRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    purchaseOrderItemRepository.update(item);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public boolean exists(PurchaseOrderItemId id) {
    return purchaseOrderItemRepository.exists(id);
  }

  @Override
  public boolean exists(PurchaseRequestId requestId) {
    return purchaseOrderItemRepository.exists(requestId);
  }

  @Override
  public void generate(GenerateRequest request) {
    request.getRequestIds().stream()
      .map(purchaseRequestService::get)
      .map(purchaseRequest -> {
        val id = PurchaseOrderItemId.generate();
        val itemId = purchaseRequest.getItemId();
        val item = itemService.get(itemId);
        val itemSpecId = purchaseRequest.getItemSpecId();
        val itemSpecCode = purchaseRequest.getItemSpecCode();
        val itemSpec = Optional.ofNullable(itemSpecId)
          .map(itemSpecService::get)
          .orElse(null);
        val quantity = Optional.ofNullable(itemSpec)
          .map(spec -> itemSpecService.calculate(
            new ItemSpecRequests.CalculatePurchaseQuantityRequest(spec.getId(),
              purchaseRequest.getQuantity())
          ))
          .orElse(purchaseRequest.getQuantity());
        val unit = Optional.ofNullable(itemSpec)
          .map(spec -> spec.getPurchaseUnit())
          .orElse(purchaseRequest.getUnit());
        val unitCost = Optional.ofNullable(itemSpec)
          .map(spec -> spec.getPurchaseUnitCost())
          .orElse(item.getBaseUnitCost());
        val remark = purchaseRequest.getRemark();
        val projectId = purchaseRequest.getProjectId();

        return PurchaseOrderItemRequests.CreateRequest.builder()
          .id(id)
          .orderId(request.getId())
          .itemId(itemId)
          .itemSpecId(itemSpecId)
          .itemSpecCode(itemSpecCode)
          .quantity(quantity)
          .unit(unit)
          .unitCost(unitCost)
          .remark(remark)
          .projectId(projectId)
          .requestId(purchaseRequest.getId())
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
  public PurchaseOrderItemData get(PurchaseRequestId requestId) {
    return purchaseOrderItemRepository.findBy(requestId)
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
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void reject(PurchaseOrderItemRequests.RejectRequest request) {
    val item = purchaseOrderItemRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    purchaseOrderItemRepository.update(item);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void send(PurchaseOrderItemRequests.SendRequest request) {
    val item = purchaseOrderItemRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    purchaseOrderItemRepository.update(item);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void update(PurchaseOrderItemRequests.UpdateRequest request) {
    val item = purchaseOrderItemRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    purchaseOrderItemRepository.update(item);
    eventPublisher.publishEvents(response.getEvents());
  }

}
