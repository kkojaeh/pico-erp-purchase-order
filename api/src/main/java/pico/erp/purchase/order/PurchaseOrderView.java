package pico.erp.purchase.order;

import java.time.OffsetDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.company.CompanyId;
import pico.erp.item.ItemId;
import pico.erp.project.ProjectId;
import pico.erp.shared.data.Address;
import pico.erp.shared.data.Auditor;
import pico.erp.user.UserId;

@Data
public class PurchaseOrderView {

  PurchaseOrderId id;

  PurchaseOrderCode code;

  Auditor charger;

  CompanyId supplierId;

  CompanyId receiverId;

  Address receiveAddress;

  OffsetDateTime dueDate;

  OffsetDateTime determinedDate;

  OffsetDateTime receivedDate;

  OffsetDateTime sentDate;

  OffsetDateTime rejectedDate;

  OffsetDateTime canceledDate;

  PurchaseOrderStatusKind status;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Filter {

    String code;

    CompanyId receiverId;

    CompanyId supplierId;

    UserId chargerId;

    ProjectId projectId;

    ItemId itemId;

    Set<PurchaseOrderStatusKind> statuses;

    OffsetDateTime startDueDate;

    OffsetDateTime endDueDate;

  }

}
