package pico.erp.purchase.order;

import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurchaseOrderQuery {

  Page<PurchaseOrderView> retrieve(@NotNull PurchaseOrderView.Filter filter,
    @NotNull Pageable pageable);

}
