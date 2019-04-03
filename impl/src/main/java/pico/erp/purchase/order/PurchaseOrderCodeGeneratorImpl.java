package pico.erp.purchase.order;

import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class PurchaseOrderCodeGeneratorImpl implements PurchaseOrderCodeGenerator {

  @Lazy
  @Autowired
  private PurchaseOrderRepository purchaseOrderRepository;

  @Override
  public PurchaseOrderCode generate(PurchaseOrder purchaseOrder) {
    val now = LocalDateTime.now();
    val begin = now.with(LocalTime.MIN);
    val end = now.with(LocalTime.MAX);
    val count = purchaseOrderRepository.countCreatedBetween(begin, end);
    val date =
      Integer.toString(now.getYear() - 1900, 36) + Integer.toString(now.getMonthValue(), 16)
        + Integer.toString(now.getDayOfMonth(), 36);
    val code = String.format("%s-%04d", date, count + 1).toUpperCase();
    return PurchaseOrderCode.from(code);
  }
}
