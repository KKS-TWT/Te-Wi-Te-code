package spofo.mock;

import java.util.ArrayList;
import java.util.List;
import spofo.holdingstock.domain.HoldingStock;
import spofo.stock.domain.Stock;
import spofo.tradelog.controller.port.TradeLogService;
import spofo.tradelog.domain.TradeLog;
import spofo.tradelog.domain.TradeLogCreate;
import spofo.tradelog.domain.TradeLogStatistic;

public class FakeTradeLogService implements TradeLogService {

    private List<TradeLog> data = new ArrayList<>();

    @Override
    public TradeLog create(TradeLogCreate request, HoldingStock holdingStock) {
        Stock stock = Stock.builder()
                .code(holdingStock.getStockCode())
                .build();
        TradeLog tradeLog = TradeLog.of(request, holdingStock, stock);
        data.add(tradeLog);
        return tradeLog;
    }

    @Override
    public List<TradeLogStatistic> getStatistics(Long stockId) {
        return null;
    }

    @Override
    public void deleteByHoldingStockId(Long id) {

    }

    @Override
    public void deleteByHoldingStockIds(List<Long> ids) {

    }
}
