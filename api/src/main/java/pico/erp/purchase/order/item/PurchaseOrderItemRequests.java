package pico.erp.purchase.order.item;

import java.math.BigDecimal;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.item.ItemId;
import pico.erp.item.spec.ItemSpecId;
import pico.erp.project.ProjectId;
import pico.erp.purchase.order.PurchaseOrderId;
import pico.erp.shared.TypeDefinitions;

public interface PurchaseOrderItemRequests {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class CreateRequest {

    @Valid
    @NotNull
    PurchaseOrderItemId id;

    @Valid
    PurchaseOrderId orderId;

    @Valid
    @NotNull
    ItemId itemId;

    @Valid
    ItemSpecId itemSpecId;

    @NotNull
    @Min(0)
    BigDecimal quantity;

    BigDecimal estimatedUnitCost;

    @NotNull
    @Min(0)
    BigDecimal unitCost;

    @Size(max = TypeDefinitions.REMARK_LENGTH)
    String remark;

    @Valid
    @NotNull
    ProjectId projectId;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class UpdateRequest {

    @Valid
    @NotNull
    PurchaseOrderItemId id;

    @Valid
    ItemSpecId itemSpecId;

    BigDecimal estimatedUnitCost;

    @NotNull
    @Min(0)
    BigDecimal unitCost;

    @NotNull
    @Min(0)
    BigDecimal quantity;

    @Size(max = TypeDefinitions.REMARK_LENGTH)
    String remark;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class DeleteRequest {

    @Valid
    @NotNull
    PurchaseOrderItemId id;

  }
}
