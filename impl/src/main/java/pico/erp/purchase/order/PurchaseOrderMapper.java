package pico.erp.purchase.order;

import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import pico.erp.item.ItemService;
import pico.erp.item.spec.ItemSpecService;
import pico.erp.purchase.order.PurchaseOrderRequests.DetermineRequest;
import pico.erp.purchase.order.PurchaseOrderRequests.ReceiveRequest;
import pico.erp.purchase.order.PurchaseOrderRequests.SendRequest;
import pico.erp.shared.data.Auditor;

@Mapper
public abstract class PurchaseOrderMapper {

  @Autowired
  protected AuditorAware<Auditor> auditorAware;

  @Lazy
  @Autowired
  protected ItemService itemService;

  @Lazy
  @Autowired
  protected ItemSpecService itemSpecService;

  @Autowired
  protected PurchaseOrderCodeGenerator purchaseOrderCodeGenerator;

  @Lazy
  @Autowired
  private PurchaseOrderRepository purchaseOrderRepository;


  @Mappings({
    @Mapping(target = "createdBy", ignore = true),
    @Mapping(target = "createdDate", ignore = true),
    @Mapping(target = "lastModifiedBy", ignore = true),
    @Mapping(target = "lastModifiedDate", ignore = true)
  })
  public abstract PurchaseOrderEntity jpa(PurchaseOrder data);

  public PurchaseOrder jpa(PurchaseOrderEntity entity) {
    return PurchaseOrder.builder()
      .id(entity.getId())
      .code(entity.getCode())
      .dueDate(entity.getDueDate())
      .supplierId(entity.getSupplierId())
      .receiverId(entity.getReceiverId())
      .receiveAddress(entity.getReceiveAddress())
      .remark(entity.getRemark())
      .chargerId(entity.getChargerId())
      .determinedDate(entity.getDeterminedDate())
      .receivedDate(entity.getReceivedDate())
      .sentDate(entity.getSentDate())
      .rejectedDate(entity.getRejectedDate())
      .canceledDate(entity.getCanceledDate())
      .status(entity.getStatus())
      .rejectedReason(entity.getRejectedReason())
      .draftId(entity.getDraftId())
      .deliveryId(entity.getDeliveryId())
      .build();
  }

  public PurchaseOrder map(PurchaseOrderId purchaseOrderId) {
    return Optional.ofNullable(purchaseOrderId)
      .map(id -> purchaseOrderRepository.findBy(id)
        .orElseThrow(PurchaseOrderExceptions.NotFoundException::new)
      )
      .orElse(null);
  }

  public abstract PurchaseOrderData map(PurchaseOrder purchaseOrder);

  @Mappings({
    @Mapping(target = "codeGenerator", expression = "java(purchaseOrderCodeGenerator)")
  })
  public abstract PurchaseOrderMessages.Create.Request map(
    PurchaseOrderRequests.CreateRequest request);

  public abstract PurchaseOrderMessages.Update.Request map(
    PurchaseOrderRequests.UpdateRequest request);

  @Mappings({
    @Mapping(target = "draftId", ignore = true),
    @Mapping(target = "deliveryId", ignore = true)
  })
  public abstract PurchaseOrderMessages.Determine.Request map(
    DetermineRequest request);

  public abstract PurchaseOrderMessages.Send.Request map(
    SendRequest request);

  public abstract PurchaseOrderMessages.Receive.Request map(
    ReceiveRequest request);

  public abstract PurchaseOrderMessages.Cancel.Request map(
    PurchaseOrderRequests.CancelRequest request);

  public abstract PurchaseOrderMessages.Reject.Request map(
    PurchaseOrderRequests.RejectRequest request);

  public abstract void pass(PurchaseOrderEntity from, @MappingTarget PurchaseOrderEntity to);


}


