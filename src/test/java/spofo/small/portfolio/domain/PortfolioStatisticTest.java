package spofo.small.portfolio.domain;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static spofo.global.component.utils.CommonUtils.getBD;
import static spofo.portfolio.domain.enums.IncludeType.Y;
import static spofo.tradelog.domain.enums.TradeType.BUY;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.holdingstock.domain.HoldingStock;
import spofo.portfolio.domain.Portfolio;
import spofo.portfolio.domain.PortfolioStatistic;
import spofo.stock.domain.Stock;
import spofo.tradelog.domain.TradeLog;

class PortfolioStatisticTest {

    private final String TEST_STOCK_CODE = "000660";

    @Test
    @DisplayName("포트폴리오로 포트폴리오 통계를 만든다.")
    void createPortfolioStatisticFromPortfolio() {
        // given
        Long memberId = 1L;
        TradeLog tradeLog = getTradeLog();
        HoldingStock holdingStock = getHoldingStock(tradeLog);

        Portfolio portfolio = Portfolio.builder()
                .id(1L)
                .memberId(memberId)
                .includeType(Y)
                .holdingStocks(List.of(holdingStock))
                .build();

        // when
        PortfolioStatistic statistic = PortfolioStatistic.of(portfolio, getStockMap());

        // then
        assertThat(statistic).extracting(
                s -> s.getPortfolio().getId(),
                PortfolioStatistic::getTotalAsset, PortfolioStatistic::getTotalBuy,
                PortfolioStatistic::getTotalGain, PortfolioStatistic::getGainRate
        ).containsExactly(
                1L,
                getBD(66_000), getBD(33_000),
                getBD(33_000), getBD(100)
        );
    }

    @Test
    @DisplayName("보유종목이 없을 때 포트폴리오 통계의 모든 값들은 0이다.")
    void PortfolioStatisticWithoutHoldingStock() {
        // given
        Portfolio portfolio = Portfolio.builder()
                .includeType(Y)
                .build();

        // when
        PortfolioStatistic statistic = PortfolioStatistic.of(portfolio, getStockMap());

        // then
        assertThat(statistic).extracting(
                PortfolioStatistic::getTotalAsset, PortfolioStatistic::getTotalBuy,
                PortfolioStatistic::getTotalGain, PortfolioStatistic::getGainRate
        ).containsExactly(ZERO, ZERO, ZERO, ZERO);
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
