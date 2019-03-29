package pico.erp.purchase.order;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import kkojaeh.spring.boot.component.Give;
import kkojaeh.spring.boot.component.Take;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pico.erp.company.CompanyService;
import pico.erp.company.address.CompanyAddressData;
import pico.erp.company.address.CompanyAddressService;
import pico.erp.delivery.subject.DeliverySubjectDefinition;
import pico.erp.delivery.subject.DeliverySubjectId;
import pico.erp.document.DocumentService;
import pico.erp.shared.data.ContentInputStream;
import pico.erp.user.UserService;

@Give
@Component
public class PurchaseOrderDraftDeliverySubjectDefinition implements
  DeliverySubjectDefinition<PurchaseOrderId, Object> {

  public static DeliverySubjectId ID = DeliverySubjectId.from("purchase-order-draft");

  @Getter
  DeliverySubjectId id = ID;

  @Getter
  String name = "[purchase-order] 발주서";

  @Take
  private CompanyService companyService;

  @Autowired
  private PurchaseOrderService purchaseOrderService;

  @Take
  private UserService userService;

  @Take
  private DocumentService documentService;

  @Take
  private CompanyAddressService companyAddressService;

  @Override
  public List<ContentInputStream> getAttachments(PurchaseOrderId key) {
    val order = purchaseOrderService.get(key);
    return Arrays.asList(documentService.load(order.getDraftId()));
  }

  @Override
  public Object getContext(PurchaseOrderId key) {
    val data = new HashMap<String, Object>();
    val owner = companyService.getOwner();
    val order = purchaseOrderService.get(key);
    val ownerAddress = companyAddressService.getAll(owner.getId()).stream()
      .filter(c -> c.isRepresented())
      .findFirst()
      .orElse(new CompanyAddressData());

    val receiverAddress = companyAddressService.getAll(order.getReceiverId()).stream()
      .filter(c -> c.isRepresented())
      .findFirst()
      .orElse(new CompanyAddressData());

    val supplierAddress = companyAddressService.getAll(order.getSupplierId()).stream()
      .filter(c -> c.isRepresented())
      .findFirst()
      .orElse(new CompanyAddressData());
    data.put("supplier", companyService.get(order.getSupplierId()));
    data.put("owner", owner);
    data.put("ownerAddress", ownerAddress);
    data.put("receiverAddress", receiverAddress);
    data.put("supplierAddress", supplierAddress);
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
