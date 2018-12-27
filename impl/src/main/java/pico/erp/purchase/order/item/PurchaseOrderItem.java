package pico.erp.purchase.order.item;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import pico.erp.audit.annotation.Audit;
import pico.erp.item.ItemData;
import pico.erp.item.spec.ItemSpecData;
import pico.erp.project.ProjectData;
import pico.erp.purchase.order.PurchaseOrder;
import pico.erp.purchase.order.PurchaseOrderExceptions;
import pico.erp.purchase.order.item.PurchaseOrderItemUnitCostEstimator.PurchaseOrderItemContext;
import pico.erp.purchase.order.item.PurchaseOrderItemUnitCostEstimator.PurchaseOrderItemContextImpl;
import pico.erp.purchase.request.item.PurchaseRequestItemData;
import pico.erp.shared.data.UnitKind;

/**
 * 주문 접수
 */
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Audit(alias = "purchase-order-item")
public class PurchaseOrderItem implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  PurchaseOrderItemId id;

  PurchaseOrder order;

  ItemData item;

  ItemSpecData itemSpec;

  BigDecimal quantity;

  BigDecimal receivedQuantity;

  BigDecimal estimatedUnitCost;

  BigDecimal unitCost;

  UnitKind unit;

  String remark;

  ProjectData project;

  PurchaseRequestItemData requestItem;

  PurchaseOrderItemStatusKind status;

  public PurchaseOrderItem() {
  }

  public PurchaseOrderItemMessages.Create.Response apply(
    PurchaseOrderItemMessages.Create.Request request) {
    if (!request.getOrder().isUpdatable()) {
      throw new PurchaseOrderItemExceptions.CannotCreateException();
    }
    this.id = request.getId();
    this.order = request.getOrder();
    this.item = request.getItem();
    this.itemSpec = request.getItemSpec();
    this.quantity = request.getQuantity();
    this.unitCost = request.getUnitCost();
    this.remark = request.getRemark();
    this.project = request.getProject();
    this.requestItem = request.getRequestItem();
    this.receivedQuantity = BigDecimal.ZERO;
    this.status = PurchaseOrderItemStatusKind.DRAFT;
    this.estimatedUnitCost = request.getUnitCostEstimator().estimate(createContext());
    this.unit = request.getUnit();
    return new PurchaseOrderItemMessages.Create.Response(
      Arrays.asList(new PurchaseOrderItemEvents.CreatedEvent(this.id))
    );
  }

  public PurchaseOrderItemMessages.Update.Response apply(
    PurchaseOrderItemMessages.Update.Request request) {
    if (!this.isUpdatable()) {
      throw new PurchaseOrderItemExceptions.CannotUpdateException();
    }
    this.itemSpec = request.getItemSpec();
    this.unitCost = request.getUnitCost();
    this.quantity = request.getQuantity();
    this.remark = request.getRemark();
    this.estimatedUnitCost = request.getUnitCostEstimator().estimate(createContext());
    return new PurchaseOrderItemMessages.Update.Response(
      Arrays.asList(new PurchaseOrderItemEvents.UpdatedEvent(this.id))
    );
  }

  public PurchaseOrderItemMessages.Delete.Response apply(
    PurchaseOrderItemMessages.Delete.Request request) {
    if (!this.isUpdatable()) {
      throw new PurchaseOrderItemExceptions.CannotDeleteException();
    }
    return new PurchaseOrderItemMessages.Delete.Response(
      Arrays.asList(new PurchaseOrderItemEvents.DeletedEvent(this.id))
    );
  }

  public PurchaseOrderItemMessages.Receive.Response apply(
    PurchaseOrderItemMessages.Receive.Request request) {
    if (!this.isReceivable()) {
      throw new PurchaseOrderItemExceptions.CannotReceiveException();
    }
    this.receivedQuantity = this.receivedQuantity.add(request.getQuantity());
    if (this.receivedQuantity.compareTo(this.quantity) > -1) {
      this.status = PurchaseOrderItemStatusKind.RECEIVED;
    } else {
      this.status = PurchaseOrderItemStatusKind.IN_RECEIVING;
    }
    return new PurchaseOrderItemMessages.Receive.Response(
      Arrays.asList(new PurchaseOrderItemEvents.ReceivedEvent(this.id,
        this.status == PurchaseOrderItemStatusKind.RECEIVED))
    );
  }

  public PurchaseOrderItemMessages.Determine.Response apply(
    PurchaseOrderItemMessages.Determine.Request request) {
    if (!isDeterminable()) {
      throw new PurchaseOrderExceptions.CannotDetermineException();
    }
    this.status = PurchaseOrderItemStatusKind.DETERMINED;
    return new PurchaseOrderItemMessages.Determine.Response(
      Arrays.asList(new PurchaseOrderItemEvents.DeterminedEvent(this.id))
    );
  }

  public PurchaseOrderItemMessages.Cancel.Response apply(
    PurchaseOrderItemMessages.Cancel.Request request) {
    if (!isCancelable()) {
      throw new PurchaseOrderExceptions.CannotCancelException();
    }
    this.status = PurchaseOrderItemStatusKind.CANCELED;
    return new PurchaseOrderItemMessages.Cancel.Response(
      Arrays.asList(new PurchaseOrderItemEvents.CanceledEvent(this.id))
    );
  }

  public PurchaseOrderItemMessages.Send.Response apply(
    PurchaseOrderItemMessages.Send.Request request) {
    if (!isSendable()) {
      throw new PurchaseOrderExceptions.CannotSendException();
    }
    this.status = PurchaseOrderItemStatusKind.SENT;
    return new PurchaseOrderItemMessages.Send.Response(
      Arrays.asList(new PurchaseOrderItemEvents.SentEvent(this.id))
    );
  }

  public PurchaseOrderItemMessages.Reject.Response apply(
    PurchaseOrderItemMessages.Reject.Request request) {
    if (!isRejectable()) {
      throw new PurchaseOrderExceptions.CannotRejectException();
    }
    this.status = PurchaseOrderItemStatusKind.REJECTED;
    return new PurchaseOrderItemMessages.Reject.Response(
      Arrays.asList(new PurchaseOrderItemEvents.RejectedEvent(this.id))
    );
  }

  private PurchaseOrderItemContext createContext() {
    return PurchaseOrderItemContextImpl.builder()
      .itemId(item.getId())
      .itemSpecId(
        Optional.ofNullable(itemSpec)
          .map(ItemSpecData::getId)
          .orElse(null)
      )
      .quantity(quantity)
      .build();
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
}
