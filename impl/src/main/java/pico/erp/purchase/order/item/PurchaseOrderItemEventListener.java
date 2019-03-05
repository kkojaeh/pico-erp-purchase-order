package pico.erp.purchase.order.item;

import java.math.BigDecimal;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pico.erp.purchase.order.PurchaseOrderEvents;
import pico.erp.purchase.order.PurchaseOrderService;
import pico.erp.purchase.request.PurchaseRequestEvents;
import pico.erp.purchase.request.PurchaseRequestRequests;
import pico.erp.purchase.request.PurchaseRequestService;

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
  private PurchaseRequestService purchaseRequestService;

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderEvents.CanceledEvent.CHANNEL)
  public void onOrderCanceled(PurchaseOrderEvents.CanceledEvent event) {
    val orderId = event.getId();

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
    val orderId = event.getId();

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
        .id(event.getId())
        .requestIds(event.getRequestIds())
        .build()
    );
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderItemEvents.CanceledEvent.CHANNEL)
  public void onOrderItemCanceled(PurchaseOrderItemEvents.CanceledEvent event) {
    val item = purchaseOrderItemService.get(event.getId());
    val requestId = item.getRequestId();
    if (requestId != null) {
      val request = purchaseRequestService.get(requestId);
      if (request.isProgressCancelable()) {
        purchaseRequestService.cancelProgress(
          PurchaseRequestRequests.CancelProgressRequest.builder()
            .id(requestId)
            .build()
        );
      }
    }
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderItemEvents.CreatedEvent.CHANNEL)
  public void onOrderItemCreated(PurchaseOrderItemEvents.CreatedEvent event) {
    val item = purchaseOrderItemService.get(event.getId());
    val requestId = item.getRequestId();
    if (requestId != null) {
      purchaseRequestService.plan(
        PurchaseRequestRequests.PlanRequest.builder()
          .id(requestId)
          .build()
      );
    }
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderItemEvents.RejectedEvent.CHANNEL)
  public void onOrderItemReceived(PurchaseOrderItemEvents.ReceivedEvent event) {
    val item = purchaseOrderItemService.get(event.getId());
    val requestId = item.getRequestId();
    if (requestId != null) {
      purchaseRequestService.progress(
        PurchaseRequestRequests.ProgressRequest.builder()
          .id(requestId)
          .progressedQuantity(event.getQuantity())
          .build()
      );
      if (event.isCompleted()) {
        purchaseRequestService.complete(
          PurchaseRequestRequests.CompleteRequest.builder()
            .id(requestId)
            .build()
        );
      }
    }

  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderItemEvents.RejectedEvent.CHANNEL)
  public void onOrderItemRejected(PurchaseOrderItemEvents.RejectedEvent event) {
    val item = purchaseOrderItemService.get(event.getId());
    val requestId = item.getRequestId();
    if (requestId != null) {
      purchaseRequestService.cancelProgress(
        PurchaseRequestRequests.CancelProgressRequest.builder()
          .id(requestId)
          .build()
      );
    }
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderItemEvents.SentEvent.CHANNEL)
  public void onOrderItemSent(PurchaseOrderItemEvents.SentEvent event) {
    val item = purchaseOrderItemService.get(event.getId());
    val requestId = item.getRequestId();
    if (requestId != null) {
      purchaseRequestService.progress(
        PurchaseRequestRequests.ProgressRequest.builder()
          .id(requestId)
          .progressedQuantity(BigDecimal.ZERO)
          .build()
      );
    }
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderEvents.RejectedEvent.CHANNEL)
  public void onOrderRejected(PurchaseOrderEvents.RejectedEvent event) {
    val orderId = event.getId();

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
    val orderId = event.getId();

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
    + PurchaseRequestEvents.CanceledEvent.CHANNEL)
  public void onRequestCanceled(PurchaseRequestEvents.CanceledEvent event) {
    val orderItem = purchaseOrderItemService.get(event.getId());
    if (orderItem.isCancelable()) {
      purchaseOrderItemService.cancel(
        PurchaseOrderItemRequests.CancelRequest.builder()
          .id(orderItem.getId())
          .build()
      );
    }
  }

}
