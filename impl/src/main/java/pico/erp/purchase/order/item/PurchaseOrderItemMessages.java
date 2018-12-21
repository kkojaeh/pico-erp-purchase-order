package pico.erp.purchase.order.item;

import java.math.BigDecimal;
import java.util.Collection;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import pico.erp.item.ItemData;
import pico.erp.item.lot.ItemLotData;
import pico.erp.item.spec.ItemSpecData;
import pico.erp.project.ProjectData;
import pico.erp.purchase.order.PurchaseOrder;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.event.Event;

public interface PurchaseOrderItemMessages {

  interface Create {

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    class Request {

      @Valid
      @NotNull
      PurchaseOrderItemId id;

      @NotNull
      PurchaseOrder order;

      @NotNull
      ItemData item;

      ItemSpecData itemSpec;

      @NotNull
      @Min(0)
      BigDecimal quantity;

      BigDecimal estimatedUnitCost;

      @NotNull
      @Min(0)
      BigDecimal unitCost;

      @Size(max = TypeDefinitions.REMARK_LENGTH)
      String remark;

      @NotNull
      ProjectData project;

    }

    @Value
    class Response {

      Collection<Event> events;

    }
  }


  interface Update {

    @Data
    class Request {

      ItemSpecData itemSpec;

      BigDecimal estimatedUnitCost;

      @NotNull
      @Min(0)
      BigDecimal unitCost;

      @NotNull
      @Min(0)
      BigDecimal quantity;

      @Size(max = TypeDefinitions.REMARK_LENGTH)
      String remark;

    }

    @Value
    class Response {

      Collection<Event> events;

    }
  }

  interface Delete {

    @Data
    class Request {

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }

}
