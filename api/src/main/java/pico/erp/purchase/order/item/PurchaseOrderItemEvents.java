package pico.erp.purchase.order.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.shared.event.Event;

public interface PurchaseOrderItemEvents {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class CreatedEvent implements Event {

    public final static String CHANNEL = "event.purchase-order-item.created";

    private PurchaseOrderItemId purchaseRequestItemId;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class UpdatedEvent implements Event {

    public final static String CHANNEL = "event.purchase-order-item.updated";

    private PurchaseOrderItemId purchaseRequestItemId;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class DeletedEvent implements Event {

    public final static String CHANNEL = "event.purchase-order-item.deleted";

    private PurchaseOrderItemId purchaseRequestItemId;

    public String channel() {
      return CHANNEL;
    }

  }
}
