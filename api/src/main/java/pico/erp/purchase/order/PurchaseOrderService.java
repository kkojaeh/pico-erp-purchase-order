package pico.erp.purchase.order;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import pico.erp.purchase.order.PurchaseOrderRequests.ReceiveRequest;

public interface PurchaseOrderService {

  void cancel(@Valid @NotNull PurchaseOrderRequests.CancelRequest request);

  PurchaseOrderData create(@Valid @NotNull PurchaseOrderRequests.CreateRequest request);

  boolean exists(@Valid @NotNull PurchaseOrderId id);

  PurchaseOrderData get(@Valid @NotNull PurchaseOrderId id);

  void update(@Valid @NotNull PurchaseOrderRequests.UpdateRequest request);

  void determine(@Valid @NotNull PurchaseOrderRequests.DetermineRequest request);

  void send(@Valid @NotNull PurchaseOrderRequests.SendRequest request);

  void receive(@Valid @NotNull PurchaseOrderRequests.ReceiveRequest request);

  void reject(@Valid @NotNull PurchaseOrderRequests.RejectRequest request);

}
