package spofo.holdingstock.infrastructure;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spofo.portfolio.infrastructure.PortfolioEntity;

@Repository
public interface HoldingStockJpaRepository extends JpaRepository<HoldingStockEntity, Long> {

    @Query("select distinct h "
            + "from HoldingStockEntity h "
            + "left join fetch h.tradeLogEntities s "
            + "where h.portfolioEntity.id = :portfolioId")
    List<HoldingStockEntity> findByPortfolioId(@Param("portfolioId") Long portfolioId);

    void deleteByPortfolioEntityId(Long id);

    Optional<HoldingStockEntity> findByPortfolioEntityAndStockCode
            (PortfolioEntity portfolioEntity, String stockCode);
}