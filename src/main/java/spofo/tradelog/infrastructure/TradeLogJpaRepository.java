package spofo.tradelog.infrastructure;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeLogJpaRepository extends JpaRepository<TradeLogEntity, Long> {

    List<TradeLogEntity> findByHoldingStockEntityId(Long id);

    void deleteByHoldingStockEntityId(Long id);

    void deleteByHoldingStockEntityIdIn(List<Long> ids);
}
