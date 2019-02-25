package pico.erp.purchase.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import pico.erp.company.CompanyService;
import pico.erp.company.address.CompanyAddressData;
import pico.erp.company.address.CompanyAddressService;
import pico.erp.document.context.DocumentContextFactory;
import pico.erp.document.subject.DocumentSubjectDefinition;
import pico.erp.document.subject.DocumentSubjectId;
import pico.erp.item.ItemService;
import pico.erp.purchase.order.item.PurchaseOrderItemService;
import pico.erp.shared.Public;
import pico.erp.user.UserService;

@Public
@Component
public class PurchaseOrderDraftDocumentSubjectDefinition implements
  DocumentSubjectDefinition<PurchaseOrderId, Object> {

  public static DocumentSubjectId ID = DocumentSubjectId.from("purchase-order-draft");

  @Getter
  DocumentSubjectId id = ID;

  @Getter
  String name = "[purchase-order] 발주서";

  @Lazy
  @Autowired
  private DocumentContextFactory contextFactory;

  @Lazy
  @Autowired
  private CompanyService companyService;

  @Lazy
  @Autowired
  private CompanyAddressService companyAddressService;

  @Lazy
  @Autowired
  private PurchaseOrderService purchaseOrderService;

  @Lazy
  @Autowired
  private PurchaseOrderItemService purchaseOrderItemService;

  @Lazy
  @Autowired
  private UserService userService;

  @Lazy
  @Autowired
  private ItemService itemService;

  @Lazy
  @Autowired
  private MessageSource messageSource;

  @Override
  public Object getContext(PurchaseOrderId key) {
    val locale = LocaleContextHolder.getLocale();
    val context = contextFactory.factory();
    val data = context.getData();
    val order = purchaseOrderService.get(key);

    val owner = companyService.getOwner();
    val supplier = companyService.get(order.getSupplierId());
    val receiver = companyService.get(order.getReceiverId());
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

    val charger = userService.get(order.getChargerId());
    List<Map<String, Object>> items = purchaseOrderItemService.getAll(key).stream()
      .map(orderItem -> {
        val map = new HashMap<String, Object>();
        map.put("order", orderItem);
        map.put("item", itemService.get(orderItem.getItemId()));
        map.put("unitLabel", messageSource
          .getMessage(orderItem.getUnit().getNameCode(), null, orderItem.getUnit().getDefault(),
            locale));
        return map;
      })
      .collect(Collectors.toList());

    data.put("owner", owner);
    data.put("supplier", supplier);
    data.put("receiver", receiver);
    data.put("ownerAddress", ownerAddress);
    data.put("receiverAddress", receiverAddress);
    data.put("supplierAddress", supplierAddress);
    data.put("charger", charger);
    data.put("order", order);
    data.put("items", items);
    return context;
  }

  @Override
  public PurchaseOrderId getKey(String key) {
    return PurchaseOrderId.from(key);
  }

}
