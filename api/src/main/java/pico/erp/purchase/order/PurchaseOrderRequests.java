package pico.erp.purchase.order;

import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.company.CompanyId;
import pico.erp.project.ProjectId;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.data.Address;
import pico.erp.user.UserId;
import pico.erp.warehouse.location.site.SiteId;
import pico.erp.warehouse.location.station.StationId;

public interface PurchaseOrderRequests {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class CreateRequest {

    @Valid
    @NotNull
    PurchaseOrderId id;

    @Future
    @NotNull
    OffsetDateTime dueDate;

    @Valid
    @NotNull
    CompanyId receiverId;

    @Valid
    @NotNull
    CompanyId supplierId;

    @Valid
    @NotNull
    Address receiveAddress;

    @Size(max = TypeDefinitions.REMARK_LENGTH)
    String remark;

    @Valid
    @NotNull
    UserId chargerId;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class UpdateRequest {

    @Valid
    @NotNull
    PurchaseOrderId id;

    @Future
    @NotNull
    OffsetDateTime dueDate;

    @Valid
    @NotNull
    CompanyId receiverId;

    @Valid
    @NotNull
    CompanyId supplierId;

    @Valid
    @NotNull
    Address receiveAddress;

    @Size(max = TypeDefinitions.REMARK_LENGTH)
    String remark;

    @Valid
    @NotNull
    UserId chargerId;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class DetermineRequest {

    @Valid
    @NotNull
    PurchaseOrderId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class SendRequest {

    @Valid
    @NotNull
    PurchaseOrderId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class ReceiveRequest {

    @Valid
    @NotNull
    PurchaseOrderId id;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class RejectRequest {

    @Valid
    @NotNull
    PurchaseOrderId id;

    @Size(max = TypeDefinitions.REMARK_LENGTH)
    String rejectedReason;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  class CancelRequest {

    @Valid
    @NotNull
    PurchaseOrderId id;

  }

}
