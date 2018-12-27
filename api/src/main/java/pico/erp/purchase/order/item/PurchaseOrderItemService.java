package pico.erp.purchase.order.item;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import pico.erp.purchase.order.PurchaseOrderId;
import pico.erp.purchase.request.item.PurchaseRequestItemId;

public interface PurchaseOrderItemService {

  PurchaseOrderItemData create(
    @Valid @NotNull PurchaseOrderItemRequests.CreateRequest request);

  void delete(@Valid @NotNull PurchaseOrderItemRequests.DeleteRequest request);

  boolean exists(@Valid @NotNull PurchaseOrderItemId id);

  void cancel(@Valid @NotNull PurchaseOrderItemRequests.CancelRequest request);

  PurchaseOrderItemData get(@Valid @NotNull PurchaseOrderItemId id);

  void determine(@Valid @NotNull PurchaseOrderItemRequests.DetermineRequest request);

  boolean exists(@Valid @NotNull PurchaseRequestItemId requestItemId);

  void update(@Valid @NotNull PurchaseOrderItemRequests.UpdateRequest request);

  void generate(@Valid @NotNull PurchaseOrderItemRequests.GenerateRequest request);

  PurchaseOrderItemData get(@Valid @NotNull PurchaseRequestItemId requestItemId);

  List<PurchaseOrderItemData> getAll(@Valid @NotNull PurchaseOrderId orderId);

  void receive(@Valid @NotNull PurchaseOrderItemRequests.ReceiveRequest request);

  void reject(@Valid @NotNull PurchaseOrderItemRequests.RejectRequest request);

  void send(@Valid @NotNull PurchaseOrderItemRequests.SendRequest request);

}
