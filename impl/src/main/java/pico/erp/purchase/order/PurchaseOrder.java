package pico.erp.purchase.order;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Arrays;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import pico.erp.company.CompanyId;
import pico.erp.delivery.DeliveryId;
import pico.erp.document.DocumentId;
import pico.erp.purchase.order.PurchaseOrderEvents.DeterminedEvent;
import pico.erp.shared.data.Address;
import pico.erp.user.UserId;

/**
 * 주문 접수
 */
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseOrder implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  PurchaseOrderId id;

  PurchaseOrderCode code;

  OffsetDateTime dueDate;

  CompanyId supplierId;

  CompanyId receiverId;

  Address receiveAddress;

  String remark;

  UserId chargerId;

  OffsetDateTime determinedDate;

  OffsetDateTime receivedDate;

  OffsetDateTime sentDate;

  OffsetDateTime rejectedDate;

  OffsetDateTime canceledDate;

  PurchaseOrderStatusKind status;

  String rejectedReason;

  DocumentId draftId;

  DeliveryId deliveryId;

  public PurchaseOrder() {

  }

  public PurchaseOrderMessages.Create.Response apply(
    PurchaseOrderMessages.Create.Request request) {
    this.id = request.getId();
    this.dueDate = request.getDueDate();
    this.supplierId = request.getSupplierId();
    this.receiverId = request.getReceiverId();
    this.receiveAddress = request.getReceiveAddress();
    this.remark = request.getRemark();
    this.status = PurchaseOrderStatusKind.DRAFT;
    this.chargerId = request.getChargerId();
    this.code = request.getCodeGenerator().generate(this);
    return new PurchaseOrderMessages.Create.Response(
      Arrays.asList(new PurchaseOrderEvents.CreatedEvent(this.id))
    );
  }

  public PurchaseOrderMessages.Update.Response apply(
    PurchaseOrderMessages.Update.Request request) {
    if (!isUpdatable()) {
      throw new PurchaseOrderExceptions.CannotUpdateException();
    }
    this.dueDate = request.getDueDate();
    this.supplierId = request.getSupplierId();
    this.receiverId = request.getReceiverId();
    this.receiveAddress = request.getReceiveAddress();
    this.chargerId = request.getChargerId();
    this.remark = request.getRemark();
    return new PurchaseOrderMessages.Update.Response(
      Arrays.asList(new PurchaseOrderEvents.UpdatedEvent(this.id))
    );
  }

  public PurchaseOrderMessages.Determine.Response apply(
    PurchaseOrderMessages.Determine.Request request) {
    if (!isDeterminable()) {
      throw new PurchaseOrderExceptions.CannotDetermineException();
    }
    this.draftId = request.getDraftId();
    this.deliveryId = request.getDeliveryId();
    this.status = PurchaseOrderStatusKind.DETERMINED;
    this.determinedDate = OffsetDateTime.now();
    return new PurchaseOrderMessages.Determine.Response(
      Arrays.asList(new DeterminedEvent(this.id))
    );
  }

  public PurchaseOrderMessages.Cancel.Response apply(
    PurchaseOrderMessages.Cancel.Request request) {
    if (!isCancelable()) {
      throw new PurchaseOrderExceptions.CannotCancelException();
    }
    this.status = PurchaseOrderStatusKind.CANCELED;
    this.canceledDate = OffsetDateTime.now();
    return new PurchaseOrderMessages.Cancel.Response(
      Arrays.asList(new PurchaseOrderEvents.CanceledEvent(this.id))
    );
  }

  public PurchaseOrderMessages.Receive.Response apply(
    PurchaseOrderMessages.Receive.Request request) {
    if (!isReceivable()) {
      throw new PurchaseOrderExceptions.CannotReceiveException();
    }
    this.status = PurchaseOrderStatusKind.RECEIVED;
    this.receivedDate = OffsetDateTime.now();
    return new PurchaseOrderMessages.Receive.Response(
      Arrays.asList(new PurchaseOrderEvents.ReceivedEvent(this.id))
    );
  }

  public PurchaseOrderMessages.Send.Response apply(
    PurchaseOrderMessages.Send.Request request) {
    if (!isSendable()) {
      throw new PurchaseOrderExceptions.CannotSendException();
    }
    this.status = PurchaseOrderStatusKind.SENT;
    this.sentDate = OffsetDateTime.now();
    return new PurchaseOrderMessages.Send.Response(
      Arrays.asList(new PurchaseOrderEvents.SentEvent(this.id))
    );
  }

  public PurchaseOrderMessages.Reject.Response apply(
    PurchaseOrderMessages.Reject.Request request) {
    if (!isRejectable()) {
      throw new PurchaseOrderExceptions.CannotRejectException();
    }
    this.status = PurchaseOrderStatusKind.REJECTED;
    this.rejectedDate = OffsetDateTime.now();
    this.rejectedReason = request.getRejectedReason();
    return new PurchaseOrderMessages.Reject.Response(
      Arrays.asList(new PurchaseOrderEvents.RejectedEvent(this.id))
    );
  }

  public boolean isCancelable() {
    return status.isCancelable();
  }

  public boolean isDeterminable() {
    return status.isDeterminable();
  }

  public boolean isReceivable() {
    return status.isReceivable();
  }

  public boolean isRejectable() {
    return status.isRejectable();
  }

  public boolean isSendable() {
    return status.isSendable();
  }

  public boolean isUpdatable() {
    return status.isUpdatable();
  }

  public boolean isPrintable() {
    return status.isPrintable();
  }

}
