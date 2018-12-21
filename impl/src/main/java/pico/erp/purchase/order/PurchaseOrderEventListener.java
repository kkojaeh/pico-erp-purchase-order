package pico.erp.purchase.order;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("unused")
@Component
@Transactional
public class PurchaseOrderEventListener {

  private static final String LISTENER_NAME = "listener.purchase-order-event-listener";

}
