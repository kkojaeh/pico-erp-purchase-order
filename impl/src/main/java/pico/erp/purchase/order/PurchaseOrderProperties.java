package pico.erp.purchase.order;

import java.time.LocalTime;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("purchase-order")
public class PurchaseOrderProperties {

  DetailGenerationPolicy detailGenerationPolicy;

  @Data
  public static class DetailGenerationPolicy {

    LocalTime startTime;

    LocalTime endTime;

  }

}
