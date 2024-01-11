package spofo.tradelog.controller.port;

import java.util.List;
import spofo.holdingstock.domain.HoldingStock;
import spofo.tradelog.domain.TradeLog;
import spofo.tradelog.domain.TradeLogCreate;
import spofo.tradelog.domain.TradeLogStatistic;

public interface TradeLogService {

    TradeLog create(TradeLogCreate request, HoldingStock holdingStock);

    List<TradeLogStatistic> getStatistics(Long stockId);

    void deleteByHoldingStockId(Long id);

    void deleteByHoldingStockIds(List<Long> ids);
}
