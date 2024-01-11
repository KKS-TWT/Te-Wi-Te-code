package spofo.holdingstock.controller.request;

import static spofo.tradelog.domain.enums.TradeType.BUY;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import spofo.holdingstock.domain.HoldingStockCreate;
import spofo.tradelog.domain.TradeLogCreate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoldingStockRequest {

    @NotBlank(message = "종목코드는 필수 입력입니다.")
    private String code;

    @NotNull(message = "매수날짜 필수 입력입니다.")
    private LocalDateTime tradeDate;

    @Positive(message = "수량은 0보다 커야 합니다.")
    @NotNull(message = "수량은 필수 입력입니다.")
    private BigDecimal quantity;

    @Positive(message = "평균단가는 0보다 커야 합니다.")
    @NotNull(message = "평균단가는 필수 입력입니다.")
    private BigDecimal avgPrice;

    public HoldingStockCreate toHoldingStockCreate() {
        return HoldingStockCreate.builder()
                .stockCode(code)
                .build();
    }

    public TradeLogCreate toTradeLogCreate() {
        return TradeLogCreate.builder()
                .type(BUY)
                .price(avgPrice)
                .tradeDate(tradeDate)
                .quantity(quantity)
                .build();
    }
}
