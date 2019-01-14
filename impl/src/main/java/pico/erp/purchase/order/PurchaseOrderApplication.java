package pico.erp.purchase.order;

import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import pico.erp.audit.AuditApi;
import pico.erp.audit.AuditConfiguration;
import pico.erp.item.ItemApi;
import pico.erp.project.ProjectApi;
import pico.erp.purchase.order.PurchaseOrderApi.Roles;
import pico.erp.purchase.request.PurchaseRequestApi;
import pico.erp.shared.ApplicationId;
import pico.erp.shared.ApplicationStarter;
import pico.erp.shared.Public;
import pico.erp.shared.SpringBootConfigs;
import pico.erp.shared.data.Role;
import pico.erp.shared.impl.ApplicationImpl;
import pico.erp.user.UserApi;
import pico.erp.warehouse.WarehouseApi;

@Slf4j
@SpringBootConfigs
public class PurchaseOrderApplication implements ApplicationStarter {

  public static final String CONFIG_NAME = "purchase-order/application";

  public static final Properties DEFAULT_PROPERTIES = new Properties();

  static {
    DEFAULT_PROPERTIES.put("spring.config.name", CONFIG_NAME);
  }

  public static SpringApplication application() {
    return new SpringApplicationBuilder(PurchaseOrderApplication.class)
      .properties(DEFAULT_PROPERTIES)
      .web(false)
      .build();
  }

  public static void main(String[] args) {
    application().run(args);
  }

  @Bean
  @Public
  public AuditConfiguration auditConfiguration() {
    return AuditConfiguration.builder()
      .packageToScan("pico.erp.purchase.order")
      .entity(Roles.class)
      .build();
  }

  @Override
  public Set<ApplicationId> getDependencies() {
    return Stream.of(
      UserApi.ID,
      ItemApi.ID,
      AuditApi.ID,
      ProjectApi.ID,
      PurchaseRequestApi.ID,
      WarehouseApi.ID
    ).collect(Collectors.toSet());
  }

  @Override
  public ApplicationId getId() {
    return PurchaseOrderApi.ID;
  }

  @Override
  public boolean isWeb() {
    return false;
  }

  @Bean
  @Public
  public Role purchaseOrderCharger() {
    return Roles.PURCHASE_ORDER_CHARGER;
  }

  @Bean
  @Public
  public Role purchaseOrderManager() {
    return Roles.PURCHASE_ORDER_MANAGER;
  }

  @Override
  public pico.erp.shared.Application start(String... args) {
    return new ApplicationImpl(application().run(args));
  }

}
