package pico.erp.config;

import java.math.BigDecimal;
import kkojaeh.spring.boot.component.ComponentAutowired;
import kkojaeh.spring.boot.component.ComponentBean;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pico.erp.item.ItemService;
import pico.erp.item.spec.ItemSpecService;
import pico.erp.purchase.order.item.PurchaseOrderItemUnitCostEstimator;

@Configuration
public class PurchaseOrderConfiguration {

  @Bean
  @ComponentBean(host = false)
  @ConditionalOnMissingBean(PurchaseOrderItemUnitCostEstimator.class)
  public PurchaseOrderItemUnitCostEstimator defaultPurchaseOrderItemUnitCostEstimator() {
    return new DefaultPurchaseOrderItemUnitCostEstimator();
  }

  public static class DefaultPurchaseOrderItemUnitCostEstimator implements
    PurchaseOrderItemUnitCostEstimator {

    @ComponentAutowired
    ItemService itemService;

    @ComponentAutowired
    ItemSpecService itemSpecService;

    @Override
    public BigDecimal estimate(PurchaseOrderItemContext context) {
      if (context.getItemSpecId() != null) {
        val itemSpec = itemSpecService.get(context.getItemSpecId());
        return itemSpec.getPurchaseUnitCost();
      }
      val item = itemService.get(context.getItemId());
      if (BigDecimal.ZERO.compareTo(item.getBaseUnitCost()) == 0) {
        return null;
      } else {
        return item.getBaseUnitCost();
      }
    }

  }

}
