package pico.erp.purchase.order;

import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.shared.data.ContentInputStream;

public interface PurchaseOrderPrinter {

  ContentInputStream printDraft(PurchaseOrderId id, DraftPrintOptions options);

  @Data
  @NoArgsConstructor
  class DraftPrintOptions {

  }

}
