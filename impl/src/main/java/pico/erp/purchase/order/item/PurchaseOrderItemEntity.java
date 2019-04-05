package pico.erp.purchase.order.item;


import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Index;
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
import pico.erp.item.ItemId;
import pico.erp.item.spec.ItemSpecCode;
import pico.erp.item.spec.ItemSpecId;
import pico.erp.project.ProjectId;
import pico.erp.purchase.order.PurchaseOrderId;
import pico.erp.purchase.request.PurchaseRequestId;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.data.Auditor;
import pico.erp.shared.data.UnitKind;

@Entity(name = "PurchaseOrderItem")
@Table(name = "PCO_PURCHASE_ORDER_ITEM", indexes = {
  @Index(columnList = "ORDER_ID"),
  @Index(columnList = "REQUEST_ID")
})
@Data
@EqualsAndHashCode(of = "id")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseOrderItemEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @EmbeddedId
  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "ID", length = TypeDefinitions.UUID_BINARY_LENGTH))
  })
  PurchaseOrderItemId id;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "ORDER_ID", length = TypeDefinitions.UUID_BINARY_LENGTH))
  })
  PurchaseOrderId orderId;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "PROJECT_ID", length = TypeDefinitions.UUID_BINARY_LENGTH))
  })
  ProjectId projectId;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "ITEM_ID", length = TypeDefinitions.UUID_BINARY_LENGTH))
  })
  ItemId itemId;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "ITEM_SPEC_ID", length = TypeDefinitions.UUID_BINARY_LENGTH))
  })
  ItemSpecId itemSpecId;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "ITEM_SPEC_CODE", length = TypeDefinitions.CODE_LENGTH))
  })
  ItemSpecCode itemSpecCode;

  @Column(precision = 19, scale = 2)
  BigDecimal quantity;

  @Column(precision = 19, scale = 2)
  BigDecimal receivedQuantity;

  @Column(scale = 2)
  BigDecimal estimatedUnitCost;

  @Column(scale = 2)
  BigDecimal unitCost;

  @Column(length = TypeDefinitions.ENUM_LENGTH)
  @Enumerated(EnumType.STRING)
  UnitKind unit;

  @Column(length = TypeDefinitions.REMARK_LENGTH)
  String remark;

  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "REQUEST_ID", length = TypeDefinitions.UUID_BINARY_LENGTH))
  })
  PurchaseRequestId requestId;

  @Column(length = TypeDefinitions.ENUM_LENGTH)
  @Enumerated(EnumType.STRING)
  PurchaseOrderItemStatusKind status;

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
