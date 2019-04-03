package pico.erp.purchase.order;

import static org.springframework.util.StringUtils.isEmpty;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import kkojaeh.spring.boot.component.ComponentBean;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.purchase.order.PurchaseOrderView.Filter;
import pico.erp.purchase.order.item.QPurchaseOrderItemEntity;
import pico.erp.shared.jpa.QueryDslJpaSupport;

@Service
@ComponentBean
@Transactional(readOnly = true)
@Validated
public class PurchaseOrderQueryJpa implements PurchaseOrderQuery {


  private final QPurchaseOrderEntity order = QPurchaseOrderEntity.purchaseOrderEntity;

  private final QPurchaseOrderItemEntity orderItem = QPurchaseOrderItemEntity.purchaseOrderItemEntity;

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private QueryDslJpaSupport queryDslJpaSupport;

  @Override
  public Page<PurchaseOrderView> retrieve(Filter filter, Pageable pageable) {
    val query = new JPAQuery<PurchaseOrderView>(entityManager);
    val select = Projections.bean(PurchaseOrderView.class,
      order.id,
      order.code,
      order.chargerId,
      order.receiverId,
      order.supplierId,
      order.receiveAddress,
      order.dueDate,
      order.determinedDate,
      order.receivedDate,
      order.sentDate,
      order.rejectedDate,
      order.canceledDate,
      order.status,
      order.createdBy,
      order.createdDate
    );

    query.select(select);
    query.from(order);

    val builder = new BooleanBuilder();

    if (!isEmpty(filter.getCode())) {
      builder.and(order.code.value
        .likeIgnoreCase(queryDslJpaSupport.toLikeKeyword("%", filter.getCode(), "%")));
    }

    if (filter.getReceiverId() != null) {
      builder.and(order.receiverId.eq(filter.getReceiverId()));
    }

    if (filter.getChargerId() != null) {
      builder.and(order.chargerId.eq(filter.getChargerId()));
    }

    if (filter.getProjectId() != null) {
      builder.and(
        order.id.in(
          JPAExpressions.select(orderItem.orderId)
            .from(orderItem)
            .where(orderItem.projectId.eq(filter.getProjectId()))
        )
      );
    }

    if (filter.getItemId() != null) {
      builder.and(
        order.id.in(
          JPAExpressions.select(orderItem.orderId)
            .from(orderItem)
            .where(orderItem.itemId.eq(filter.getItemId()))
        )
      );
    }

    if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) {
      builder.and(order.status.in(filter.getStatuses()));
    }

    if (filter.getStartDueDate() != null) {
      builder.and(order.dueDate.goe(filter.getStartDueDate()));
    }
    if (filter.getEndDueDate() != null) {
      builder.and(order.dueDate.loe(filter.getEndDueDate()));
    }

    query.where(builder);
    return queryDslJpaSupport.paging(query, pageable, select);
  }
}
