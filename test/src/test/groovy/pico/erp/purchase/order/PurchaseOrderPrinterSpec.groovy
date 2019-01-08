package pico.erp.purchase.order

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.shared.IntegrationConfiguration
import spock.lang.Specification

@SpringBootTest(classes = [IntegrationConfiguration])
@Transactional
@Rollback
@ActiveProfiles("test")
@Configuration
@ComponentScan("pico.erp.config")
class PurchaseOrderPrinterSpec extends Specification {

  @Autowired
  PurchaseOrderService orderService

  def id = PurchaseOrderId.from("purchase-order-a")

  def determineOrder() {
    orderService.determine(
      new PurchaseOrderRequests.DetermineRequest(
        id: id
      )
    )
  }

  def printOrder() {
    return orderService.printDraft(new PurchaseOrderRequests.PrintDraftRequest(
      id: id
    ))
  }


  def "출력 - 확정 후 출력"() {
    when:
    determineOrder()
    def result = printOrder()
    //FileCopyUtils.copy(result, new FileOutputStream("/Users/kojaehun/po.xlsx"))

    then:
    result.contentLength > 0
  }


}
