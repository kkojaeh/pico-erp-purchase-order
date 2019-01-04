package pico.erp.purchase.order;

import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import pico.erp.company.CompanyData;
import pico.erp.company.CompanyId;
import pico.erp.company.CompanyService;
import pico.erp.item.ItemData;
import pico.erp.item.ItemId;
import pico.erp.item.ItemService;
import pico.erp.item.spec.ItemSpecData;
import pico.erp.item.spec.ItemSpecId;
import pico.erp.item.spec.ItemSpecService;
import pico.erp.project.ProjectData;
import pico.erp.project.ProjectId;
import pico.erp.project.ProjectService;
import pico.erp.purchase.order.PurchaseOrderRequests.DetermineRequest;
import pico.erp.purchase.order.PurchaseOrderRequests.ReceiveRequest;
import pico.erp.purchase.order.PurchaseOrderRequests.SendRequest;
import pico.erp.shared.data.Auditor;
import pico.erp.user.UserData;
import pico.erp.user.UserId;
import pico.erp.user.UserService;

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
  private CompanyService companyService;

  @Lazy
  @Autowired
  private UserService userService;

  @Lazy
  @Autowired
  private PurchaseOrderRepository purchaseOrderRepository;

  @Lazy
  @Autowired
  private ProjectService projectService;

  protected Auditor auditor(UserId userId) {
    return Optional.ofNullable(userId)
      .map(userService::getAuditor)
      .orElse(null);
  }

  @Mappings({
    @Mapping(target = "receiverId", source = "receiver.id"),
    @Mapping(target = "supplierId", source = "supplier.id"),
    @Mapping(target = "chargerId", source = "charger.id"),
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
      .supplier(map(entity.getSupplierId()))
      .receiver(map(entity.getReceiverId()))
      .receiveAddress(entity.getReceiveAddress())
      .remark(entity.getRemark())
      .charger(map(entity.getChargerId()))
      .determinedDate(entity.getDeterminedDate())
      .receivedDate(entity.getReceivedDate())
      .sentDate(entity.getSentDate())
      .rejectedDate(entity.getRejectedDate())
      .canceledDate(entity.getCanceledDate())
      .status(entity.getStatus())
      .rejectedReason(entity.getRejectedReason())
      .build();
  }

  protected UserData map(UserId userId) {
    return Optional.ofNullable(userId)
      .map(userService::get)
      .orElse(null);
  }

  protected CompanyData map(CompanyId companyId) {
    return Optional.ofNullable(companyId)
      .map(companyService::get)
      .orElse(null);
  }

  protected ProjectData map(ProjectId projectId) {
    return Optional.ofNullable(projectId)
      .map(projectService::get)
      .orElse(null);
  }

  public PurchaseOrder map(PurchaseOrderId purchaseOrderId) {
    return Optional.ofNullable(purchaseOrderId)
      .map(id -> purchaseOrderRepository.findBy(id)
        .orElseThrow(PurchaseOrderExceptions.NotFoundException::new)
      )
      .orElse(null);
  }

  protected ItemData map(ItemId itemId) {
    return Optional.ofNullable(itemId)
      .map(itemService::get)
      .orElse(null);
  }

  protected ItemSpecData map(ItemSpecId itemSpecId) {
    return Optional.ofNullable(itemSpecId)
      .map(itemSpecService::get)
      .orElse(null);
  }

  @Mappings({
    @Mapping(target = "supplierId", source = "supplier.id"),
    @Mapping(target = "receiverId", source = "receiver.id"),
    @Mapping(target = "chargerId", source = "charger.id")
  })
  public abstract PurchaseOrderData map(PurchaseOrder purchaseOrder);

  @Mappings({
    @Mapping(target = "supplier", source = "supplierId"),
    @Mapping(target = "receiver", source = "receiverId"),
    @Mapping(target = "charger", source = "chargerId"),
    @Mapping(target = "codeGenerator", expression = "java(purchaseOrderCodeGenerator)")
  })
  public abstract PurchaseOrderMessages.Create.Request map(
    PurchaseOrderRequests.CreateRequest request);

  @Mappings({
    @Mapping(target = "supplier", source = "supplierId"),
    @Mapping(target = "receiver", source = "receiverId"),
    @Mapping(target = "charger", source = "chargerId")
  })
  public abstract PurchaseOrderMessages.Update.Request map(
    PurchaseOrderRequests.UpdateRequest request);

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


