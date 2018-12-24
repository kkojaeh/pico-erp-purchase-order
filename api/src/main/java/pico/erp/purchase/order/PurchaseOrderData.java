package pico.erp.purchase.order;

import java.time.OffsetDateTime;
import lombok.Data;
import pico.erp.company.CompanyId;
import pico.erp.shared.data.Address;
import pico.erp.shared.data.Auditor;

@Data
public class PurchaseOrderData {

  PurchaseOrderId id;

  PurchaseOrderCode code;

  Auditor charger;

  String rejectedReason;

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

  String remark;

}
