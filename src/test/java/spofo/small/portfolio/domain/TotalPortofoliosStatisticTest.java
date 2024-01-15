package spofo.small.portfolio.domain;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static spofo.global.component.utils.CommonUtils.getBD;
import static spofo.portfolio.domain.enums.IncludeType.N;
import static spofo.portfolio.domain.enums.IncludeType.Y;
import static spofo.tradelog.domain.enums.TradeType.BUY;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.holdingstock.domain.HoldingStock;
import spofo.portfolio.domain.Portfolio;
import spofo.portfolio.domain.PortfolioStatistic;
import spofo.portfolio.domain.TotalPortfoliosStatistic;
import spofo.stock.domain.Stock;
import spofo.tradelog.domain.TradeLog;

class TotalPortofoliosStatisticTest {

    private final String TEST_STOCK_CODE = "000660";

    @Test
    @DisplayName("여러 개의 포트폴리오는 통계 계산에 포함된다.")
    void createPortfoliosStatisticWithPortfolios() {
        // given
        TradeLog tradeLog = getTradeLog();
        HoldingStock holdingStock = getHoldingStock(tradeLog);

        Portfolio portfolio1 = Portfolio.builder()
                .includeType(Y)
                .holdingStocks(List.of(holdingStock))
                .build();

        Portfolio portfolio2 = Portfolio.builder()
                .includeType(Y)
                .holdingStocks(List.of(holdingStock, holdingStock))
                .build();

        PortfolioStatistic statistic1 = PortfolioStatistic.of(portfolio1, getStockMap());
        PortfolioStatistic statistic2 = PortfolioStatistic.of(portfolio2, getStockMap());

        // when
        TotalPortfoliosStatistic statistic = TotalPortfoliosStatistic.of(
                List.of(statistic1, statistic2));

        // then
        assertThat(statistic).extracting(
                TotalPortfoliosStatistic::getTotalAsset,
                TotalPortfoliosStatistic::getGain,
                TotalPortfoliosStatistic::getGainRate
        ).containsExactly(
                getBD(198_000),
                getBD(99_000),
                getBD(100)
        );
    }

    @Test
    @DisplayName("포함 여부가 Y인 포트폴리오는 통계 계산에 포함된다.")
    void createPortfoliosStatisticWithIncludeIsYPortfolio() {
        // given
        TradeLog tradeLog = getTradeLog();
        HoldingStock holdingStock = getHoldingStock(tradeLog);

        Portfolio portfolio = Portfolio.builder()
                .includeType(Y)
                .holdingStocks(List.of(holdingStock))
                .build();

        PortfolioStatistic statistic1 = PortfolioStatistic.of(portfolio, getStockMap());

        // when
        TotalPortfoliosStatistic statistic = TotalPortfoliosStatistic.of(
                List.of(statistic1));

        // then
        assertThat(statistic).extracting(
                TotalPortfoliosStatistic::getTotalAsset,
                TotalPortfoliosStatistic::getGain,
                TotalPortfoliosStatistic::getGainRate
        ).containsExactly(
                getBD(66_000),
                getBD(33_000),
                getBD(100)
        );
    }

    @Test
    @DisplayName("포함여부가 N인 포트폴리오는 통계 계산에서 제외된다.")
    void createPortfoliosStatisticWithIncludeIsNPortfolio() {
        // given
        TradeLog tradeLog = getTradeLog();
        HoldingStock holdingStock = getHoldingStock(tradeLog);

        Portfolio portfolio = Portfolio.builder()
                .includeType(N)
                .holdingStocks(List.of(holdingStock))
                .build();

        PortfolioStatistic statistic1 = PortfolioStatistic.of(portfolio, getStockMap());

        // when
        TotalPortfoliosStatistic statistic = TotalPortfoliosStatistic.of(
                List.of(statistic1));

        // then
        assertThat(statistic).extracting(
                TotalPortfoliosStatistic::getTotalAsset,
                TotalPortfoliosStatistic::getGain,
                TotalPortfoliosStatistic::getGainRate
        ).containsExactly(ZERO, ZERO, ZERO);
    }

    @Test
    @DisplayName("포트폴리오 통계가 없을 때 전체 포트폴리오 통계의 모든 값은 0이다.")
    void portfoliosStatisticWithoutAnyStatistic() {
        // given
        Portfolio portfolio1 = Portfolio.builder()
                .memberId(1L)
                .includeType(Y)
                .build();

        PortfolioStatistic statistic1 = PortfolioStatistic.of(portfolio1, getStockMap());

        // when
        TotalPortfoliosStatistic statistic = TotalPortfoliosStatistic.of(List.of(statistic1));

        // then
        assertThat(statistic).extracting(
                TotalPortfoliosStatistic::getTotalAsset,
                TotalPortfoliosStatistic::getGain,
                TotalPortfoliosStatistic::getGainRate
        ).containsExactly(ZERO, ZERO, ZERO);
    }

    private HoldingStock getHoldingStock(TradeLog tradeLog) {
        return HoldingStock.builder()
                .stockCode(TEST_STOCK_CODE)
                .tradeLogs(List.of(tradeLog))
                .build();
    }

    private TradeLog getTradeLog() {
        return TradeLog.builder()
                .type(BUY)
                .price(getBD(33000))
                .quantity(getBD(1))
                .build();
    }

    private Map<String, Stock> getStockMap() {
        Stock stock = Stock.builder()
                .code(TEST_STOCK_CODE)
                .name("SK하이닉스")
                .price(getBD(66000))
                .sector("반도체")
                .build();

        return Map.of(TEST_STOCK_CODE, stock);
    }
}
