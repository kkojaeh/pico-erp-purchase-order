package pico.erp.purchase.order.item;

import java.util.Optional;
import java.util.stream.Stream;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pico.erp.purchase.order.PurchaseOrderId;
import pico.erp.purchase.request.PurchaseRequestId;

@Repository
interface PurchaseOrderItemEntityRepository extends
  CrudRepository<PurchaseOrderItemEntity, PurchaseOrderItemId> {

  @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM PurchaseOrderItem i WHERE i.requestId = :requestId")
  boolean exists(@Param("requestId") PurchaseRequestId requestId);

  @Query("SELECT i FROM PurchaseOrderItem i WHERE i.orderId = :orderId ORDER BY i.createdDate")
  Stream<PurchaseOrderItemEntity> findAllBy(@Param("orderId") PurchaseOrderId orderId);

  @Query("SELECT i FROM PurchaseOrderItem i WHERE i.requestId = :requestId")
  PurchaseOrderItemEntity findBy(@Param("requestId") PurchaseRequestId requestId);

}

@Repository
@Transactional
public class PurchaseOrderItemRepositoryJpa implements PurchaseOrderItemRepository {

  @Autowired
  private PurchaseOrderItemEntityRepository repository;

  @Autowired
  private PurchaseOrderItemMapper mapper;

  @Override
  public PurchaseOrderItem create(PurchaseOrderItem item) {
    val entity = mapper.jpa(item);
    val created = repository.save(entity);
    return mapper.jpa(created);
  }

  @Override
  public void deleteBy(PurchaseOrderItemId id) {
    repository.delete(id);
  }

  @Override
  public boolean exists(PurchaseOrderItemId id) {
    return repository.exists(id);
  }

  @Override
  public boolean exists(PurchaseRequestId requestId) {
    return repository.exists(requestId);
  }

  @Override
  public Stream<PurchaseOrderItem> findAllBy(PurchaseOrderId orderId) {
    return repository.findAllBy(orderId)
      .map(mapper::jpa);
  }

  @Override
  public Optional<PurchaseOrderItem> findBy(PurchaseOrderItemId id) {
    return Optional.ofNullable(repository.findOne(id))
      .map(mapper::jpa);
  }

  @Override
  public Optional<PurchaseOrderItem> findBy(PurchaseRequestId requestId) {
    return Optional.ofNullable(repository.findBy(requestId))
      .map(mapper::jpa);
  }

  @Override
  public void update(PurchaseOrderItem planItem) {
    val entity = repository.findOne(planItem.getId());
    mapper.pass(mapper.jpa(planItem), entity);
    repository.save(entity);
  }
}
