package spofo.medium.holdingstock.service;

import static java.math.BigDecimal.ONE;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static spofo.global.component.utils.CommonUtils.getBD;
import static spofo.global.domain.exception.ErrorCode.HOLDING_STOCK_NOT_FOUND;
import static spofo.portfolio.domain.enums.Currency.KRW;
import static spofo.portfolio.domain.enums.PortfolioType.REAL;
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
import spofo.portfolio.domain.PortfolioCreate;
import spofo.stock.domain.Stock;
import spofo.support.service.ServiceIntegrationTestSupport;
import spofo.tradelog.domain.TradeLogCreate;

public class HoldingStockServiceIntegrationTest extends ServiceIntegrationTestSupport {

    private static final String PORTFOLIO_CREATE_NAME = "포트폴리오 생성";
    private static final String PORTFOLIO_CREATE_DESC = "포트폴리오 생성입니다.";
    private static final String TEST_STOCK_CODE = "005930";
    private static final Long MEMBER_ID = 1L;

    @Test
    @DisplayName("포트폴리오 1개에 속한 보유종목을 조회한다.")
    void getByPortfolioId() {
        // given
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());
        Portfolio portfolio = getPortfolio();
        HoldingStock savedHoldingStock = savedHoldingStockWithPortfolio(portfolio);

        // when
        List<HoldingStock> holdingStocks = holdingStockService.getByPortfolioId(portfolio.getId());

        // then
        assertThat(holdingStocks).hasSize(1)
                .extracting("id", "stockCode", "portfolio")
                .containsExactlyInAnyOrder(
                        tuple(savedHoldingStock.getId(), TEST_STOCK_CODE, null));
    }

    @Test
    @DisplayName("포트폴리오 1개에 속한 보유종목이 없으면 비어있는 리스트를 반환한다.")
    void getByPortfolioIdWithNoHoldingStock() {
        // given
        Portfolio savedPortfolio = getPortfolio();

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
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());

        Portfolio savedPortfolio = getPortfolio();
        HoldingStock savedHoldingStock = savedHoldingStockWithPortfolio(savedPortfolio);

        // when
        HoldingStock foundHoldingStock = holdingStockService.get(savedHoldingStock.getId());

        // then
        assertThat(foundHoldingStock.getId()).isEqualTo(savedHoldingStock.getId());
        assertThat(foundHoldingStock.getStockCode()).isEqualTo(TEST_STOCK_CODE);
    }

    @Test
    @DisplayName("존재하지 않는 보유종목은 조회할 수 없다.")
    void getHoldingStockWithNoResult() {
        // given
        Long holdingStockId = 1000L;

        // expected
        assertThatThrownBy(() -> holdingStockService.get(holdingStockId))
                .isInstanceOf(HoldingStockNotFound.class)
                .hasMessage(HOLDING_STOCK_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("보유 종목 1건을 생성한다.")
    void holdingStockCreate() {
        // given
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());

        Portfolio savedPortfolio = getPortfolio();
        HoldingStockCreate holdingStockCreate = getHoldingStockCreate();
        TradeLogCreate tradeLogCreate = getTradeLogCreate(getBD(33000), ONE);

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
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());

        Portfolio savedPortfolio = getPortfolio();
        HoldingStock savedHoldingStock = savedHoldingStockWithPortfolio(savedPortfolio);

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
        Long holdingStockId = 1500L;

        // expected
        assertThatThrownBy(() -> holdingStockService.delete(holdingStockId))
                .isInstanceOf(HoldingStockNotFound.class)
                .hasMessage(HOLDING_STOCK_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("포트폴리오 아이디로 보유종목을 삭제한다.")
    void deleteHoldingStockByPortfolioId() {
        // given
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());

        Portfolio savedPortfolio = getPortfolio();
        savedHoldingStockWithPortfolio(savedPortfolio);

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
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());
        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        Portfolio savedPortfolio = getPortfolio();
        HoldingStockCreate holdingStockCreate = getHoldingStockCreate();
        TradeLogCreate tradeLogCreate1 = getTradeLogCreate(getBD(33_000), ONE);
        TradeLogCreate tradeLogCreate2 = getTradeLogCreate(getBD(28_600), ONE);

        HoldingStock savedHoldingStock = holdingStockService.create(holdingStockCreate,
                tradeLogCreate1,
                savedPortfolio);
        tradeLogService.create(tradeLogCreate2, savedHoldingStock);

        // when
        List<HoldingStockStatistic> statistics = holdingStockService.getHoldingStockStatistics(
                savedPortfolio.getId());

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
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());
        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        Portfolio savedPortfolio = getPortfolio();
        HoldingStockCreate holdingStockCreate = getHoldingStockCreate();
        TradeLogCreate tradeLogCreate1 = getTradeLogCreate(getBD(33_000), ONE);
        TradeLogCreate tradeLogCreate2 = getTradeLogCreate(getBD(28_600), ONE);
        TradeLogCreate tradeLogCreate3 = getTradeLogCreate(getBD(77_620), getBD(2));

        HoldingStock savedHoldingStock = holdingStockService.create(holdingStockCreate,
                tradeLogCreate1,
                savedPortfolio);
        tradeLogService.create(tradeLogCreate2, savedHoldingStock);
        tradeLogService.create(tradeLogCreate3, savedHoldingStock);

        // when
        List<HoldingStockStatistic> statistics = holdingStockService.getHoldingStockStatistics(
                savedPortfolio.getId());

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
    void getWithStockCode() {
        // given
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());
        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        Portfolio savedPortfolio = getPortfolio();
        HoldingStockCreate holdingStockCreate = getHoldingStockCreate();
        TradeLogCreate tradeLogCreate = getTradeLogCreate(getBD(33_000), ONE);

        holdingStockService.create(holdingStockCreate, tradeLogCreate, savedPortfolio);

        // when
        HoldingStock holdingStock = holdingStockService.get(savedPortfolio, TEST_STOCK_CODE);

        // then
        assertThat(holdingStock).isNotNull();
    }

    @Test
    @DisplayName("유효하지 않은 종목코드로 보유종목의 존재여부를 조회할 수 없다.")
    void existsWithNotValidStockCode() {
        // given
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());
        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        Portfolio savedPortfolio = getPortfolio();
        HoldingStockCreate holdingStockCreate = getHoldingStockCreate();
        TradeLogCreate tradeLogCreate = getTradeLogCreate(getBD(33_000), ONE);

        holdingStockService.create(holdingStockCreate, tradeLogCreate, savedPortfolio);

        // when
        HoldingStock holdingStock = holdingStockService.get(savedPortfolio, "유효하지 않은 종목코드");

        // then
        assertThat(holdingStock).isNull();
    }

    @Test
    @DisplayName("포트폴리오가 존재하지 않으면 보유종목의 존재여부를 조회할 수 없다.")
    void existsWithNoPortfolio() {
        // given
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());
        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        Portfolio savedPortfolio = getPortfolio();
        HoldingStockCreate holdingStockCreate = getHoldingStockCreate();
        TradeLogCreate tradeLogCreate = getTradeLogCreate(getBD(33_000), ONE);

        holdingStockService.create(holdingStockCreate, tradeLogCreate, savedPortfolio);

        // when
        HoldingStock holdingStock = holdingStockService.get(savedPortfolio, "유효하지 않은 종목코드");

        // then
        assertThat(holdingStock).isNull();
    }

    private Portfolio getPortfolio() {
        PortfolioCreate createPortfolio = getPortfolioCreate();
        return portfolioService.create(createPortfolio, MEMBER_ID);
    }

    private HoldingStock savedHoldingStockWithPortfolio(Portfolio portfolio) {
        HoldingStockCreate holdingStockCreate = getHoldingStockCreate();
        TradeLogCreate tradeLogCreate = getTradeLogCreate(getBD(33_000), ONE);

        return holdingStockService.create(holdingStockCreate, tradeLogCreate, portfolio);
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
                .name("SK하이닉스")
                .price(getBD(66000))
                .sector("반도체")
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
