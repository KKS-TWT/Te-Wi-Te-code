package spofo.portfolio.domain;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static spofo.global.component.utils.CommonUtils.format;
import static spofo.global.component.utils.CommonUtils.getGlobalScale;
import static spofo.global.component.utils.CommonUtils.isZero;
import static spofo.global.component.utils.CommonUtils.toPercent;
import static spofo.portfolio.domain.enums.IncludeType.Y;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TotalPortfoliosStatistic {

    private BigDecimal totalAsset; // 포트폴리오 자산들의 합계
    private BigDecimal gain; // 포트폴리오 수익들의 합계
    private BigDecimal gainRate; // 총 수익 / 총 자산
    private BigDecimal dailyGainRate;

    public static TotalPortfoliosStatistic of(List<PortfolioStatistic> portfolioStatistics) {
        BigDecimal totalAsset = ZERO;
        BigDecimal totalBuy = ZERO;
        BigDecimal totalGain = ZERO;
        BigDecimal gainRate = ZERO;

        if (portfolioStatistics != null) {
            for (PortfolioStatistic statistic : portfolioStatistics) {
                if (statistic.getPortfolio().getIncludeType() == Y) {
                    totalAsset = totalAsset.add(statistic.getTotalAsset());
                    totalBuy = totalBuy.add(statistic.getTotalBuy());
                    totalGain = totalGain.add(statistic.getTotalGain());
                }
            }
            if (!isZero(totalBuy)) {
                gainRate = toPercent(totalGain.divide(totalBuy, getGlobalScale(), HALF_UP));
            }
        }

        return TotalPortfoliosStatistic.builder()
                .totalAsset(format(totalAsset))
                .gain(format(totalGain))
                .gainRate(format(gainRate))
                //.dailyGainRate(portfolio)
                .build();
    }
}
