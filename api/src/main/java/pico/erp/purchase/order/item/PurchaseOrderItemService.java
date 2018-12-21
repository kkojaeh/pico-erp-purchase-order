package pico.erp.purchase.order.item;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import pico.erp.purchase.order.PurchaseOrderId;

public interface PurchaseOrderItemService {

  PurchaseOrderItemData create(
    @Valid @NotNull PurchaseOrderItemRequests.CreateRequest request);

  void delete(@Valid @NotNull PurchaseOrderItemRequests.DeleteRequest request);

  boolean exists(@Valid @NotNull PurchaseOrderItemId id);

  PurchaseOrderItemData get(@Valid @NotNull PurchaseOrderItemId id);

  List<PurchaseOrderItemData> getAll(PurchaseOrderId planId);

  void update(@Valid @NotNull PurchaseOrderItemRequests.UpdateRequest request);


}
