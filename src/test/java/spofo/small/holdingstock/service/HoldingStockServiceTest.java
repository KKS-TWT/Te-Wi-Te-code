package spofo.small.holdingstock.service;

import static java.math.BigDecimal.ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static spofo.global.component.utils.CommonUtils.getBD;
import static spofo.global.domain.exception.ErrorCode.HOLDING_STOCK_NOT_FOUND;
import static spofo.tradelog.domain.enums.TradeType.BUY;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.global.domain.exception.HoldingStockNotFound;
import spofo.holdingstock.domain.HoldingStock;
import spofo.holdingstock.domain.HoldingStockCreate;
import spofo.holdingstock.domain.HoldingStockStatistic;
import spofo.holdingstock.service.HoldingStockService;
import spofo.mock.FakeHoldingStockRepository;
import spofo.mock.FakePortfolioRepository;
import spofo.mock.FakePortfolioService;
import spofo.mock.FakeStockServerService;
import spofo.mock.FakeTradeLogService;
import spofo.portfolio.domain.Portfolio;
import spofo.stock.domain.Stock;
import spofo.tradelog.domain.TradeLog;
import spofo.tradelog.domain.TradeLogCreate;

class HoldingStockServiceTest {

    private HoldingStockService holdingStockService;
    private FakePortfolioService fakePortfolioService;
    private FakeTradeLogService fakeTradeLogService;
    private FakeHoldingStockRepository fakeHoldingStockRepository;
    private FakeStockServerService fakeStockServerService;
    private FakePortfolioRepository fakePortfolioRepository;

    private static final Long PORTFOLIO_ID = 1L;
    private static final String TEST_STOCK_CODE = "101010";

    @BeforeEach
    void setup() {
        fakeTradeLogService = new FakeTradeLogService();
        fakeHoldingStockRepository = new FakeHoldingStockRepository();
        fakeStockServerService = new FakeStockServerService();
        fakePortfolioRepository = new FakePortfolioRepository();
        fakePortfolioService
                = new FakePortfolioService(fakePortfolioRepository, fakeStockServerService);
        holdingStockService = null;
        
        Stock stock = Stock.builder()
                .code(TEST_STOCK_CODE)
                .name("삼성전자")
                .price(getBD(66000))
                .sector("반도체")
                .build();

        fakeStockServerService.save(stock);
    }

    @Test
    @DisplayName("포트폴리오 1개에 속한 보유종목을 조회한다.")
    void getByPortfolioId() {
        // given
        Portfolio portfolio = getPortfolio(PORTFOLIO_ID);

        HoldingStock holdingStock = getHoldingStock(TEST_STOCK_CODE, portfolio);

        fakeHoldingStockRepository.save(holdingStock);

        // when
        List<HoldingStock> holdingStocks = holdingStockService.getByPortfolioId(PORTFOLIO_ID);

        // then
        assertThat(holdingStocks)
                .extracting("id", "stockCode")
                .contains(
                        tuple(1L, TEST_STOCK_CODE)
                );
    }

    @Test
    @DisplayName("포트폴리오 1개에 속한 보유종목이 없으면 비어있는 리스트를 반환한다.")
    void getByPortfolioIdWithNoHoldingStock() {
        // given & when
        List<HoldingStock> holdingStocks = holdingStockService.getByPortfolioId(PORTFOLIO_ID);

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
        Long holdingStockId = 1L;
        Portfolio portfolio = getPortfolio(PORTFOLIO_ID);
        HoldingStock holdingStock = getHoldingStock(TEST_STOCK_CODE, portfolio);

        fakeHoldingStockRepository.save(holdingStock);

        // when
        HoldingStock savedHoldingStock = holdingStockService.get(holdingStockId);

        // then
        assertThat(savedHoldingStock.getId()).isEqualTo(holdingStockId);
        assertThat(savedHoldingStock.getStockCode()).isEqualTo(TEST_STOCK_CODE);
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
        Portfolio portfolio = getPortfolio(PORTFOLIO_ID);
        TradeLogCreate tradeLogCreate = TradeLogCreate.builder().build();

        HoldingStockCreate holdingStockCreate = HoldingStockCreate.builder()
                .stockCode(TEST_STOCK_CODE)
                .build();

        fakePortfolioRepository.save(portfolio);

        // when
        HoldingStock savedHoldingStock =
                holdingStockService.create(holdingStockCreate, tradeLogCreate, portfolio);

        // then
        assertThat(savedHoldingStock.getId()).isEqualTo(1L);
        assertThat(savedHoldingStock.getStockCode()).isEqualTo(TEST_STOCK_CODE);
    }

    @Test
    @DisplayName("보유종목 1건을 삭제한다.")
    void deleteHoldingStock() {
        // given
        Long holdingStockId = 1L;
        Portfolio portfolio = getPortfolio(PORTFOLIO_ID);
        HoldingStock holdingStock = getHoldingStock(TEST_STOCK_CODE, portfolio);

        fakeHoldingStockRepository.save(holdingStock);

        // when
        holdingStockService.delete(holdingStockId);

        // then
        assertThatThrownBy(() -> holdingStockService.get(holdingStockId))
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
        Portfolio portfolio = getPortfolio(PORTFOLIO_ID);
        HoldingStockCreate create = HoldingStockCreate.builder()
                .stockCode(TEST_STOCK_CODE)
                .build();

        HoldingStock holdingStock = HoldingStock.of(create, portfolio);

        fakeHoldingStockRepository.save(holdingStock);

        // when
        holdingStockService.deleteByPortfolioId(PORTFOLIO_ID);

        // then
        List<HoldingStock> holdingStocks = holdingStockService.getByPortfolioId(PORTFOLIO_ID);

        assertThat(holdingStocks).isEmpty();
    }

    @Test
    @DisplayName("2건의 매매이력으로 보유종목 통계를 만든다.")
    void getHoldingStockStatistics() {
        // given
        Portfolio portfolio = getPortfolio(PORTFOLIO_ID);

        TradeLog log1 = getTradeLog(getBD(33000), ONE);
        TradeLog log2 = getTradeLog(getBD(28600), ONE);

        HoldingStock holdingStock = getHoldingStock(TEST_STOCK_CODE, portfolio,
                List.of(log1, log2));

        fakeHoldingStockRepository.save(holdingStock);

        // when
        List<HoldingStockStatistic> statistics = holdingStockService.getHoldingStockStatistics(
                PORTFOLIO_ID);

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
        Portfolio portfolio = getPortfolio(PORTFOLIO_ID);

        TradeLog log1 = getTradeLog(getBD(33000), ONE);
        TradeLog log2 = getTradeLog(getBD(28600), ONE);
        TradeLog log3 = getTradeLog(getBD(77620), getBD(2));

        HoldingStock holdingStock = getHoldingStock(TEST_STOCK_CODE, portfolio,
                List.of(log1, log2, log3));

        fakeHoldingStockRepository.save(holdingStock);

        // when
        List<HoldingStockStatistic> statistics = holdingStockService.getHoldingStockStatistics(
                PORTFOLIO_ID);

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

    @Test
    @DisplayName("보유종목의 종목코드로 보유종목의 존재여부를 조회한다.")
    void exists() {
        // given
        Portfolio portfolio = getPortfolio(PORTFOLIO_ID);
        HoldingStock holdingStock = getHoldingStock(TEST_STOCK_CODE, portfolio);

        fakeHoldingStockRepository.save(holdingStock);

        // when
        HoldingStock foundHoldingStock = holdingStockService.get(portfolio, TEST_STOCK_CODE);

        // then
        assertThat(foundHoldingStock).isNotNull();
    }

    @Test
    @DisplayName("유효하지 않은 종목코드로 보유종목의 존재여부를 조회할 수 없다.")
    void existsWithNotValidStockCode() {
        // given
        Portfolio portfolio = getPortfolio(PORTFOLIO_ID);
        HoldingStock holdingStock = getHoldingStock(TEST_STOCK_CODE, portfolio);

        fakeHoldingStockRepository.save(holdingStock);

        // when
        HoldingStock foundHoldingStock = holdingStockService.get(portfolio, "유효하지 않은 종목코드");

        // then
        assertThat(foundHoldingStock).isNull();
    }

    @Test
    @DisplayName("포트폴리오가 존재하지 않으면 보유종목의 존재여부를 조회할 수 없다.")
    void existsWithNoPortfolio() {
        // given
        // when
        HoldingStock foundHoldingStock = holdingStockService.get(null, "유효하지 않은 종목코드");

        // then
        assertThat(foundHoldingStock).isNull();
    }

    private Portfolio getPortfolio(Long id) {
        return Portfolio.builder()
                .id(id)
                .build();
    }

    private HoldingStock getHoldingStock(String stockCode, Portfolio portfolio) {
        return HoldingStock.builder()
                .id(1L)
                .stockCode(stockCode)
                .portfolio(portfolio)
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
}
