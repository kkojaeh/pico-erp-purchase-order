package pico.erp.purchase.order.item;

import java.util.Optional;
import kkojaeh.spring.boot.component.Take;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import pico.erp.item.ItemData;
import pico.erp.item.ItemId;
import pico.erp.item.ItemService;
import pico.erp.item.lot.ItemLotData;
import pico.erp.item.lot.ItemLotId;
import pico.erp.item.lot.ItemLotService;
import pico.erp.item.spec.ItemSpecData;
import pico.erp.item.spec.ItemSpecId;
import pico.erp.item.spec.ItemSpecService;
import pico.erp.project.ProjectData;
import pico.erp.project.ProjectId;
import pico.erp.project.ProjectService;
import pico.erp.purchase.order.PurchaseOrder;
import pico.erp.purchase.order.PurchaseOrderExceptions;
import pico.erp.purchase.order.PurchaseOrderId;
import pico.erp.purchase.order.PurchaseOrderMapper;
import pico.erp.shared.data.Auditor;

@Mapper
public abstract class PurchaseOrderItemMapper {

  @Autowired
  protected AuditorAware<Auditor> auditorAware;

  @Lazy
  @Autowired
  protected ItemService itemService;

  @Lazy
  @Autowired
  protected ItemLotService itemLotService;

  @Lazy
  @Autowired
  protected ItemSpecService itemSpecService;

  @Lazy
  @Autowired
  private PurchaseOrderItemRepository purchaseRequestItemRepository;

  @Autowired
  private PurchaseOrderMapper requestMapper;

  @Lazy
  @Autowired
  private ProjectService projectService;

  @Take(required = false)
  protected PurchaseOrderItemUnitCostEstimator unitCostEstimator;

  protected PurchaseOrderItemId id(PurchaseOrderItem purchaseRequestItem) {
    return purchaseRequestItem != null ? purchaseRequestItem.getId() : null;
  }

  @Mappings({
    @Mapping(target = "orderId", source = "order.id"),
    @Mapping(target = "createdBy", ignore = true),
    @Mapping(target = "createdDate", ignore = true),
    @Mapping(target = "lastModifiedBy", ignore = true),
    @Mapping(target = "lastModifiedDate", ignore = true)
  })
  public abstract PurchaseOrderItemEntity jpa(PurchaseOrderItem data);

  public PurchaseOrderItem jpa(PurchaseOrderItemEntity entity) {
    return PurchaseOrderItem.builder()
      .id(entity.getId())
      .order(map(entity.getOrderId()))
      .itemId(entity.getItemId())
      .itemSpecId(entity.getItemSpecId())
      .itemSpecCode(entity.getItemSpecCode())
      .quantity(entity.getQuantity())
      .receivedQuantity(entity.getReceivedQuantity())
      .estimatedUnitCost(entity.getEstimatedUnitCost())
      .unitCost(entity.getUnitCost())
      .unit(entity.getUnit())
      .remark(entity.getRemark())
      .projectId(entity.getProjectId())
      .requestId(entity.getRequestId())
      .status(entity.getStatus())
      .build();
  }

  public PurchaseOrderItem map(PurchaseOrderItemId purchaseRequestItemId) {
    return Optional.ofNullable(purchaseRequestItemId)
      .map(id -> purchaseRequestItemRepository.findBy(id)
        .orElseThrow(PurchaseOrderExceptions.NotFoundException::new)
      )
      .orElse(null);
  }

  protected ItemData map(ItemId itemId) {
    return Optional.ofNullable(itemId)
      .map(itemService::get)
      .orElse(null);
  }

  protected ItemLotData map(ItemLotId itemLotId) {
    return Optional.ofNullable(itemLotId)
      .map(itemLotService::get)
      .orElse(null);
  }

  protected ItemSpecData map(ItemSpecId itemSpecId) {
    return Optional.ofNullable(itemSpecId)
      .map(itemSpecService::get)
      .orElse(null);
  }

  protected PurchaseOrder map(PurchaseOrderId purchaseOrderId) {
    return requestMapper.map(purchaseOrderId);
  }

  protected ProjectData map(ProjectId projectId) {
    return Optional.ofNullable(projectId)
      .map(projectService::get)
      .orElse(null);
  }

  @Mappings({
    @Mapping(target = "orderId", source = "order.id")
  })
  public abstract PurchaseOrderItemData map(PurchaseOrderItem item);

  @Mappings({
    @Mapping(target = "order", source = "orderId"),
    @Mapping(target = "unitCostEstimator", expression = "java(unitCostEstimator)")
  })
  public abstract PurchaseOrderItemMessages.Create.Request map(
    PurchaseOrderItemRequests.CreateRequest request);

  @Mappings({
    @Mapping(target = "unitCostEstimator", expression = "java(unitCostEstimator)")
  })
  public abstract PurchaseOrderItemMessages.Update.Request map(
    PurchaseOrderItemRequests.UpdateRequest request);

  public abstract PurchaseOrderItemMessages.Delete.Request map(
    PurchaseOrderItemRequests.DeleteRequest request);

  public abstract PurchaseOrderItemMessages.Receive.Request map(
    PurchaseOrderItemRequests.ReceiveRequest request);

  public abstract PurchaseOrderItemMessages.Determine.Request map(
    PurchaseOrderItemRequests.DetermineRequest request);

  public abstract PurchaseOrderItemMessages.Send.Request map(
    PurchaseOrderItemRequests.SendRequest request);

  public abstract PurchaseOrderItemMessages.Reject.Request map(
    PurchaseOrderItemRequests.RejectRequest request);

  public abstract PurchaseOrderItemMessages.Cancel.Request map(
    PurchaseOrderItemRequests.CancelRequest request);


  public abstract void pass(
    PurchaseOrderItemEntity from, @MappingTarget PurchaseOrderItemEntity to);


}



