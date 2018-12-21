package pico.erp.purchase.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.shared.event.Event;

public interface PurchaseOrderEvents {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class CreatedEvent implements Event {

    public final static String CHANNEL = "event.purchase-order.created";

    private PurchaseOrderId purchaseOrderId;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class ProgressedEvent implements Event {

    public final static String CHANNEL = "event.purchase-order.progressed";

    private PurchaseOrderId purchaseOrderId;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class DeterminedEvent implements Event {

    public final static String CHANNEL = "event.purchase-order.determined";

    private PurchaseOrderId purchaseOrderId;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class UpdatedEvent implements Event {

    public final static String CHANNEL = "event.purchase-order.updated";

    private PurchaseOrderId purchaseOrderId;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class CanceledEvent implements Event {

    public final static String CHANNEL = "event.purchase-order.canceled";

    private PurchaseOrderId purchaseOrderId;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class ReceivedEvent implements Event {

    public final static String CHANNEL = "event.purchase-order.received";

    private PurchaseOrderId purchaseOrderId;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class SentEvent implements Event {

    public final static String CHANNEL = "event.purchase-order.sent";

    private PurchaseOrderId purchaseOrderId;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class RejectedEvent implements Event {

    public final static String CHANNEL = "event.purchase-order.rejected";

    private PurchaseOrderId purchaseOrderId;

    public String channel() {
      return CHANNEL;
    }

  }


}
