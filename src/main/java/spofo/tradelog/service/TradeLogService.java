package spofo.tradelog.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spofo.holdingstock.domain.HoldingStock;
import spofo.stock.domain.Stock;
import spofo.stock.service.StockServerService;
import spofo.tradelog.domain.TradeLog;
import spofo.tradelog.domain.TradeLogCreate;
import spofo.tradelog.domain.TradeLogStatistic;
import spofo.tradelog.service.port.TradeLogRepository;

@Service
@RequiredArgsConstructor
public class TradeLogService {

    private final TradeLogRepository tradeLogRepository;
    private final StockServerService stockServerService;

    public TradeLog create(TradeLogCreate request, HoldingStock holdingStock) {
        Stock stock = stockServerService.getStock(holdingStock.getStockCode());
        TradeLog tradeLog = TradeLog.of(request, holdingStock, stock);
        return tradeLogRepository.save(tradeLog);
    }

    public List<TradeLogStatistic> getStatistics(Long stockId) {
        List<TradeLog> tradeLogs = tradeLogRepository.findByHoldingStockEntityId(stockId);
        return tradeLogs.stream()
                .map(TradeLogStatistic::of)
                .toList();
    }

    public void deleteByHoldingStockId(Long id) {
        tradeLogRepository.deleteByHoldingStockId(id);
    }

    public void deleteByHoldingStockIds(List<Long> ids) {
        tradeLogRepository.deleteByHoldingStockEntityIdIn(ids);
    }
}
