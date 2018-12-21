package pico.erp.purchase.order;

import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
interface PurchaseOrderEntityRepository extends
  CrudRepository<PurchaseOrderEntity, PurchaseOrderId> {

  @Query("SELECT COUNT(pr) FROM PurchaseOrder pr WHERE pr.createdDate >= :begin AND pr.createdDate <= :end")
  long countCreatedBetween(@Param("begin") OffsetDateTime begin, @Param("end") OffsetDateTime end);

}

@Repository
@Transactional
public class PurchaseOrderRepositoryJpa implements PurchaseOrderRepository {

  @Autowired
  private PurchaseOrderEntityRepository repository;

  @Autowired
  private PurchaseOrderMapper mapper;

  @Override
  public long countCreatedBetween(OffsetDateTime begin, OffsetDateTime end) {
    return repository.countCreatedBetween(begin, end);
  }

  @Override
  public PurchaseOrder create(PurchaseOrder plan) {
    val entity = mapper.jpa(plan);
    val created = repository.save(entity);
    return mapper.jpa(created);
  }

  @Override
  public void deleteBy(PurchaseOrderId id) {
    repository.delete(id);
  }

  @Override
  public boolean exists(PurchaseOrderId id) {
    return repository.exists(id);
  }

  @Override
  public Optional<PurchaseOrder> findBy(PurchaseOrderId id) {
    return Optional.ofNullable(repository.findOne(id))
      .map(mapper::jpa);
  }

  @Override
  public void update(PurchaseOrder plan) {
    val entity = repository.findOne(plan.getId());
    mapper.pass(mapper.jpa(plan), entity);
    repository.save(entity);
  }
}
