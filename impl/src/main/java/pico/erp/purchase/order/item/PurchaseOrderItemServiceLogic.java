package pico.erp.purchase.order.item;

import java.util.List;
import java.util.stream.Collectors;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.audit.AuditService;
import pico.erp.purchase.order.PurchaseOrderId;
import pico.erp.purchase.order.PurchaseOrderProperties;
import pico.erp.purchase.order.PurchaseOrderService;
import pico.erp.purchase.order.item.PurchaseOrderItemRequests.DeleteRequest;
import pico.erp.shared.Public;
import pico.erp.shared.event.EventPublisher;

@SuppressWarnings("Duplicates")
@Service
@Public
@Transactional
@Validated
public class PurchaseOrderItemServiceLogic implements PurchaseOrderItemService {

  @Autowired
  private PurchaseOrderItemRepository planDetailRepository;

  @Autowired
  private EventPublisher eventPublisher;

  @Autowired
  private PurchaseOrderItemMapper mapper;

  @Autowired
  private PurchaseOrderProperties properties;

  @Autowired
  private PurchaseOrderService purchaseOrderService;

  @Lazy
  @Autowired
  private AuditService auditService;


  @Override
  public PurchaseOrderItemData create(PurchaseOrderItemRequests.CreateRequest request) {
    val item = new PurchaseOrderItem();
    val response = item.apply(mapper.map(request));
    if (planDetailRepository.exists(item.getId())) {
      throw new PurchaseOrderItemExceptions.AlreadyExistsException();
    }
    val created = planDetailRepository.create(item);
    auditService.commit(created);
    eventPublisher.publishEvents(response.getEvents());
    return mapper.map(created);
  }

  @Override
  public void delete(DeleteRequest request) {
    val item = planDetailRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    planDetailRepository.deleteBy(item.getId());
    auditService.commit(item);
    eventPublisher.publishEvents(response.getEvents());
  }



  @Override
  public boolean exists(PurchaseOrderItemId id) {
    return planDetailRepository.exists(id);
  }


  @Override
  public PurchaseOrderItemData get(PurchaseOrderItemId id) {
    return planDetailRepository.findBy(id)
      .map(mapper::map)
      .orElseThrow(PurchaseOrderItemExceptions.NotFoundException::new);
  }

  @Override
  public List<PurchaseOrderItemData> getAll(PurchaseOrderId planId) {
    return planDetailRepository.findAllBy(planId)
      .map(mapper::map)
      .collect(Collectors.toList());
  }

  @Override
  public void update(PurchaseOrderItemRequests.UpdateRequest request) {
    val item = planDetailRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderItemExceptions.NotFoundException::new);
    val response = item.apply(mapper.map(request));
    planDetailRepository.update(item);
    auditService.commit(item);
    eventPublisher.publishEvents(response.getEvents());
  }

}
