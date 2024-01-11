package spofo.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import spofo.tradelog.domain.TradeLog;
import spofo.tradelog.service.port.TradeLogRepository;

public class FakeTradeLogRepository implements TradeLogRepository {

    private long autoIncrement = 0;
    List<TradeLog> data = new ArrayList<>();

    @Override
    public TradeLog save(TradeLog tradeLog) {
        if (tradeLog.getId() == null || tradeLog.getId() == 0) {
            TradeLog newTradeLog = TradeLog.builder()
                    .id(++autoIncrement)
                    .type(tradeLog.getType())
                    .price(tradeLog.getPrice())
                    .tradeDate(tradeLog.getTradeDate())
                    .quantity(tradeLog.getQuantity())
                    .marketPrice(tradeLog.getMarketPrice())
                    .createdAt(tradeLog.getCreatedAt())
                    .holdingStock(tradeLog.getHoldingStock())
                    .build();
            data.add(newTradeLog);
            return newTradeLog;
        } else {
            data.removeIf(item -> Objects.equals(item.getId(), tradeLog.getId()));
            data.add(tradeLog);
            return tradeLog;
        }
    }

    @Override
    public List<TradeLog> findByHoldingStockEntityId(Long id) {
        return data.stream()
                .filter(item -> item.getHoldingStock().getId().equals(id))
                .toList();
    }

    @Override
    public void deleteByHoldingStockId(Long id) {
        data.removeIf(item -> Objects.equals(item.getHoldingStock().getId(), id));
    }

    @Override
    public void deleteByHoldingStockEntityIdIn(List<Long> ids) {
        ids.forEach(
                id -> data.removeIf(item -> Objects.equals(item.getHoldingStock().getId(), id)));
    }

    @Override
    public void deleteAll() {
        data.clear();
    }
}
