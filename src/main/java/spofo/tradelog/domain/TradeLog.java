package spofo.tradelog.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import spofo.holdingstock.domain.HoldingStock;
import spofo.stock.domain.Stock;
import spofo.tradelog.domain.enums.TradeType;

@Getter
@Builder
public class TradeLog {

    private final Long id;
    private final TradeType type;
    private final BigDecimal price;
    private final LocalDateTime tradeDate;
    private final BigDecimal quantity;
    private final BigDecimal marketPrice;
    private final LocalDateTime createdAt;
    private final HoldingStock holdingStock;

    public static TradeLog of(TradeLogCreate create, HoldingStock holdingStock, Stock stock) {
        return TradeLog.builder()
                .type(create.getType())
                .price(create.getPrice())
                .tradeDate(create.getTradeDate())
                .quantity(create.getQuantity())
                .marketPrice(stock.getPrice())
                .holdingStock(holdingStock)
                .build();
    }
}