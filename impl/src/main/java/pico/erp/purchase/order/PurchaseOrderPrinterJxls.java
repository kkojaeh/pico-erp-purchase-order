package pico.erp.purchase.order;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jxls.common.Context;
import org.jxls.transform.poi.PoiTransformer;
import org.jxls.util.JxlsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import pico.erp.company.CompanyService;
import pico.erp.company.address.CompanyAddressExceptions;
import pico.erp.company.address.CompanyAddressService;
import pico.erp.purchase.order.item.PurchaseOrderItemRepository;
import pico.erp.shared.ExportHelper;
import pico.erp.shared.data.ContentInputStream;

@Component
public class PurchaseOrderPrinterJxls implements PurchaseOrderPrinter {


  private static OutputStream NOOP_OUTPUT_STREAM = new OutputStream() {
    @Override
    public void write(int b) throws IOException {
    }
  };

  @Value("${purchase-order.print.draft-template}")
  private Resource sheetTemplate;

  @Autowired
  private ExportHelper exportHelper;

  @Autowired
  private PurchaseOrderRepository purchaseOrderRepository;

  @Autowired
  private PurchaseOrderItemRepository purchaseOrderItemRepository;

  @Lazy
  @Autowired
  private CompanyService companyService;

  @Lazy
  @Autowired
  private CompanyAddressService companyAddressService;

  @SneakyThrows
  @Override
  public ContentInputStream printDraft(PurchaseOrderId id, DraftPrintOptions options) {
    @Cleanup
    InputStream template = sheetTemplate.getInputStream();

    val order = purchaseOrderRepository.findBy(id).get();

    val items = purchaseOrderItemRepository.findAllBy(id).collect(Collectors.toList());
    val owner = companyService.getOwner();
    val ownerAddress = companyAddressService.getAll(owner.getId()).stream()
      .filter(c -> c.isRepresented())
      .findFirst()
      .orElseThrow(CompanyAddressExceptions.CompanyAddressNotFoundException::new);

    val receiverAddress = companyAddressService.getAll(order.getReceiver().getId()).stream()
      .filter(c -> c.isRepresented())
      .findFirst()
      .orElseThrow(CompanyAddressExceptions.CompanyAddressNotFoundException::new);

    val supplierAddress = companyAddressService.getAll(order.getSupplier().getId()).stream()
      .filter(c -> c.isRepresented())
      .findFirst()
      .orElseThrow(CompanyAddressExceptions.CompanyAddressNotFoundException::new);

    Context context = new Context();
    context.putVar("owner", owner);
    context.putVar("ownerAddress", ownerAddress);
    context.putVar("receiverAddress", receiverAddress);
    context.putVar("supplierAddress", supplierAddress);
    context.putVar("order", order);
    context.putVar("items", items);
    context.putVar("options", options);
    context.putVar("helper", exportHelper);

    Workbook workbook = WorkbookFactory.create(template);

    PoiTransformer transformer = PoiTransformer.createTransformer(workbook);
    transformer.setOutputStream(NOOP_OUTPUT_STREAM);

    JxlsHelper.getInstance().processTemplate(context, transformer);

    for (Sheet sheet : workbook) {
      sheet.protectSheet(order.getCode().getValue());
    }
    @Cleanup
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    workbook.write(baos);
    return ContentInputStream.builder()
      .name(
        String.format("PO_%s_%s_%s.%s",
          order.getCode().getValue(),
          order.getSupplier().getName(),
          DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now()),
          ContentInputStream.XLSX_CONTENT_EXTENSION
        )
      )
      .contentType(ContentInputStream.XLSX_CONTENT_TYPE)
      .contentLength(baos.size())
      .inputStream(new ByteArrayInputStream(baos.toByteArray()))
      .build();
  }
}
