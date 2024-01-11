package spofo.portfolio.controller.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalPortfolioResponse {

    private BigDecimal totalAsset; // 총 자산
    private BigDecimal gain; // 평가 손익
    private BigDecimal gainRate; // 수익률
    private BigDecimal dailyGainRate; // 일간 수익률

    public static TotalPortfolioResponse from(BigDecimal totalAsset, BigDecimal gain,
            BigDecimal gainRate, BigDecimal dailyGainRate) {
        return TotalPortfolioResponse.builder()
                .totalAsset(totalAsset)
                .gain(gain)
                .gainRate(gainRate)
                .dailyGainRate(dailyGainRate)
                .build();
    }
}
