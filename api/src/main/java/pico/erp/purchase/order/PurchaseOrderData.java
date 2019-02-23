package pico.erp.purchase.order;

import java.time.OffsetDateTime;
import lombok.Data;
import pico.erp.company.CompanyId;
import pico.erp.delivery.DeliveryId;
import pico.erp.document.DocumentId;
import pico.erp.shared.data.Address;
import pico.erp.user.UserId;

@Data
public class PurchaseOrderData {

  PurchaseOrderId id;

  PurchaseOrderCode code;

  UserId chargerId;

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

  boolean cancelable;

  boolean determinable;

  boolean receivable;

  boolean rejectable;

  boolean sendable;

  boolean updatable;

  boolean printable;

  DocumentId draftId;

  DeliveryId deliveryId;


}
