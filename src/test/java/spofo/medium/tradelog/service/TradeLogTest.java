package spofo.medium.tradelog.service;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static spofo.global.component.utils.CommonUtils.getBD;
import static spofo.portfolio.domain.enums.Currency.KRW;
import static spofo.portfolio.domain.enums.PortfolioType.REAL;
import static spofo.tradelog.domain.enums.TradeType.BUY;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.holdingstock.domain.HoldingStock;
import spofo.holdingstock.domain.HoldingStockCreate;
import spofo.portfolio.domain.Portfolio;
import spofo.portfolio.domain.PortfolioCreate;
import spofo.stock.domain.Stock;
import spofo.support.service.ServiceTestSupport;
import spofo.tradelog.domain.TradeLog;
import spofo.tradelog.domain.TradeLogCreate;
import spofo.tradelog.domain.TradeLogStatistic;

class TradeLogTest extends ServiceTestSupport {

    private static final String PORTFOLIO_CREATE_NAME = "포트폴리오 생성";
    private static final String PORTFOLIO_CREATE_DESC = "포트폴리오 생성입니다.";
    private static final String TEST_STOCK_CODE = "005930";
    private static final Long MEMBER_ID = 1L;

    @Test
    @DisplayName("매매이력 1건을 생성한다.")
    void createTradeLog() {
        // given
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());

        HoldingStock savedHoldingStock = savedHoldingStock();
        TradeLogCreate tradeLogCreate = getTradeLogCreate(getBD(33_000), ONE);

        // when
        TradeLog savedTradeLog = tradeLogService.create(tradeLogCreate, savedHoldingStock);

        // then
        assertThat(savedTradeLog.getId()).isNotNull();
        assertThat(savedTradeLog.getPrice()).isEqualTo(getBD(33_000));
        assertThat(savedTradeLog.getType()).isEqualTo(BUY);
        assertThat(savedTradeLog.getQuantity()).isEqualTo(ONE);
        assertThat(savedTradeLog.getMarketPrice()).isEqualTo(getBD(66_000));
    }

    @Test
    @DisplayName("1개의 종목이력 ID로 1개 매매이력 통계를 조회한다.")
    void getTradeLogStatistics1() {
        // given
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());

        HoldingStock savedHoldingStock = savedHoldingStock();

        // when
        List<TradeLogStatistic> statistics = tradeLogService.getStatistics(
                savedHoldingStock.getId());

        // then
        assertThat(statistics)
                .hasSize(1)
                .extracting("type", "avgPrice", "quantity", "gain", "totalPrice")
                .contains(
                        tuple(BUY, getBD(33_000), ONE, getBD(33_000), getBD(33_000))
                );
    }

    @Test
    @DisplayName("1개의 종목이력 ID로 2개 매매이력 통계를 조회한다.")
    void getTradeLogStatistics2() {
        // given
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());

        HoldingStock savedHoldingStock = savedHoldingStock();
        TradeLogCreate tradeLogCreate = getTradeLogCreate(getBD(66_000), getBD(2));
        tradeLogService.create(tradeLogCreate, savedHoldingStock);

        // when
        List<TradeLogStatistic> statistics = tradeLogService.getStatistics(
                savedHoldingStock.getId());

        // then
        assertThat(statistics)
                .hasSize(2)
                .extracting("type", "avgPrice", "quantity", "gain", "totalPrice")
                .containsExactlyInAnyOrder(
                        tuple(BUY, getBD(33_000), ONE, getBD(33_000), getBD(33_000)),
                        tuple(BUY, getBD(66_000), getBD(2), ZERO, getBD(132_000))
                );
    }

    private HoldingStock savedHoldingStock() {
        PortfolioCreate portfolioCreate = getPortfolioCreate();
        HoldingStockCreate holdingStockCreate = getHoldingStockCreate();
        TradeLogCreate tradeLogCreate = getTradeLogCreate(getBD(33_000), ONE);

        Portfolio savedPortfolio = portfolioService.create(portfolioCreate, MEMBER_ID);
        return holdingStockService.create(holdingStockCreate, tradeLogCreate, savedPortfolio);
    }

    private PortfolioCreate getPortfolioCreate() {
        return PortfolioCreate.builder()
                .name(PORTFOLIO_CREATE_NAME)
                .description(PORTFOLIO_CREATE_DESC)
                .currency(KRW)
                .type(REAL)
                .build();
    }

    private HoldingStockCreate getHoldingStockCreate() {
        return HoldingStockCreate.builder()
                .stockCode(TEST_STOCK_CODE)
                .build();
    }

    private TradeLogCreate getTradeLogCreate(BigDecimal price, BigDecimal quantity) {
        return TradeLogCreate.builder()
                .type(BUY)
                .price(price)
                .tradeDate(now())
                .quantity(quantity)
                .build();
    }

    private Stock getStock() {
        return Stock.builder()
                .code(TEST_STOCK_CODE)
                .price(getBD(66_000))
                .build();
    }
}
