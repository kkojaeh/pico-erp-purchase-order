package pico.erp.purchase.order;

import kkojaeh.spring.boot.component.Give;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pico.erp.user.group.GroupData;

@Give
@Data
@Configuration
@ConfigurationProperties("purchase-order")
public class PurchaseOrderPropertiesImpl implements PurchaseOrderProperties {

  GroupData chargerGroup;

}
