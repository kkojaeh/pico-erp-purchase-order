package pico.erp.purchase.order;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.company.CompanyId;
import pico.erp.item.ItemId;
import pico.erp.project.ProjectId;
import pico.erp.shared.data.Address;
import pico.erp.user.UserId;

@Data
public class PurchaseOrderView {

  PurchaseOrderId id;

  PurchaseOrderCode code;

  UserId chargerId;

  CompanyId supplierId;

  CompanyId receiverId;

  Address receiveAddress;

  LocalDateTime dueDate;

  LocalDateTime determinedDate;

  LocalDateTime receivedDate;

  LocalDateTime sentDate;

  LocalDateTime rejectedDate;

  LocalDateTime canceledDate;

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

    LocalDateTime startDueDate;

    LocalDateTime endDueDate;

  }

}
