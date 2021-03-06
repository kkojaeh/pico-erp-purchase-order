package pico.erp.purchase.order;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.purchase.request.PurchaseRequestId;
import pico.erp.shared.event.Event;

public interface PurchaseOrderEvents {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class CreatedEvent implements Event {

    public final static String CHANNEL = "event.purchase-order.created";

    private PurchaseOrderId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class DeterminedEvent implements Event {

    public final static String CHANNEL = "event.purchase-order.determined";

    private PurchaseOrderId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class UpdatedEvent implements Event {

    public final static String CHANNEL = "event.purchase-order.updated";

    private PurchaseOrderId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class CanceledEvent implements Event {

    public final static String CHANNEL = "event.purchase-order.canceled";

    private PurchaseOrderId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class ReceivedEvent implements Event {

    public final static String CHANNEL = "event.purchase-order.received";

    private PurchaseOrderId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class SentEvent implements Event {

    public final static String CHANNEL = "event.purchase-order.sent";

    private PurchaseOrderId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class RejectedEvent implements Event {

    public final static String CHANNEL = "event.purchase-order.rejected";

    private PurchaseOrderId id;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class GeneratedEvent implements Event {

    public final static String CHANNEL = "event.purchase-order.generated";

    List<PurchaseRequestId> requestIds;

    private PurchaseOrderId id;

    public String channel() {
      return CHANNEL;
    }

  }


}
