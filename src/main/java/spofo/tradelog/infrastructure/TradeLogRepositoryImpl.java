package spofo.tradelog.infrastructure;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import spofo.tradelog.domain.TradeLog;
import spofo.tradelog.service.port.TradeLogRepository;

@Repository
@RequiredArgsConstructor
public class TradeLogRepositoryImpl implements TradeLogRepository {

    private final TradeLogJpaRepository tradeLogJpaRepository;

    @Override
    public TradeLog save(TradeLog tradeLog) {
        return tradeLogJpaRepository.save(TradeLogEntity.from(tradeLog)).toModel();
    }

    @Override
    public List<TradeLog> findByHoldingStockEntityId(Long id) {
        return tradeLogJpaRepository.findByHoldingStockEntityId(id).stream()
                .map(TradeLogEntity::toModel)
                .toList();
    }

    @Override
    public void deleteByHoldingStockId(Long id) {
        tradeLogJpaRepository.deleteByHoldingStockEntityId(id);
    }

    @Override
    public void deleteByHoldingStockEntityIdIn(List<Long> ids) {
        tradeLogJpaRepository.deleteByHoldingStockEntityIdIn(ids);
    }

    @Override
    public void deleteAll() {
        tradeLogJpaRepository.deleteAll();
    }
}
