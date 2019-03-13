package pico.erp.purchase.order;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pico.erp.company.CompanyService;
import pico.erp.delivery.DeliveryService;
import pico.erp.document.DocumentService;
import pico.erp.purchase.order.item.PurchaseOrderItemEvents;
import pico.erp.purchase.order.item.PurchaseOrderItemService;
import pico.erp.purchase.order.item.PurchaseOrderItemStatusKind;
import pico.erp.purchase.request.PurchaseRequestService;

@SuppressWarnings("unused")
@Component
public class PurchaseOrderEventListener {

  private static final String LISTENER_NAME = "listener.purchase-order-event-listener";

  @Autowired
  private PurchaseOrderItemService purchaseOrderItemService;

  @Autowired
  private PurchaseOrderService purchaseOrderService;

  @Lazy
  @Autowired
  private PurchaseRequestService purchaseRequestService;

  @Lazy
  @Autowired
  private DocumentService documentService;

  @Lazy
  @Autowired
  private DeliveryService deliveryService;

  @Lazy
  @Autowired
  private CompanyService companyService;

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderItemEvents.CanceledEvent.CHANNEL)
  public void onOrderItemCanceled(PurchaseOrderItemEvents.CanceledEvent event) {
    val orderItem = purchaseOrderItemService.get(event.getId());
    val order = purchaseOrderService.get(orderItem.getOrderId());
    if (order.isCancelable()) {
      val allCanceled = purchaseOrderItemService.getAll(orderItem.getOrderId()).stream()
        .allMatch(item -> item.getStatus() == PurchaseOrderItemStatusKind.CANCELED);
      if (allCanceled) {
        purchaseOrderService.cancel(
          PurchaseOrderRequests.CancelRequest.builder()
            .id(orderItem.getOrderId())
            .build()
        );
      }
    }
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderItemEvents.ReceivedEvent.CHANNEL)
  public void onOrderItemReceived(PurchaseOrderItemEvents.ReceivedEvent event) {
    if (event.isCompleted()) {
      val orderItem = purchaseOrderItemService.get(event.getId());
      val orderId = orderItem.getOrderId();

      val received = purchaseOrderItemService.getAll(orderId).stream()
        .allMatch(item -> item.getStatus() == PurchaseOrderItemStatusKind.RECEIVED);

      if (received) {
        purchaseOrderService.receive(
          PurchaseOrderRequests.ReceiveRequest.builder()
            .id(orderId)
            .build()
        );
      }

    }
  }

}

