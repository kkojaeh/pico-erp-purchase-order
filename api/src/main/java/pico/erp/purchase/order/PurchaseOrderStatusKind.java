package pico.erp.purchase.order;

import pico.erp.shared.data.LocalizedNameable;

public enum PurchaseOrderStatusKind implements LocalizedNameable {

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
   * 납품완료
   */
  RECEIVED;

  public boolean isUpdatable() {
    return this == DRAFT;
  }

  public boolean isDeterminable() {
    return this == DRAFT;
  }

  public boolean isSendable() {
    return this == DETERMINED;
  }

  public boolean isCancelable() {
    return this == DRAFT || this == DETERMINED;
  }

  public boolean isRejectable() {
    return this == SENT;
  }

  public boolean isReceivable() {
    return this == SENT;
  }


}
