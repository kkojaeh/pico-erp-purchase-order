package pico.erp.purchase.order.item;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
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

  BigDecimal estimatedUnitCost;

  BigDecimal unitCost;

  UnitKind unit;

  String remark;

  ProjectData project;


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
    this.unit = request.getUnit();
    this.estimatedUnitCost = request.getEstimatedUnitCost();
    this.unitCost = request.getUnitCost();
    this.remark = request.getRemark();
    this.project = request.getProject();

    return new PurchaseOrderItemMessages.Create.Response(
      Arrays.asList(new PurchaseOrderItemEvents.CreatedEvent(this.id))
    );
  }

  public PurchaseOrderItemMessages.Update.Response apply(
    PurchaseOrderItemMessages.Update.Request request) {
    if (!this.order.isUpdatable()) {
      throw new PurchaseOrderItemExceptions.CannotUpdateException();
    }
    this.itemSpec = request.getItemSpec();
    this.estimatedUnitCost = request.getEstimatedUnitCost();
    this.unitCost = request.getUnitCost();
    this.quantity = request.getQuantity();
    this.remark = request.getRemark();
    return new PurchaseOrderItemMessages.Update.Response(
      Arrays.asList(new PurchaseOrderItemEvents.UpdatedEvent(this.id))
    );
  }

  public PurchaseOrderItemMessages.Delete.Response apply(
    PurchaseOrderItemMessages.Delete.Request request) {
    if (!this.order.isUpdatable()) {
      throw new PurchaseOrderItemExceptions.CannotDeleteException();
    }
    return new PurchaseOrderItemMessages.Delete.Response(
      Arrays.asList(new PurchaseOrderItemEvents.DeletedEvent(this.id))
    );
  }


}
