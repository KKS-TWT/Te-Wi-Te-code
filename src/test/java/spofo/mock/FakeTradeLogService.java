package spofo.mock;

import java.util.ArrayList;
import java.util.List;
import spofo.holdingstock.domain.HoldingStock;
import spofo.stock.domain.Stock;
import spofo.tradelog.domain.TradeLog;
import spofo.tradelog.domain.TradeLogCreate;
import spofo.tradelog.domain.TradeLogStatistic;

public class FakeTradeLogService {

    private List<TradeLog> data = new ArrayList<>();

    public TradeLog create(TradeLogCreate request, HoldingStock holdingStock) {
        Stock stock = Stock.builder()
                .code(holdingStock.getStockCode())
                .build();
        TradeLog tradeLog = TradeLog.of(request, holdingStock, stock);
        data.add(tradeLog);
        return tradeLog;
    }

    public List<TradeLogStatistic> getStatistics(Long stockId) {
        return null;
    }

    public void deleteByHoldingStockId(Long id) {

    }

    public void deleteByHoldingStockIds(List<Long> ids) {

    }
}
