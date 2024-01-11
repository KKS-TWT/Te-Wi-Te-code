package spofo.tradelog.domain;

import static java.math.BigDecimal.ZERO;
import static spofo.global.component.utils.CommonUtils.format;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import spofo.tradelog.domain.enums.TradeType;

@Getter
@Builder
public class TradeLogStatistic {

    private TradeType type;
    private LocalDateTime tradeDate;
    private BigDecimal avgPrice;
    private BigDecimal quantity;
    private BigDecimal gain;
    private BigDecimal totalPrice;

    public static TradeLogStatistic of(TradeLog tradeLog) {
        TradeType type = tradeLog.getType();
        LocalDateTime tradeDate = tradeLog.getTradeDate();
        BigDecimal avgPrice = tradeLog.getPrice();
        BigDecimal quantity = tradeLog.getQuantity();
        BigDecimal gain = ZERO;
        BigDecimal totalPrice = ZERO;
        BigDecimal marketPrice = tradeLog.getMarketPrice();

        totalPrice = avgPrice.multiply(quantity);
        gain = marketPrice.multiply(quantity).subtract(totalPrice);

        return TradeLogStatistic.builder()
                .type(type)
                .tradeDate(tradeDate)
                .avgPrice(format(avgPrice))
                .quantity(format(quantity))
                .gain(format(gain))
                .totalPrice(format(totalPrice))
                .build();
    }
}
