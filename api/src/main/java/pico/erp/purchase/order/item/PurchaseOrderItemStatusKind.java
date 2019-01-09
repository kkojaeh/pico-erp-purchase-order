package pico.erp.purchase.order.item;

import pico.erp.shared.data.LocalizedNameable;

public enum PurchaseOrderItemStatusKind implements LocalizedNameable {

  /**
   * 작성중
   */
  DRAFT,

  /**
   * 확정(전송대기)
   */
  DETERMINED,

  /**
   * 전송 완료
   */
  SENT,

  /**
   * 취소됨
   */
  CANCELED,

  /**
   * 거부됨
   */
  REJECTED,

  /**
   * 수령중
   */
  IN_RECEIVING,

  /**
   * 납품완료
   */
  RECEIVED;

  public boolean isCancelable() {
    return this == DRAFT;
  }

  public boolean isDeterminable() {
    return this == DRAFT;
  }

  public boolean isReceivable() {
    return this == SENT || this == IN_RECEIVING;
  }

  public boolean isRejectable() {
    return this == SENT;
  }

  public boolean isSendable() {
    return this == DETERMINED;
  }

  public boolean isUpdatable() {
    return this == DRAFT;
  }


}
