package pico.erp.purchase.order

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.MessageSource
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.purchase.order.item.PurchaseOrderItemStatusKind
import pico.erp.shared.IntegrationConfiguration
import spock.lang.Specification

import java.util.stream.Collectors
import java.util.stream.Stream

@SpringBootTest(classes = [IntegrationConfiguration])
@Transactional
@Rollback
@ActiveProfiles("test")
@Configuration
@ComponentScan("pico.erp.config")
class MessageSourceSpec extends Specification {

  @Autowired
  MessageSource messageSource

  def locale = LocaleContextHolder.locale

  def "발주 상태"() {
    when:
    def messages = Stream.of(PurchaseOrderStatusKind.values())
      .map({
      kind -> messageSource.getMessage(kind.nameCode, null, locale)
    }).collect(Collectors.toList())

    println messages

    then:
    messages.size() == 6
  }

  def "발주 품목 상태"() {
    when:
    def messages = Stream.of(PurchaseOrderItemStatusKind.values())
      .map({
      kind -> messageSource.getMessage(kind.nameCode, null, locale)
    }).collect(Collectors.toList())

    println messages

    then:
    messages.size() == 7
  }


}
