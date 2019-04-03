package pico.erp.purchase.order;

import java.time.LocalDateTime;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository {

  long countCreatedBetween(LocalDateTime begin, LocalDateTime end);

  PurchaseOrder create(@NotNull PurchaseOrder orderAcceptance);

  void deleteBy(@NotNull PurchaseOrderId id);

  boolean exists(@NotNull PurchaseOrderId id);

  Optional<PurchaseOrder> findBy(@NotNull PurchaseOrderId id);

  void update(@NotNull PurchaseOrder orderAcceptance);

}
