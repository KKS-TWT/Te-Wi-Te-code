package spofo.tradelog.controller.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import spofo.tradelog.domain.TradeLogStatistic;
import spofo.tradelog.domain.enums.TradeType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeLogResponse {

    private TradeType type; // 종류
    private LocalDateTime tradeDate; // 날짜
    private BigDecimal avgPrice; // 매매가
    private BigDecimal quantity; // 수량
    private BigDecimal gain; // 실현 수익
    private BigDecimal totalPrice; // 금액

    public static TradeLogResponse from(TradeLogStatistic tradeLogStatistic) {
        return TradeLogResponse.builder()
                .type(tradeLogStatistic.getType())
                .tradeDate(tradeLogStatistic.getTradeDate())
                .avgPrice(tradeLogStatistic.getAvgPrice())
                .quantity(tradeLogStatistic.getQuantity())
                .gain(tradeLogStatistic.getGain())
                .totalPrice(tradeLogStatistic.getTotalPrice())
                .build();
    }
}
