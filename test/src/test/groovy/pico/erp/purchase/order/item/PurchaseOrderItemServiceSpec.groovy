package pico.erp.purchase.order.item

import kkojaeh.spring.boot.component.SpringBootTestComponent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.item.ItemId
import pico.erp.item.spec.ItemSpecCode
import pico.erp.project.ProjectId
import pico.erp.purchase.order.*
import pico.erp.shared.ComponentDefinitionServiceLoaderTestComponentSiblingsSupplier
import pico.erp.shared.TestParentApplication
import pico.erp.shared.data.UnitKind
import spock.lang.Specification

@SpringBootTest(classes = [PurchaseOrderApplication, TestConfig])
@SpringBootTestComponent(parent = TestParentApplication, siblingsSupplier = ComponentDefinitionServiceLoaderTestComponentSiblingsSupplier.class)
@Transactional
@Rollback
@ActiveProfiles("test")
class PurchaseOrderItemServiceSpec extends Specification {

  @Autowired
  PurchaseOrderService orderService

  @Autowired
  PurchaseOrderItemService orderItemService

  def orderId = PurchaseOrderId.from("purchase-order-1")

  def id = PurchaseOrderItemId.from("purchase-order-item-1")

  def unknownId = PurchaseOrderItemId.from("unknown")

  def itemId = ItemId.from("item-1")

  def itemSpecCode = ItemSpecCode.NOT_APPLICABLE

  def unitCost = 100

  def projectId = ProjectId.from("sample-project1")

  def unit = UnitKind.EA

  def setup() {

  }

  def cancelOrder() {
    orderService.cancel(
      new PurchaseOrderRequests.CancelRequest(
        id: orderId
      )
    )
  }

  def determineOrder() {
    orderService.determine(
      new PurchaseOrderRequests.DetermineRequest(
        id: orderId
      )
    )
  }

  def rejectOrder() {
    orderService.reject(
      new PurchaseOrderRequests.RejectRequest(
        id: orderId,
        rejectedReason: "납기를 맞출수 없음"
      )
    )
  }

  def receiveOrder() {
    orderService.receive(
      new PurchaseOrderRequests.ReceiveRequest(
        id: orderId
      )
    )
  }

  def sendOrder() {
    orderService.send(
      new PurchaseOrderRequests.SendRequest(
        id: orderId
      )
    )

  }

  def createItem() {
    orderItemService.create(
      new PurchaseOrderItemRequests.CreateRequest(
        id: id,
        orderId: orderId,
        itemId: itemId,
        itemSpecCode: itemSpecCode,
        quantity: 100,
        unit: unit,
        unitCost: unitCost,
        remark: "품목 비고",
        projectId: projectId
      )
    )
  }

  def createItem2() {
    orderItemService.create(
      new PurchaseOrderItemRequests.CreateRequest(
        id: PurchaseOrderItemId.from("purchase-order-item-2"),
        orderId: orderId,
        itemId: itemId,
        itemSpecCode: itemSpecCode,
        quantity: 100,
        unit: unit,
        unitCost: unitCost,
        remark: "품목 비고",
        projectId: projectId
      )
    )
  }

  def updateItem() {
    orderItemService.update(
      new PurchaseOrderItemRequests.UpdateRequest(
        id: id,
        itemSpecCode: itemSpecCode,
        quantity: 200,
        remark: "품목 비고2",
        unitCost: unitCost
      )
    )
  }

  def deleteItem() {
    orderItemService.delete(
      new PurchaseOrderItemRequests.DeleteRequest(
        id: id
      )
    )
  }


  def "존재 - 아이디로 확인"() {
    when:
    createItem()
    def exists = orderItemService.exists(id)

    then:
    exists == true
  }

  def "존재 - 존재하지 않는 아이디로 확인"() {
    when:
    def exists = orderItemService.exists(unknownId)

    then:
    exists == false
  }

  def "조회 - 아이디로 조회"() {
    when:
    createItem()
    def item = orderItemService.get(id)

    then:
    item.id == id
    item.itemId == itemId
    item.orderId == orderId
    item.quantity == 100
    item.remark == "품목 비고"
    item.unit == unit

  }

  def "조회 - 존재하지 않는 아이디로 조회"() {
    when:
    orderItemService.get(unknownId)

    then:
    thrown(PurchaseOrderItemExceptions.NotFoundException)
  }

  def "생성 - 작성 후 생성"() {
    when:
    createItem()
    def items = orderItemService.getAll(orderId)
    then:
    items.size() > 0
  }

  def "생성 - 제출 후 생성"() {
    when:
    createItem()
    determineOrder()
    createItem2()
    then:
    thrown(PurchaseOrderItemExceptions.CannotCreateException)

  }

  def "생성 - 접수 후 생성"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    createItem2()
    then:
    thrown(PurchaseOrderItemExceptions.CannotCreateException)
  }

  def "생성 - 진행 중 생성"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    createItem2()
    then:
    thrown(PurchaseOrderItemExceptions.CannotCreateException)

  }

  def "생성 - 취소 후 생성"() {
    when:
    createItem()
    cancelOrder()
    createItem2()
    then:
    thrown(PurchaseOrderItemExceptions.CannotCreateException)
  }

  def "생성 - 반려 후 생성"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    rejectOrder()
    createItem2()
    then:
    thrown(PurchaseOrderItemExceptions.CannotCreateException)
  }

  def "생성 - 수령 후 생성"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    receiveOrder()
    createItem2()
    then:
    thrown(PurchaseOrderItemExceptions.CannotCreateException)
  }

  def "수정 - 작성 후 수정"() {
    when:
    createItem()
    updateItem()
    def items = orderItemService.getAll(orderId)
    then:
    items.size() > 0
  }

  def "수정 - 제출 후 수정"() {
    when:
    createItem()
    determineOrder()
    updateItem()
    then:
    thrown(PurchaseOrderItemExceptions.CannotUpdateException)

  }

  def "수정 - 접수 후 수정"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    updateItem()
    then:
    thrown(PurchaseOrderItemExceptions.CannotUpdateException)
  }

  def "수정 - 진행 중 수정"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    updateItem()
    then:
    thrown(PurchaseOrderItemExceptions.CannotUpdateException)

  }

  def "수정 - 취소 후 수정"() {
    when:
    createItem()
    cancelOrder()
    updateItem()
    then:
    thrown(PurchaseOrderItemExceptions.CannotUpdateException)
  }

  def "수정 - 반려 후 수정"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    rejectOrder()
    updateItem()
    then:
    thrown(PurchaseOrderItemExceptions.CannotUpdateException)
  }

  def "수정 - 수령 후 수정"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    receiveOrder()
    updateItem()
    then:
    thrown(PurchaseOrderItemExceptions.CannotUpdateException)
  }


  def "삭제 - 작성 후 삭제"() {
    when:
    createItem()
    deleteItem()
    def items = orderItemService.getAll(orderId)
    then:
    items.size() == 0
  }

  def "삭제 - 제출 후 삭제"() {
    when:
    createItem()
    determineOrder()
    deleteItem()
    then:
    thrown(PurchaseOrderItemExceptions.CannotDeleteException)

  }

  def "삭제 - 접수 후 삭제"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    deleteItem()
    then:
    thrown(PurchaseOrderItemExceptions.CannotDeleteException)
  }

  def "삭제 - 진행 중 삭제"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    deleteItem()
    then:
    thrown(PurchaseOrderItemExceptions.CannotDeleteException)

  }

  def "삭제 - 취소 후 삭제"() {
    when:
    createItem()
    cancelOrder()
    deleteItem()
    then:
    thrown(PurchaseOrderItemExceptions.CannotDeleteException)
  }

  def "삭제 - 반려 후 삭제"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    rejectOrder()
    deleteItem()
    then:
    thrown(PurchaseOrderItemExceptions.CannotDeleteException)
  }

  def "삭제 - 수령 후 삭제"() {
    when:
    createItem()
    determineOrder()
    sendOrder()
    receiveOrder()
    deleteItem()
    then:
    thrown(PurchaseOrderItemExceptions.CannotDeleteException)
  }


}
