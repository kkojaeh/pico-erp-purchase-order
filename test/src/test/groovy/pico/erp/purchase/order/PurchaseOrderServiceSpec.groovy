package pico.erp.purchase.order

import kkojaeh.spring.boot.component.SpringBootTestComponent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.company.CompanyApplication
import pico.erp.company.CompanyId
import pico.erp.delivery.DeliveryApplication
import pico.erp.document.DocumentApplication
import pico.erp.item.ItemApplication
import pico.erp.project.ProjectApplication
import pico.erp.purchase.request.PurchaseRequestApplication
import pico.erp.shared.TestParentApplication
import pico.erp.shared.data.Address
import pico.erp.user.UserApplication
import pico.erp.user.UserId
import pico.erp.warehouse.WarehouseApplication
import spock.lang.Specification

import java.time.LocalDateTime

@SpringBootTest(classes = [PurchaseOrderApplication, TestConfig])
@SpringBootTestComponent(parent = TestParentApplication, siblings = [
  UserApplication, ItemApplication, ProjectApplication, CompanyApplication, DocumentApplication, DeliveryApplication,
  PurchaseRequestApplication, WarehouseApplication
])
@Transactional
@Rollback
@ActiveProfiles("test")
class PurchaseOrderServiceSpec extends Specification {

  @Autowired
  PurchaseOrderService orderService

  def id = PurchaseOrderId.from("order-1")

  def unknownId = PurchaseOrderId.from("unknown")

  def dueDate = LocalDateTime.now().plusDays(7)

  def remark = "요청 비고"

  def receiverId = CompanyId.from("CUST1")

  def supplierId = CompanyId.from("SUPP1")

  def chargerId = UserId.from("kjh")

  def receiverId2 = CompanyId.from("CUST2")

  def supplierId2 = CompanyId.from("SUPP2")

  def dueDate2 = LocalDateTime.now().plusDays(8)

  def remark2 = "요청 비고2"

  def receiveAddress = new Address(
    postalCode: '13496',
    street: '경기도 성남시 분당구 장미로 42',
    detail: '야탑리더스 410호'
  )

  def receiveAddress2 = new Address(
    postalCode: '13490',
    street: '경기도 성남시 분당구 장미로 40',
    detail: '야탑리더스 510호'
  )

  def setup() {
    orderService.create(
      new PurchaseOrderRequests.CreateRequest(
        id: id,
        receiverId: receiverId,
        supplierId: supplierId,
        receiveAddress: receiveAddress,
        chargerId: chargerId,
        dueDate: dueDate,
        remark: remark,
      )
    )
  }

  def cancelOrder() {
    orderService.cancel(
      new PurchaseOrderRequests.CancelRequest(
        id: id
      )
    )
  }

  def determineOrder() {
    orderService.determine(
      new PurchaseOrderRequests.DetermineRequest(
        id: id
      )
    )
  }

  def rejectOrder() {
    orderService.reject(
      new PurchaseOrderRequests.RejectRequest(
        id: id,
        rejectedReason: "작업자가 없어요"
      )
    )
  }

  def receiveOrder() {
    orderService.receive(
      new PurchaseOrderRequests.ReceiveRequest(
        id: id
      )
    )
  }

  def sendOrder() {
    orderService.send(
      new PurchaseOrderRequests.SendRequest(
        id: id
      )
    )
  }

  def updateOrder() {
    orderService.update(
      new PurchaseOrderRequests.UpdateRequest(
        id: id,
        receiverId: receiverId2,
        supplierId: supplierId2,
        receiveAddress: receiveAddress2,
        dueDate: dueDate2,
        remark: remark2,
        chargerId: chargerId
      )
    )
  }


  def "존재 - 아이디로 존재 확인"() {
    when:
    def exists = orderService.exists(id)

    then:
    exists == true
  }

  def "존재 - 존재하지 않는 아이디로 확인"() {
    when:
    def exists = orderService.exists(unknownId)

    then:
    exists == false
  }

  def "조회 - 아이디로 조회"() {
    when:
    def order = orderService.get(id)

    then:
    order.id == id
    order.receiverId == receiverId
    order.remark == remark
    order.chargerId == chargerId
    order.dueDate == dueDate
    order.receiveAddress == receiveAddress

  }

  def "조회 - 존재하지 않는 아이디로 조회"() {
    when:
    orderService.get(unknownId)

    then:
    thrown(PurchaseOrderExceptions.NotFoundException)
  }


  def "수정 - 취소 후 수정"() {
    when:
    cancelOrder()
    updateOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotUpdateException)
  }

  def "수정 - 전송 후 수정"() {
    when:
    determineOrder()
    sendOrder()
    updateOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotUpdateException)
  }

  def "수정 - 확정 후 수정"() {
    when:
    determineOrder()
    updateOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotUpdateException)
  }

  def "수정 - 수령 후 수정"() {
    when:
    determineOrder()
    sendOrder()

    receiveOrder()
    updateOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotUpdateException)
  }

  def "수정 - 반려 후 수정"() {
    when:
    determineOrder()
    sendOrder()
    rejectOrder()
    updateOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotUpdateException)
  }

  def "수정 - 진행 후 수정"() {
    when:
    determineOrder()
    sendOrder()
    updateOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotUpdateException)
  }


  def "수정 - 작성 후 수정"() {
    when:
    updateOrder()
    def order = orderService.get(id)

    then:
    order.receiverId == receiverId2
    order.supplierId == supplierId2
    order.receiveAddress == receiveAddress2
    order.dueDate == dueDate2
    order.remark == remark2
    order.chargerId == chargerId
  }

  def "확정 - 작성 후 확정"() {
    when:
    determineOrder()
    def order = orderService.get(id)
    then:
    order.status == PurchaseOrderStatusKind.DETERMINED
  }

  def "확정 - 확정 후 확정"() {
    when:
    determineOrder()
    determineOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotDetermineException)
  }


  def "확정 - 취소 후 확정"() {
    when:
    cancelOrder()
    determineOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotDetermineException)
  }

  def "확정 - 반려 후 확정"() {
    when:
    determineOrder()
    sendOrder()
    rejectOrder()
    determineOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotDetermineException)
  }

  def "확정 - 수령 후 확정"() {
    when:
    determineOrder()
    sendOrder()
    receiveOrder()
    determineOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotDetermineException)
  }

  def "전송 - 작성 후 전송"() {
    when:
    sendOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotSendException)
  }

  def "전송 - 전송 후 전송"() {
    when:
    determineOrder()
    sendOrder()
    sendOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotSendException)
  }

  def "전송 - 확정 후 전송"() {
    when:
    determineOrder()
    sendOrder()
    def order = orderService.get(id)
    then:
    order.status == PurchaseOrderStatusKind.SENT
  }

  def "전송 - 취소 후 전송"() {
    when:
    cancelOrder()
    sendOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotSendException)
  }

  def "전송 - 반려 후 전송"() {
    when:
    determineOrder()
    sendOrder()
    rejectOrder()
    sendOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotSendException)
  }

  def "전송 - 수령 후 전송"() {
    when:
    determineOrder()
    sendOrder()
    receiveOrder()
    sendOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotSendException)
  }


  def "취소 - 취소 후에는 취소"() {
    when:
    cancelOrder()
    cancelOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotCancelException)
  }

  def "취소 - 확정 후 취소"() {
    when:
    determineOrder()
    cancelOrder()
    def request = orderService.get(id)
    then:
    request.status == PurchaseOrderStatusKind.CANCELED
  }

  def "취소 - 전송 후 취소"() {
    when:
    determineOrder()
    sendOrder()
    cancelOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotCancelException)
  }

  def "취소 - 진행 후 취소"() {
    when:
    determineOrder()
    sendOrder()

    cancelOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotCancelException)
  }

  def "취소 - 반려 후 취소"() {
    when:
    determineOrder()
    sendOrder()
    rejectOrder()
    cancelOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotCancelException)
  }

  def "취소 - 수령 후 취소"() {
    when:
    determineOrder()
    sendOrder()

    receiveOrder()
    cancelOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotCancelException)
  }

  def "반려 - 작성 후 반려"() {
    when:
    rejectOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotRejectException)
  }

  def "반려 - 확정 후 반려"() {
    when:
    determineOrder()
    rejectOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotRejectException)

  }

  def "반려 - 전송 후 반려"() {
    when:
    determineOrder()
    sendOrder()
    rejectOrder()
    def order = orderService.get(id)
    then:

    order.status == PurchaseOrderStatusKind.REJECTED
  }


  def "반려 - 취소 후 반려"() {
    when:
    cancelOrder()
    rejectOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotRejectException)
  }

  def "반려 - 반려 후 반려"() {
    when:
    determineOrder()
    sendOrder()
    rejectOrder()
    rejectOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotRejectException)
  }

  def "반려 - 수령 후 반려"() {
    when:
    determineOrder()
    sendOrder()

    receiveOrder()
    rejectOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotRejectException)
  }

  def "수령 - 작성 후 수령"() {
    when:
    receiveOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotReceiveException)
  }

  def "수령 - 확정 후 수령"() {
    when:
    determineOrder()
    receiveOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotReceiveException)

  }

  def "수령 - 전송 후 수령"() {
    when:
    determineOrder()
    sendOrder()
    receiveOrder()
    def order = orderService.get(id)
    then:
    order.status == PurchaseOrderStatusKind.RECEIVED
  }

  def "수령 - 취소 후 수령"() {
    when:
    cancelOrder()
    receiveOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotReceiveException)
  }

  def "수령 - 반려 후 수령"() {
    when:
    determineOrder()
    sendOrder()
    rejectOrder()
    receiveOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotReceiveException)
  }

  def "수령 - 수령 후 수령"() {
    when:
    determineOrder()
    sendOrder()
    receiveOrder()
    receiveOrder()
    then:
    thrown(PurchaseOrderExceptions.CannotReceiveException)
  }


}
