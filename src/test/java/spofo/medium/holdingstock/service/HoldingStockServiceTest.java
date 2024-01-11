package spofo.medium.holdingstock.service;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static spofo.global.component.utils.CommonUtils.getBD;
import static spofo.global.domain.exception.ErrorCode.HOLDING_STOCK_NOT_FOUND;
import static spofo.tradelog.domain.enums.TradeType.BUY;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.global.domain.exception.HoldingStockNotFound;
import spofo.holdingstock.domain.HoldingStock;
import spofo.holdingstock.domain.HoldingStockCreate;
import spofo.holdingstock.domain.HoldingStockStatistic;
import spofo.portfolio.domain.Portfolio;
import spofo.stock.domain.Stock;
import spofo.support.service.ServiceTestSupport;
import spofo.tradelog.domain.TradeLog;
import spofo.tradelog.domain.TradeLogCreate;

public class HoldingStockServiceTest extends ServiceTestSupport {

    private static final String TEST_STOCK_CODE = "101010";

    @Test
    @DisplayName("포트폴리오 1개에 속한 보유종목을 조회한다.")
    void getByPortfolioId() {
        // given
        Portfolio savedPortfolio = portfolioRepository.save(getPortfolio());
        HoldingStock holdingStock = getHoldingStock(savedPortfolio);

        HoldingStock savedHoldingStock = holdingStockRepository.save(holdingStock);

        // when
        List<HoldingStock> holdingStocks =
                holdingStockService.getByPortfolioId(savedPortfolio.getId());

        // then
        assertThat(holdingStocks).hasSize(1)
                .extracting("id", "stockCode", "portfolio")
                .containsExactlyInAnyOrder(
                        tuple(savedHoldingStock.getId(), TEST_STOCK_CODE, null)
                );
    }

    @Test
    @DisplayName("포트폴리오 1개에 속한 보유종목이 없으면 비어있는 리스트를 반환한다.")
    void getByPortfolioIdWithNoHoldingStock() {
        // given
        Portfolio savedPortfolio = portfolioRepository.save(getPortfolio());

        // when
        List<HoldingStock> holdingStocks =
                holdingStockService.getByPortfolioId(savedPortfolio.getId());

        // then
        assertThat(holdingStocks).isEmpty();
    }

    @Test
    @DisplayName("포트폴리오 id가 유효하지 않으면 비어있는 리스트를 반환한다.")
    void getByPortfolioIdWithNoNotValidPortfolioId() {
        // given
        Long portfolioId = null;

        // when
        List<HoldingStock> holdingStocks = holdingStockService.getByPortfolioId(portfolioId);

        // then
        assertThat(holdingStocks).isEmpty();
    }

    @Test
    @DisplayName("보유종목 1건을 조회한다.")
    void getHoldingStock() {
        // given
        Portfolio savedPortfolio = portfolioRepository.save(getPortfolio());
        HoldingStock holdingStock = getHoldingStock(savedPortfolio);

        HoldingStock savedHoldingStock = holdingStockRepository.save(holdingStock);

        // when
        HoldingStock foundHoldingStock = holdingStockService.get(savedHoldingStock.getId());

        // then
        assertThat(foundHoldingStock.getId()).isEqualTo(savedHoldingStock.getId());
        assertThat(foundHoldingStock.getStockCode()).isEqualTo(holdingStock.getStockCode());
    }

    @Test
    @DisplayName("존재하지 않는 보유종목은 조회할 수 없다.")
    void getHoldingStockWithNoResult() {
        // given
        Long holdingStockId = 1L;

        // expected
        assertThatThrownBy(() -> holdingStockService.get(holdingStockId))
                .isInstanceOf(HoldingStockNotFound.class)
                .hasMessage(HOLDING_STOCK_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("보유 종목 1건을 생성한다.")
    void holdingStockCreate() {
        // given
        Portfolio savedPortfolio = portfolioRepository.save(getPortfolio());
        TradeLogCreate tradeLogCreate = TradeLogCreate.builder()
                .type(BUY)
                .price(TEN)
                .tradeDate(now())
                .quantity(ONE)
                .build();

        HoldingStockCreate holdingStockCreate = getHoldingStockCreate();
        given(mockStockServerService.getStock(TEST_STOCK_CODE))
                .willReturn(Stock.builder()
                        .code(TEST_STOCK_CODE)
                        .price(TEN).build());

        // when
        HoldingStock savedHoldingStock =
                holdingStockService.create(holdingStockCreate, tradeLogCreate,
                        savedPortfolio);

        // then
        assertThat(savedHoldingStock.getId()).isNotNull();
        assertThat(savedHoldingStock.getStockCode()).isEqualTo(holdingStockCreate.getStockCode());
    }

    @Test
    @DisplayName("보유종목 1건을 삭제한다.")
    void deleteHoldingStock() {
        // given
        Portfolio portfolio = getPortfolio();
        HoldingStock holdingStock = getHoldingStock(portfolio);

        HoldingStock savedHoldingStock = holdingStockRepository.save(holdingStock);

        willDoNothing()
                .given(tradeLogRepository)
                .deleteByHoldingStockId(anyLong());

        // when
        holdingStockService.delete(savedHoldingStock.getId());

        // then
        assertThatThrownBy(() -> holdingStockService.get(savedHoldingStock.getId()))
                .isInstanceOf(HoldingStockNotFound.class)
                .hasMessage(HOLDING_STOCK_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 보유종목을 삭제할 수 없다.")
    void deleteHoldingStockWithNoResult() {
        // given
        Long holdingStockId = 1L;

        // expected
        assertThatThrownBy(() -> holdingStockService.delete(holdingStockId))
                .isInstanceOf(HoldingStockNotFound.class)
                .hasMessage(HOLDING_STOCK_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("포트폴리오 아이디로 보유종목을 삭제한다.")
    void deleteHoldingStockByPortfolioId() {
        // given
        Portfolio savedPortfolio = portfolioRepository.save(getPortfolio());

        HoldingStock holdingStock = HoldingStock.of(getHoldingStockCreate(), savedPortfolio);
        holdingStockRepository.save(holdingStock);

        // when
        holdingStockService.deleteByPortfolioId(savedPortfolio.getId());

        // then
        List<HoldingStock> holdingStocks =
                holdingStockService.getByPortfolioId(savedPortfolio.getId());

        assertThat(holdingStocks).isEmpty();
    }

    @Test
    @DisplayName("2건의 매매이력으로 보유종목 통계를 만든다.")
    void getHoldingStockStatistics() {
        // given
        TradeLog log1 = getTradeLog(getBD(33000), ONE);
        TradeLog log2 = getTradeLog(getBD(28600), ONE);

        HoldingStock holdingStock = getHoldingStock(TEST_STOCK_CODE, getPortfolio(),
                List.of(log1, log2));

        given(holdingStockRepository.findByPortfolioId(anyLong()))
                .willReturn(List.of(holdingStock));

        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        // when
        List<HoldingStockStatistic> statistics = holdingStockService.getHoldingStockStatistics(1L);

        // then
        assertThat(statistics)
                .hasSize(1)
                .extracting("totalAsset", "gain", "gainRate",
                        "avgPrice", "currentPrice", "quantity")
                .contains(
                        tuple(getBD(132_000), getBD(70_400), getBD(114.29),
                                getBD(30_800), getBD(66_000), getBD(2))
                );
    }

    @Test
    @DisplayName("3건의 매매이력으로 보유종목 통계를 만든다.")
    void getHoldingStockStatistics2() {
        // given
        TradeLog log1 = getTradeLog(getBD(33000), ONE);
        TradeLog log2 = getTradeLog(getBD(28600), ONE);
        TradeLog log3 = getTradeLog(getBD(77620), getBD(2));

        HoldingStock holdingStock = getHoldingStock(TEST_STOCK_CODE, getPortfolio(),
                List.of(log1, log2, log3));

        given(holdingStockRepository.findByPortfolioId(anyLong()))
                .willReturn(List.of(holdingStock));

        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        // when
        List<HoldingStockStatistic> statistics = holdingStockService.getHoldingStockStatistics(1L);

        // then
        assertThat(statistics)
                .hasSize(1)
                .extracting("totalAsset", "gain", "gainRate",
                        "avgPrice", "currentPrice", "quantity")
                .contains(
                        tuple(getBD(264_000), getBD(47_160), getBD(21.75),
                                getBD(54_210), getBD(66_000), getBD(4))
                );
    }

    private Portfolio getPortfolio() {
        return Portfolio.builder()
                .build();
    }

    private HoldingStock getHoldingStock(Portfolio portfolio) {
        return HoldingStock.builder()
                .id(1L)
                .stockCode("101010")
                .portfolio(portfolio)
                .build();
    }

    private HoldingStockCreate getHoldingStockCreate() {
        return HoldingStockCreate.builder()
                .stockCode("101010")
                .build();
    }


    private HoldingStock getHoldingStock(String stockCode, Portfolio portfolio,
            List<TradeLog> tradeLog) {
        return HoldingStock.builder()
                .id(1L)
                .stockCode(stockCode)
                .portfolio(portfolio)
                .tradeLogs(tradeLog)
                .build();
    }

    private TradeLog getTradeLog(BigDecimal price, BigDecimal quantity) {
        return TradeLog.builder()
                .type(BUY)
                .price(price)
                .quantity(quantity)
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
