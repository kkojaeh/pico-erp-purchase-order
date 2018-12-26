package pico.erp.purchase.order.item;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.item.ItemId;
import pico.erp.item.spec.ItemSpecId;
import pico.erp.project.ProjectId;
import pico.erp.purchase.order.PurchaseOrderId;
import pico.erp.shared.data.UnitKind;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PurchaseOrderItemData {

  PurchaseOrderItemId id;

  PurchaseOrderId orderId;

  ItemId itemId;

  ItemSpecId itemSpecId;

  BigDecimal quantity;

  BigDecimal estimatedUnitCost;

  BigDecimal unitCost;

  UnitKind unit;

  String remark;

  ProjectId projectId;

}
