package pico.erp.purchase.order;

import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import pico.erp.company.CompanyService;
import pico.erp.delivery.subject.DeliverySubjectDefinition;
import pico.erp.delivery.subject.DeliverySubjectId;
import pico.erp.shared.Public;
import pico.erp.shared.data.ContentInputStream;
import pico.erp.user.UserService;

@Public
@Component
public class PurchaseOrderDraftDeliverySubjectDefinition implements
  DeliverySubjectDefinition<PurchaseOrderId, Object> {

  public static DeliverySubjectId ID = DeliverySubjectId.from("purchase-order-draft");

  @Getter
  DeliverySubjectId id = ID;

  @Getter
  String name = "[purchase-order] 발주서";

  @Lazy
  @Autowired
  private CompanyService companyService;

  @Lazy
  @Autowired
  private PurchaseOrderService purchaseOrderService;

  @Lazy
  @Autowired
  private UserService userService;

  @Override
  public List<ContentInputStream> getAttachments(PurchaseOrderId key) {
    return null;
  }

  @Override
  public Object getContext(PurchaseOrderId key) {
    val data = new HashMap<String, Object>();
    val order = purchaseOrderService.get(key);
    data.put("supplier", companyService.get(order.getSupplierId()));
    data.put("owner", companyService.getOwner());
    data.put("charger", userService.get(order.getChargerId()));
    data.put("order", order);
    return data;
  }

  @Override
  public PurchaseOrderId toKey(String key) {
    return PurchaseOrderId.from(key);
  }

  @Override
  public String toString(PurchaseOrderId key) {
    return key.getValue().toString();
  }

}
