package pico.erp.purchase.order.item;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.item.ItemId;
import pico.erp.item.spec.ItemSpecId;
import pico.erp.shared.data.UnitKind;

public interface PurchaseOrderItemUnitCostEstimator {

  BigDecimal estimate(PurchaseOrderItemContext context);

  interface PurchaseOrderItemContext {

    ItemId getItemId();

    ItemSpecId getItemSpecId();

    BigDecimal getQuantity();

    UnitKind getUnit();

  }

  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  class PurchaseOrderItemContextImpl implements PurchaseOrderItemContext {

    ItemId itemId;

    ItemSpecId itemSpecId;

    BigDecimal quantity;

    UnitKind unit;

  }
}
