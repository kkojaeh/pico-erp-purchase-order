package pico.erp.purchase.order;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.audit.AuditService;
import pico.erp.purchase.order.PurchaseOrderRequests.DetermineRequest;
import pico.erp.purchase.order.PurchaseOrderRequests.CancelRequest;
import pico.erp.purchase.order.PurchaseOrderRequests.SendRequest;
import pico.erp.purchase.order.PurchaseOrderRequests.ReceiveRequest;
import pico.erp.purchase.order.PurchaseOrderRequests.RejectRequest;
import pico.erp.shared.Public;
import pico.erp.shared.event.EventPublisher;

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

  @Override
  public void cancel(CancelRequest request) {
    val purchaseRequest = purchaseOrderRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderExceptions.NotFoundException::new);
    val response = purchaseRequest.apply(mapper.map(request));
    purchaseOrderRepository.update(purchaseRequest);
    auditService.commit(purchaseRequest);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public PurchaseOrderData create(PurchaseOrderRequests.CreateRequest request) {
    val purchaseRequest = new PurchaseOrder();
    val response = purchaseRequest.apply(mapper.map(request));
    if (purchaseOrderRepository.exists(purchaseRequest.getId())) {
      throw new PurchaseOrderExceptions.AlreadyExistsException();
    }
    val created = purchaseOrderRepository.create(purchaseRequest);
    auditService.commit(created);
    eventPublisher.publishEvents(response.getEvents());
    return mapper.map(created);
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
  public void update(PurchaseOrderRequests.UpdateRequest request) {
    val purchaseRequest = purchaseOrderRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderExceptions.NotFoundException::new);
    val response = purchaseRequest.apply(mapper.map(request));
    purchaseOrderRepository.update(purchaseRequest);
    auditService.commit(purchaseRequest);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void determine(DetermineRequest request) {
    val purchaseRequest = purchaseOrderRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderExceptions.NotFoundException::new);
    val response = purchaseRequest.apply(mapper.map(request));
    purchaseOrderRepository.update(purchaseRequest);
    auditService.commit(purchaseRequest);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void send(SendRequest request) {
    val purchaseRequest = purchaseOrderRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderExceptions.NotFoundException::new);
    val response = purchaseRequest.apply(mapper.map(request));
    purchaseOrderRepository.update(purchaseRequest);
    auditService.commit(purchaseRequest);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void receive(ReceiveRequest request) {
    val purchaseRequest = purchaseOrderRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderExceptions.NotFoundException::new);
    val response = purchaseRequest.apply(mapper.map(request));
    purchaseOrderRepository.update(purchaseRequest);
    auditService.commit(purchaseRequest);
    eventPublisher.publishEvents(response.getEvents());
  }

  @Override
  public void reject(RejectRequest request) {
    val purchaseRequest = purchaseOrderRepository.findBy(request.getId())
      .orElseThrow(PurchaseOrderExceptions.NotFoundException::new);
    val response = purchaseRequest.apply(mapper.map(request));
    purchaseOrderRepository.update(purchaseRequest);
    auditService.commit(purchaseRequest);
    eventPublisher.publishEvents(response.getEvents());
  }

}
