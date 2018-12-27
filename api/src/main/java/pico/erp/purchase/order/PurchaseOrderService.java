package pico.erp.purchase.order;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface PurchaseOrderService {

  void cancel(@Valid @NotNull PurchaseOrderRequests.CancelRequest request);

  PurchaseOrderData create(@Valid @NotNull PurchaseOrderRequests.CreateRequest request);

  void determine(@Valid @NotNull PurchaseOrderRequests.DetermineRequest request);

  boolean exists(@Valid @NotNull PurchaseOrderId id);

  PurchaseOrderData get(@Valid @NotNull PurchaseOrderId id);

  void receive(@Valid @NotNull PurchaseOrderRequests.ReceiveRequest request);

  void reject(@Valid @NotNull PurchaseOrderRequests.RejectRequest request);

  void send(@Valid @NotNull PurchaseOrderRequests.SendRequest request);

  void update(@Valid @NotNull PurchaseOrderRequests.UpdateRequest request);

  PurchaseOrderData generate(@Valid @NotNull PurchaseOrderRequests.GenerateRequest request);

}
