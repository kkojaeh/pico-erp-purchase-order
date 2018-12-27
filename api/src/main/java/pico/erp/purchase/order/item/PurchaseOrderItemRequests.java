package pico.erp.purchase.order.item;

import java.math.BigDecimal;
import java.util.List;
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
import pico.erp.purchase.request.item.PurchaseRequestItemId;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.data.UnitKind;

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

    @NotNull
    UnitKind unit;

    @NotNull
    @Min(0)
    BigDecimal unitCost;

    @Size(max = TypeDefinitions.REMARK_LENGTH)
    String remark;

    @Valid
    @NotNull
    ProjectId projectId;

    @Valid
    PurchaseRequestItemId requestItemId;

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


  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class DetermineRequest {

    @Valid
    @NotNull
    PurchaseOrderItemId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class SendRequest {

    @Valid
    @NotNull
    PurchaseOrderItemId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class ReceiveRequest {

    @Valid
    @NotNull
    PurchaseOrderItemId id;

    BigDecimal quantity;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class RejectRequest {

    @Valid
    @NotNull
    PurchaseOrderItemId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class CancelRequest {

    @Valid
    @NotNull
    PurchaseOrderItemId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class GenerateRequest {

    @Valid
    @NotNull
    PurchaseOrderId id;

    @Size(min = 1)
    @NotNull
    List<PurchaseRequestItemId> requestItemIds;

  }

}
