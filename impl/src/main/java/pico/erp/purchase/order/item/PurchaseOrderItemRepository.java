package pico.erp.purchase.order.item;

import java.util.Optional;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;
import pico.erp.purchase.order.PurchaseOrderId;
import pico.erp.purchase.request.PurchaseRequestId;

@Repository
public interface PurchaseOrderItemRepository {

  PurchaseOrderItem create(@NotNull PurchaseOrderItem item);

  void deleteBy(@NotNull PurchaseOrderItemId id);

  boolean exists(@NotNull PurchaseOrderItemId id);

  boolean exists(@NotNull PurchaseRequestId requestId);

  Stream<PurchaseOrderItem> findAllBy(@NotNull PurchaseOrderId orderId);

  Optional<PurchaseOrderItem> findBy(@NotNull PurchaseOrderItemId id);

  Optional<PurchaseOrderItem> findBy(@NotNull PurchaseRequestId requestId);

  void update(@NotNull PurchaseOrderItem item);

}
