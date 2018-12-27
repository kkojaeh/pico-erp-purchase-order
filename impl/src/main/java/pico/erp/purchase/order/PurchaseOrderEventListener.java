package pico.erp.purchase.order;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pico.erp.purchase.order.item.PurchaseOrderItemEvents;
import pico.erp.purchase.order.item.PurchaseOrderItemService;
import pico.erp.purchase.order.item.PurchaseOrderItemStatusKind;

@SuppressWarnings("unused")
@Component
public class PurchaseOrderEventListener {

  private static final String LISTENER_NAME = "listener.purchase-order-event-listener";

  @Autowired
  private PurchaseOrderItemService purchaseOrderItemService;

  @Autowired
  private PurchaseOrderService purchaseOrderService;

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderItemEvents.ReceivedEvent.CHANNEL)
  public void onOrderItemReceived(PurchaseOrderItemEvents.ReceivedEvent event) {
    if (event.isCompleted()) {
      val orderItem = purchaseOrderItemService.get(event.getPurchaseOrderItemId());
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

