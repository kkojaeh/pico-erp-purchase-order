package pico.erp.purchase.order.item;

import java.util.Optional;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;
import pico.erp.purchase.order.PurchaseOrderId;

@Repository
public interface PurchaseOrderItemRepository {

  PurchaseOrderItem create(@NotNull PurchaseOrderItem item);

  void deleteBy(@NotNull PurchaseOrderItemId id);

  boolean exists(@NotNull PurchaseOrderItemId id);

  Stream<PurchaseOrderItem> findAllBy(@NotNull PurchaseOrderId planId);

  Optional<PurchaseOrderItem> findBy(@NotNull PurchaseOrderItemId id);

  void update(@NotNull PurchaseOrderItem item);

}
