package pico.erp.purchase.order;


import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pico.erp.company.CompanyId;
import pico.erp.delivery.DeliveryId;
import pico.erp.document.DocumentId;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.data.Address;
import pico.erp.shared.data.Auditor;
import pico.erp.user.UserId;

@Entity(name = "PurchaseOrder")
@Table(name = "PCO_PURCHASE_ORDER")
@Data
@EqualsAndHashCode(of = "id")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseOrderEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @EmbeddedId
  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "ID", length = TypeDefinitions.UUID_BINARY_LENGTH))
  })
  PurchaseOrderId id;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "CODE", length = TypeDefinitions.CODE_LENGTH))
  })
  PurchaseOrderCode code;

  OffsetDateTime dueDate;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "RECEIVER_ID", length = TypeDefinitions.ID_LENGTH))
  })
  CompanyId receiverId;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "SUPPLIER_ID", length = TypeDefinitions.ID_LENGTH))
  })
  CompanyId supplierId;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "postalCode", column = @Column(name = "RECEIVE_ADDRESS_POSTAL_CODE", length = TypeDefinitions.ADDRESS_POSTAL_LENGTH)),
    @AttributeOverride(name = "street", column = @Column(name = "RECEIVE_ADDRESS_STREET", length = TypeDefinitions.ADDRESS_STREET_LENGTH)),
    @AttributeOverride(name = "detail", column = @Column(name = "RECEIVE_ADDRESS_DETAIL", length = TypeDefinitions.ADDRESS_DETAIL_LENGTH))
  })
  Address receiveAddress;

  @Column(length = TypeDefinitions.REMARK_LENGTH)
  String remark;

  @Column(length = TypeDefinitions.REMARK_LENGTH)
  String rejectedReason;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "CHARGER_ID", length = TypeDefinitions.ID_LENGTH))
  })
  UserId chargerId;

  OffsetDateTime determinedDate;

  OffsetDateTime receivedDate;

  OffsetDateTime sentDate;

  OffsetDateTime rejectedDate;

  OffsetDateTime canceledDate;

  @Column(length = TypeDefinitions.ENUM_LENGTH)
  @Enumerated(EnumType.STRING)
  PurchaseOrderStatusKind status;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "CREATED_BY_ID", updatable = false, length = TypeDefinitions.ID_LENGTH)),
    @AttributeOverride(name = "name", column = @Column(name = "CREATED_BY_NAME", updatable = false, length = TypeDefinitions.NAME_LENGTH))
  })
  @CreatedBy
  Auditor createdBy;

  @Column(updatable = false)
  OffsetDateTime createdDate;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "LAST_MODIFIED_BY_ID", length = TypeDefinitions.ID_LENGTH)),
    @AttributeOverride(name = "name", column = @Column(name = "LAST_MODIFIED_BY_NAME", length = TypeDefinitions.NAME_LENGTH))
  })
  @LastModifiedBy
  Auditor lastModifiedBy;

  OffsetDateTime lastModifiedDate;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "DRAFT_ID", length = TypeDefinitions.UUID_BINARY_LENGTH))
  })
  DocumentId draftId;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "DELIVERY_ID", length = TypeDefinitions.UUID_BINARY_LENGTH))
  })
  DeliveryId deliveryId;

  @PrePersist
  private void onCreate() {
    createdDate = OffsetDateTime.now();
    lastModifiedDate = OffsetDateTime.now();
  }

  @PreUpdate
  private void onUpdate() {
    lastModifiedDate = OffsetDateTime.now();
  }

}
