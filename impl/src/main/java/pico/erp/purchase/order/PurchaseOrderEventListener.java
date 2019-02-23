package pico.erp.purchase.order;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pico.erp.company.CompanyService;
import pico.erp.delivery.DeliveryId;
import pico.erp.delivery.DeliveryRequests;
import pico.erp.delivery.DeliveryService;
import pico.erp.document.DocumentId;
import pico.erp.document.DocumentRequests;
import pico.erp.document.DocumentService;
import pico.erp.purchase.order.item.PurchaseOrderItemEvents;
import pico.erp.purchase.order.item.PurchaseOrderItemService;
import pico.erp.purchase.order.item.PurchaseOrderItemStatusKind;
import pico.erp.purchase.request.PurchaseRequestRequests;
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
    + PurchaseOrderEvents.DeterminedEvent.CHANNEL)
  public void onOrderDetermined(PurchaseOrderEvents.DeterminedEvent event) {
    val id = event.getId();
    val order = purchaseOrderService.get(id);
    if (order.getDraftId() != null) {
      documentService.delete(
        new DocumentRequests.DeleteRequest(order.getDraftId())
      );
    }
    val supplier = companyService.get(order.getSupplierId());
    val name = String.format("PO-%s-%s-%s",
      order.getCode().getValue(),
      supplier.getName(),
      DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now())
    );
    val draftId = DocumentId.generate();
    val draft = documentService.create(
      DocumentRequests.CreateRequest.builder()
        .id(draftId)
        .subjectId(PurchaseOrderDraftDocumentSubjectDefinition.ID)
        .name(name)
        .key(id)
        .creatorId(order.getChargerId())
        .build()
    );
    val deliveryId = DeliveryId.generate();
    deliveryService.create(
      DeliveryRequests.CreateRequest.builder()
        .id(deliveryId)
        .subjectId(PurchaseOrderDraftDeliverySubjectDefinition.ID)
        .key(id)
        .build()
    );
    purchaseOrderService.prepareSend(
      PurchaseOrderRequests.PrepareSendRequest.builder()
        .id(id)
        .draftId(draftId)
        .deliveryId(deliveryId)
        .build()
    );

  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseOrderItemEvents.ReceivedEvent.CHANNEL)
  public void onOrderItemReceived(PurchaseOrderItemEvents.ReceivedEvent event) {
    if (event.isCompleted()) {
      val orderItem = purchaseOrderItemService.get(event.getId());
      val orderId = orderItem.getOrderId();

      val requestId = orderItem.getRequestId();
      if (requestId != null) {
        purchaseRequestService.complete(
          PurchaseRequestRequests.CompleteRequest.builder()
            .id(requestId)
            .build()
        );
      }

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

