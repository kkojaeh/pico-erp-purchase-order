package pico.erp.purchase.order;

import java.time.OffsetDateTime;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository {

  long countCreatedBetween(OffsetDateTime begin, OffsetDateTime end);

  PurchaseOrder create(@NotNull PurchaseOrder orderAcceptance);

  void deleteBy(@NotNull PurchaseOrderId id);

  boolean exists(@NotNull PurchaseOrderId id);

  Optional<PurchaseOrder> findBy(@NotNull PurchaseOrderId id);

  void update(@NotNull PurchaseOrder orderAcceptance);

}
