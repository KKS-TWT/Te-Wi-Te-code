package spofo.portfolio.controller.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import spofo.portfolio.domain.Portfolio;
import spofo.portfolio.domain.PortfolioStatistic;
import spofo.portfolio.domain.enums.Currency;
import spofo.portfolio.domain.enums.IncludeType;
import spofo.portfolio.domain.enums.PortfolioType;

@Data
@Builder
public class PortfolioStatisticResponse {

    private Long id;
    private Long memberId;
    private String name;
    private String description;
    private Currency currency;
    private IncludeType includeType;
    private PortfolioType type;
    private BigDecimal totalAsset;
    private BigDecimal totalBuy;
    private BigDecimal gain;
    private BigDecimal gainRate;

    public static PortfolioStatisticResponse from(PortfolioStatistic statistic) {
        Portfolio portfolio = statistic.getPortfolio();

        return PortfolioStatisticResponse.builder()
                .id(portfolio.getId())
                .memberId(portfolio.getMemberId())
                .name(portfolio.getName())
                .description(portfolio.getDescription())
                .currency(portfolio.getCurrency())
                .includeType(portfolio.getIncludeType())
                .type(portfolio.getType())
                .totalAsset(statistic.getTotalAsset())
                .totalBuy(statistic.getTotalBuy())
                .gain(statistic.getTotalGain())
                .gainRate(statistic.getGainRate())
                .build();
    }
}
