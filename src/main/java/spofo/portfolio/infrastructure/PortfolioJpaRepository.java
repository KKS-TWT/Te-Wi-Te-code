package spofo.portfolio.infrastructure;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PortfolioJpaRepository extends JpaRepository<PortfolioEntity, Long> {

    List<PortfolioEntity> findByMemberId(Long memberId);

    @Query("select distinct p "
            + "from PortfolioEntity p "
            + "left join fetch p.holdingStockEntities s "
            + "left join fetch s.tradeLogEntities t "
            + "where p.id = :id")
    Optional<PortfolioEntity> findByIdWithTradeLogs(@Param("id") Long id);

    @Query("select p "
            + "from PortfolioEntity p "
            + "left join fetch p.holdingStockEntities s "
            + "left join fetch s.tradeLogEntities t "
            + "where p.memberId = :id")
    List<PortfolioEntity> findByMemberIdWithTradeLogs(@Param("id") Long id);
}
