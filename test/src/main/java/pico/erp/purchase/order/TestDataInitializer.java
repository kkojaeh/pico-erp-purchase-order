package pico.erp.purchase.order;

import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import pico.erp.purchase.order.item.PurchaseOrderItemRequests;
import pico.erp.purchase.order.item.PurchaseOrderItemService;
import pico.erp.shared.ApplicationInitializer;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
@Profile({"default", "test"})
public class TestDataInitializer implements ApplicationInitializer {

  @Lazy
  @Autowired
  private PurchaseOrderService purchaseOrderService;

  @Lazy
  @Autowired
  private PurchaseOrderItemService purchaseOrderItemService;


  @Autowired
  private DataProperties dataProperties;

  @Override
  public void initialize() {
    dataProperties.purchaseOrders.forEach(purchaseOrderService::create);
    dataProperties.purchaseOrderItems.forEach(purchaseOrderItemService::create);
  }

  @Data
  @Configuration
  @ConfigurationProperties("data")
  public static class DataProperties {

    List<PurchaseOrderRequests.CreateRequest> purchaseOrders = new LinkedList<>();

    List<PurchaseOrderItemRequests.CreateRequest> purchaseOrderItems = new LinkedList<>();

  }

}
