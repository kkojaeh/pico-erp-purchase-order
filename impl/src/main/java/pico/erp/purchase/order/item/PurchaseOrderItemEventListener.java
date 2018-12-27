package pico.erp.purchase.order.item;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pico.erp.purchase.order.PurchaseOrderEvents;
import pico.erp.purchase.order.PurchaseOrderRequests;
import pico.erp.purchase.order.PurchaseOrderService;
import pico.erp.purchase.request.item.PurchaseRequestItemEvents;
import pico.erp.purchase.request.item.PurchaseRequestItemRequests;
import pico.erp.purchase.request.item.PurchaseRequestItemService;
import pico.erp.purchase.request.item.PurchaseRequestItemStatusKind;

@SuppressWarnings("unused")
@Component
public class PurchaseOrderItemEventListener {

  private static final String LISTENER_NAME = "listener.purchase-order-item-event-listener";

  @Autowired
  private PurchaseOrderService purchaseOrderService;

  @Autowired
  private PurchaseOrderItemService purchaseOrderItemService;

  @Lazy
  @Autowired
  private PurchaseRequestItemService purchaseRequestItemService;

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderEvents.CanceledEvent.CHANNEL)
  public void onOrderCanceled(PurchaseOrderEvents.CanceledEvent event) {
    val orderId = event.getPurchaseOrderId();

    purchaseOrderItemService.getAll(orderId).forEach(item -> {
      purchaseOrderItemService.cancel(
        PurchaseOrderItemRequests.CancelRequest.builder()
          .id(item.getId())
          .build()
      );
    });
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderEvents.DeterminedEvent.CHANNEL)
  public void onOrderDetermined(PurchaseOrderEvents.DeterminedEvent event) {
    val orderId = event.getPurchaseOrderId();

    purchaseOrderItemService.getAll(orderId).forEach(item -> {
      purchaseOrderItemService.determine(
        PurchaseOrderItemRequests.DetermineRequest.builder()
          .id(item.getId())
          .build()
      );
    });
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderEvents.GeneratedEvent.CHANNEL)
  public void onOrderGenerated(PurchaseOrderEvents.GeneratedEvent event) {
    purchaseOrderItemService.generate(
      PurchaseOrderItemRequests.GenerateRequest.builder()
        .id(event.getPurchaseOrderId())
        .requestItemIds(event.getRequestItemIds())
        .build()
    );
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderItemEvents.CanceledEvent.CHANNEL)
  public void onOrderItemCanceled(PurchaseOrderItemEvents.CanceledEvent event) {
    val item = purchaseOrderItemService.get(event.getPurchaseOrderItemId());
    val requestItemId = item.getRequestItemId();
    if (requestItemId != null) {
      val requestItem = purchaseRequestItemService.get(item.getRequestItemId());
      if (requestItem.getStatus() == PurchaseRequestItemStatusKind.IN_PROGRESS) {
        purchaseRequestItemService.cancelProgress(
          PurchaseRequestItemRequests.CancelProgressRequest.builder()
            .id(requestItemId)
            .build()
        );
      }
    }
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderItemEvents.CreatedEvent.CHANNEL)
  public void onOrderItemCreated(PurchaseOrderItemEvents.CreatedEvent event) {
    val item = purchaseOrderItemService.get(event.getPurchaseOrderItemId());
    val requestItemId = item.getRequestItemId();
    if (requestItemId != null) {
      purchaseRequestItemService.plan(
        PurchaseRequestItemRequests.PlanRequest.builder()
          .id(requestItemId)
          .build()
      );
    }
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderItemEvents.RejectedEvent.CHANNEL)
  public void onOrderItemReceived(PurchaseOrderItemEvents.ReceivedEvent event) {
    if (event.isCompleted()) {
      val item = purchaseOrderItemService.get(event.getPurchaseOrderItemId());
      val requestItemId = item.getRequestItemId();
      if (requestItemId != null) {
        purchaseRequestItemService.complete(
          PurchaseRequestItemRequests.CompleteRequest.builder()
            .id(requestItemId)
            .build()
        );
      }
    }
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderItemEvents.RejectedEvent.CHANNEL)
  public void onOrderItemRejected(PurchaseOrderItemEvents.RejectedEvent event) {
    val item = purchaseOrderItemService.get(event.getPurchaseOrderItemId());
    val requestItemId = item.getRequestItemId();
    if (requestItemId != null) {
      purchaseRequestItemService.cancelProgress(
        PurchaseRequestItemRequests.CancelProgressRequest.builder()
          .id(requestItemId)
          .build()
      );
    }
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderItemEvents.SentEvent.CHANNEL)
  public void onOrderItemSent(PurchaseOrderItemEvents.SentEvent event) {
    val item = purchaseOrderItemService.get(event.getPurchaseOrderItemId());
    val requestItemId = item.getRequestItemId();
    if (requestItemId != null) {
      purchaseRequestItemService.progress(
        PurchaseRequestItemRequests.ProgressRequest.builder()
          .id(requestItemId)
          .build()
      );
    }
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderEvents.RejectedEvent.CHANNEL)
  public void onOrderRejected(PurchaseOrderEvents.RejectedEvent event) {
    val orderId = event.getPurchaseOrderId();

    purchaseOrderItemService.getAll(orderId).forEach(item -> {
      purchaseOrderItemService.reject(
        PurchaseOrderItemRequests.RejectRequest.builder()
          .id(item.getId())
          .build()
      );
    });
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderEvents.SentEvent.CHANNEL)
  public void onOrderSent(PurchaseOrderEvents.SentEvent event) {
    val orderId = event.getPurchaseOrderId();

    purchaseOrderItemService.getAll(orderId).forEach(item -> {
      purchaseOrderItemService.send(
        PurchaseOrderItemRequests.SendRequest.builder()
          .id(item.getId())
          .build()
      );
    });
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseRequestItemEvents.CanceledEvent.CHANNEL)
  public void onRequestItemCanceled(PurchaseRequestItemEvents.CanceledEvent event) {
    val orderItem = purchaseOrderItemService.get(event.getPurchaseRequestItemId());
    if (orderItem.isCancelable()) {
      val size = purchaseOrderItemService.getAll(orderItem.getOrderId()).size();
      if (size > 1) {
        purchaseOrderItemService.cancel(
          PurchaseOrderItemRequests.CancelRequest.builder()
            .id(orderItem.getId())
            .build()
        );
      } else {
        purchaseOrderService.cancel(
          PurchaseOrderRequests.CancelRequest.builder()
            .id(orderItem.getOrderId())
            .build()
        );
      }
    }
  }


}
