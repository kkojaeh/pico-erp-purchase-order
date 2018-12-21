package pico.erp.purchase.order;

import javax.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pico.erp.shared.ApplicationId;
import pico.erp.shared.data.Role;

public final class PurchaseOrderApi {

  public final static ApplicationId ID = ApplicationId.from("purchase-order");

  @RequiredArgsConstructor
  public enum Roles implements Role {

    PURCHASE_ORDER_CHARGER,
    PURCHASE_ORDER_MANAGER;

    @Id
    @Getter
    private final String id = name();

  }
}
