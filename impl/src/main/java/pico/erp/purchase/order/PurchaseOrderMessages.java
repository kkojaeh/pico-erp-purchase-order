package pico.erp.purchase.order;

import java.time.OffsetDateTime;
import java.util.Collection;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.Value;
import pico.erp.company.CompanyData;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.data.Address;
import pico.erp.shared.data.Auditor;
import pico.erp.shared.event.Event;

public interface PurchaseOrderMessages {

  interface Create {

    @Data
    class Request {

      @Valid
      @NotNull
      PurchaseOrderId id;

      @Future
      @NotNull
      OffsetDateTime dueDate;

      @NotNull
      CompanyData supplier;

      @NotNull
      CompanyData receiver;

      @NotNull
      Address receiveAddress;

      @Size(max = TypeDefinitions.REMARK_LENGTH)
      String remark;

      @NotNull
      Auditor charger;

      @NotNull
      PurchaseOrderCodeGenerator codeGenerator;

    }

    @Value
    class Response {

      Collection<Event> events;

    }
  }


  interface Update {

    @Data
    class Request {

      @Future
      @NotNull
      OffsetDateTime dueDate;

      @NotNull
      CompanyData supplier;

      @NotNull
      CompanyData receiver;

      @NotNull
      Address receiveAddress;

      @NotNull
      Auditor charger;

      @Size(max = TypeDefinitions.REMARK_LENGTH)
      String remark;

    }

    @Value
    class Response {

      Collection<Event> events;

    }
  }


  interface Determine {

    @Data
    class Request {

    }

    @Value
    class Response {

      Collection<Event> events;

    }
  }


  interface Send {

    @Data
    class Request {

    }

    @Value
    class Response {

      Collection<Event> events;

    }
  }

  interface Receive {

    @Data
    class Request {

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }

  interface Reject {

    @Data
    class Request {

      @Size(max = TypeDefinitions.REMARK_LENGTH)
      String rejectedReason;

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }

  interface Cancel {

    @Data
    class Request {

    }

    @Value
    class Response {

      Collection<Event> events;

    }

  }


}
