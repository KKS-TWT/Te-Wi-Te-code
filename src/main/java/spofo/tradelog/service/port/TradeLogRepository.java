package spofo.tradelog.service.port;

import java.util.List;
import spofo.tradelog.domain.TradeLog;

public interface TradeLogRepository {

    TradeLog save(TradeLog tradeLog);

    List<TradeLog> findByHoldingStockEntityId(Long id);

    void deleteByHoldingStockId(Long id);

    void deleteByHoldingStockEntityIdIn(List<Long> ids);

    void deleteAll();
}