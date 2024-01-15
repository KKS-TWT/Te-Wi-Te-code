package spofo.medium.portfolio.service;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static spofo.global.component.utils.CommonUtils.getBD;
import static spofo.global.domain.exception.ErrorCode.PORTFOLIO_NOT_FOUND;
import static spofo.portfolio.domain.enums.Currency.KRW;
import static spofo.portfolio.domain.enums.IncludeType.N;
import static spofo.portfolio.domain.enums.IncludeType.Y;
import static spofo.portfolio.domain.enums.PortfolioType.FAKE;
import static spofo.portfolio.domain.enums.PortfolioType.LINK;
import static spofo.portfolio.domain.enums.PortfolioType.REAL;
import static spofo.tradelog.domain.enums.TradeType.BUY;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.global.domain.exception.PortfolioNotFound;
import spofo.holdingstock.domain.HoldingStockCreate;
import spofo.portfolio.controller.request.PortfolioSearchCondition;
import spofo.portfolio.domain.Portfolio;
import spofo.portfolio.domain.PortfolioCreate;
import spofo.portfolio.domain.PortfolioStatistic;
import spofo.portfolio.domain.PortfolioUpdate;
import spofo.portfolio.domain.TotalPortfoliosStatistic;
import spofo.portfolio.domain.enums.IncludeType;
import spofo.portfolio.domain.enums.PortfolioType;
import spofo.stock.domain.Stock;
import spofo.support.service.ServiceTestSupport;
import spofo.tradelog.domain.TradeLogCreate;

class PortfolioServiceTest extends ServiceTestSupport {

    private static final String PORTFOLIO_CREATE_NAME = "포트폴리오 생성";
    private static final String PORTFOLIO_CREATE_DESC = "포트폴리오 생성입니다.";
    private static final String PORTFOLIO_UPDATE_NAME = "포트폴리오 수정";
    private static final String PORTFOLIO_UPDATE_DESC = "포트폴리오 수정입니다.";
    private static final Long MEMBER_ID = 1L;
    private static final String TEST_STOCK_CODE = "005930";

    @Test
    @DisplayName("회원 1명이 등록한 전체 포트폴리오의 개요를 조회한다.")
    void getPortfoliosStatistic() {
        // given
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());

        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        savePortfolioWithTradeLogs(REAL);

        PortfolioSearchCondition condition = PortfolioSearchCondition
                .builder()
                .type(REAL)
                .build();

        // when
        TotalPortfoliosStatistic totalPortfoliosStatistic =
                portfolioService.getPortfoliosStatistic(MEMBER_ID, condition);

        // then
        assertThat(totalPortfoliosStatistic.getTotalAsset()).isEqualTo(getBD(66000));
        assertThat(totalPortfoliosStatistic.getGain()).isEqualTo(getBD(33000));
        assertThat(totalPortfoliosStatistic.getGainRate()).isEqualTo(getBD(100));
    }

    @Test
    @DisplayName("포트폴리오 포함여부가 N인 포트폴리오는 전체 통계 계산 시 제외된다.")
    void getPortfoliosStatisticWithNoInclude() {
        // given
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());

        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        Portfolio savedPortfolio = savePortfolioWithTradeLogs(REAL);
        PortfolioUpdate updatePortfolio = getUpdatePortfolio(savedPortfolio.getId(), N);

        portfolioService.update(updatePortfolio, savedPortfolio.getId(), MEMBER_ID);

        PortfolioSearchCondition condition = PortfolioSearchCondition
                .builder()
                .type(REAL)
                .build();

        // when
        TotalPortfoliosStatistic totalPortfoliosStatistic =
                portfolioService.getPortfoliosStatistic(MEMBER_ID, condition);

        // then
        assertThat(totalPortfoliosStatistic.getTotalAsset()).isEqualTo(ZERO);
        assertThat(totalPortfoliosStatistic.getGain()).isEqualTo(ZERO);
        assertThat(totalPortfoliosStatistic.getGainRate()).isEqualTo(ZERO);
    }

    @Test
    @DisplayName("회원 1명이 등록한 포트폴리오가 존재하지 않으면 통계 결과는 모두 0이다.")
    void getPortfoliosStatisticWithNoResult() {
        // given
        PortfolioSearchCondition condition = PortfolioSearchCondition
                .builder()
                .type(REAL)
                .build();

        // when
        TotalPortfoliosStatistic totalPortfoliosStatistic =
                portfolioService.getPortfoliosStatistic(MEMBER_ID, condition);

        // then
        assertThat(totalPortfoliosStatistic.getTotalAsset()).isEqualTo(ZERO);
        assertThat(totalPortfoliosStatistic.getGain()).isEqualTo(ZERO);
        assertThat(totalPortfoliosStatistic.getGainRate()).isEqualTo(ZERO);
    }

    @Test
    @DisplayName("회원 1명이 가진 여러 개의 포트폴리오를 조회한다.")
    void getPortfolios() {
        // given
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());

        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        savePortfolioWithTradeLogs(REAL);
        savePortfolioWithTradeLogs(REAL);

        PortfolioSearchCondition condition = PortfolioSearchCondition.builder().build();

        // when
        List<PortfolioStatistic> portfolios = portfolioService.getPortfolios(MEMBER_ID,
                condition);

        // then
        assertThat(portfolios).hasSize(2)
                .extracting("totalAsset", "totalBuy", "totalGain", "gainRate")
                .containsExactlyInAnyOrder(
                        tuple(getBD(66000), getBD(33000), getBD(33000), getBD(100)),
                        tuple(getBD(66000), getBD(33000), getBD(33000), getBD(100))
                );
    }

    @Test
    @DisplayName("포트폴리오를 [REAL] 필터링하여 조회한다.")
    void getPortfoliosWithREAL() {
        // given
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());

        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        savePortfolioWithTradeLogs(REAL);
        savePortfolioWithTradeLogs(REAL);
        savePortfolioWithTradeLogs(FAKE);
        savePortfolioWithTradeLogs(LINK);

        PortfolioSearchCondition condition = PortfolioSearchCondition.builder().type(REAL)
                .build();

        // when
        List<PortfolioStatistic> portfolios = portfolioService.getPortfolios(MEMBER_ID,
                condition);

        // then
        assertThat(portfolios).hasSize(2)
                .extracting("totalAsset", "totalBuy", "totalGain", "gainRate")
                .containsExactlyInAnyOrder(
                        tuple(getBD(66000), getBD(33000), getBD(33000), getBD(100)),
                        tuple(getBD(66000), getBD(33000), getBD(33000), getBD(100))
                );
    }

    @Test
    @DisplayName("포트폴리오를 [FAKE] 필터링하여 조회한다.")
    void getPortfoliosWithFAKE() {
        // given
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());

        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        savePortfolioWithTradeLogs(REAL);
        savePortfolioWithTradeLogs(FAKE);
        savePortfolioWithTradeLogs(LINK);

        PortfolioSearchCondition condition = PortfolioSearchCondition.builder().type(FAKE)
                .build();

        // when
        List<PortfolioStatistic> portfolios = portfolioService.getPortfolios(MEMBER_ID,
                condition);

        // then
        assertThat(portfolios).hasSize(1)
                .extracting("totalAsset", "totalBuy", "totalGain", "gainRate")
                .containsExactlyInAnyOrder(
                        tuple(getBD(66000), getBD(33000), getBD(33000), getBD(100))
                );
    }

    @Test
    @DisplayName("포트폴리오를 [LINK] 필터링하여 조회한다.")
    void getPortfoliosWithLINK() {
        // given
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());

        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        savePortfolioWithTradeLogs(REAL);
        savePortfolioWithTradeLogs(FAKE);
        savePortfolioWithTradeLogs(LINK);
        savePortfolioWithTradeLogs(LINK);
        savePortfolioWithTradeLogs(LINK);

        PortfolioSearchCondition condition = PortfolioSearchCondition.builder().type(LINK)
                .build();

        // when
        List<PortfolioStatistic> portfolios = portfolioService.getPortfolios(MEMBER_ID,
                condition);

        // then
        assertThat(portfolios).hasSize(3)
                .extracting("totalAsset", "totalBuy", "totalGain", "gainRate")
                .containsExactlyInAnyOrder(
                        tuple(getBD(66000), getBD(33000), getBD(33000), getBD(100)),
                        tuple(getBD(66000), getBD(33000), getBD(33000), getBD(100)),
                        tuple(getBD(66000), getBD(33000), getBD(33000), getBD(100))
                );
    }

    @Test
    @DisplayName("회원 1명이 등록한 포트폴리오가 존재하지 않으면 비어있는 리스트를 반환한다.")
    void getPortfoliosWithNoResult() {
        // given
        // when
        List<PortfolioStatistic> portfolios = portfolioService.getPortfolios(MEMBER_ID, any());

        // then
        assertThat(portfolios).isEmpty();
    }

    @Test
    @DisplayName("포트폴리오 1건을 조회한다.")
    void getPortfolio() {
        // given
        PortfolioCreate portfolioCreate = getCreatePortfolio(REAL);
        Portfolio savedPortfolio = portfolioService.create(portfolioCreate, MEMBER_ID);

        // when
        Portfolio foundPortfolio = portfolioService.getPortfolio(savedPortfolio.getId());

        // then
        assertThat(foundPortfolio.getId()).isNotNull();
        assertThat(foundPortfolio.getMemberId()).isEqualTo(MEMBER_ID);
        assertThat(foundPortfolio.getName()).isEqualTo(PORTFOLIO_CREATE_NAME);
        assertThat(foundPortfolio.getDescription()).isEqualTo(PORTFOLIO_CREATE_DESC);
        assertThat(foundPortfolio.getCurrency()).isEqualTo(KRW);
        assertThat(foundPortfolio.getIncludeType()).isEqualTo(Y);
        assertThat(foundPortfolio.getType()).isEqualTo(REAL);
    }

    @Test
    @DisplayName("존재하지 않는 포트폴리오를 조회할 수 없다.")
    void getPortfolioWithNoResult() {
        // given
        Long portfolioId = 1L;

        // expected
        assertThatThrownBy(() -> portfolioService.getPortfolio(portfolioId))
                .isInstanceOf(PortfolioNotFound.class)
                .hasMessage(PORTFOLIO_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("포트폴리오 1건을 통계와 함께 조회한다.")
    void getPortfolioStatistic() {
        // given
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());

        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        Portfolio savedPortfolio = savePortfolioWithTradeLogs(REAL);

        // when
        PortfolioStatistic statistic =
                portfolioService.getPortfolioStatistic(savedPortfolio.getId());

        assertThat(statistic.getTotalAsset()).isEqualTo(getBD(66000));
        assertThat(statistic.getTotalBuy()).isEqualTo(getBD(33000));
        assertThat(statistic.getTotalGain()).isEqualTo(getBD(33000));
        assertThat(statistic.getGainRate()).isEqualTo(getBD(100));
    }

    @Test
    @DisplayName("포트폴리오 1건을 생성한다.")
    void createPortfolio() {
        // given
        PortfolioCreate portfolioCreate = getCreatePortfolio(REAL);

        // when
        Portfolio savedPortfolio = portfolioService.create(portfolioCreate, MEMBER_ID);

        // then
        assertThat(savedPortfolio.getId()).isNotNull();
        assertThat(savedPortfolio.getMemberId()).isEqualTo(MEMBER_ID);
        assertThat(savedPortfolio.getName()).isEqualTo(PORTFOLIO_CREATE_NAME);
        assertThat(savedPortfolio.getDescription()).isEqualTo(PORTFOLIO_CREATE_DESC);
        assertThat(savedPortfolio.getCurrency()).isEqualTo(KRW);
        assertThat(savedPortfolio.getIncludeType()).isEqualTo(Y);
        assertThat(savedPortfolio.getType()).isEqualTo(REAL);
    }

    @Test
    @DisplayName("포트폴리오 1건을 수정한다.")
    void updatePortfolio() {
        // given
        PortfolioCreate createPortfolio = getCreatePortfolio(REAL);
        Portfolio savedPortfolio = portfolioService.create(createPortfolio, MEMBER_ID);
        PortfolioUpdate updatePortfolio = getUpdatePortfolio(savedPortfolio.getId(), N);

        // when
        Portfolio updatedPortfolio = portfolioService
                .update(updatePortfolio, savedPortfolio.getId(), MEMBER_ID);

        // then
        assertThat(updatedPortfolio.getId()).isEqualTo(savedPortfolio.getId());
        assertThat(updatedPortfolio.getName()).isEqualTo(PORTFOLIO_UPDATE_NAME);
        assertThat(updatedPortfolio.getDescription()).isEqualTo(PORTFOLIO_UPDATE_DESC);
    }

    @Test
    @DisplayName("존재하지 않는 포트폴리오를 수정할 수 없다.")
    void updatePortfolioWithNoResult() {
        // given
        Long portfolioId = 1L;
        PortfolioUpdate portfolioUpdate = PortfolioUpdate.builder().build();

        // expected
        assertThatThrownBy(() -> portfolioService.update(portfolioUpdate, portfolioId, MEMBER_ID))
                .isInstanceOf(PortfolioNotFound.class)
                .hasMessage(PORTFOLIO_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("보유종목이 없을 때 포트폴리오 1건을 삭제한다.")
    void deletePortfolio() {
        // given
        Portfolio savedPortfolio = portfolioService.create(getCreatePortfolio(REAL), MEMBER_ID);

        // when
        portfolioService.delete(savedPortfolio.getId());

        // then
        assertThatThrownBy(() -> portfolioService.getPortfolio(savedPortfolio.getId()))
                .isInstanceOf(PortfolioNotFound.class)
                .hasMessage(PORTFOLIO_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("보유종목이 있을 때 포트폴리오 1건을 삭제한다.")
    void deletePortfolioWithHoldingStock() {
        // given
        given(mockStockServerService.getStock(anyString()))
                .willReturn(getStock());

        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        Portfolio savedPortfolio = savePortfolioWithTradeLogs(REAL);

        // when
        portfolioService.delete(savedPortfolio.getId());

        // then
        assertThatThrownBy(() -> portfolioService.getPortfolio(savedPortfolio.getId()))
                .isInstanceOf(PortfolioNotFound.class)
                .hasMessage(PORTFOLIO_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 포트폴리오를 삭제할 수 없다.")
    void deletePortfolioWithNoResult() {
        // given
        Long portfolioId = 1L;

        // expected
        assertThatThrownBy(() -> portfolioService.delete(portfolioId))
                .isInstanceOf(PortfolioNotFound.class)
                .hasMessage(PORTFOLIO_NOT_FOUND.getMessage());
    }

    private Portfolio savePortfolioWithTradeLogs(PortfolioType type) {
        PortfolioCreate createPortfolio = getCreatePortfolio(type);
        HoldingStockCreate holdingStockCreate = getHoldingStockCreate();
        TradeLogCreate tradeLogCreate = getCreateTradeLog();

        Portfolio savedPortfolio = portfolioService.create(createPortfolio, MEMBER_ID);

        holdingStockService.create(holdingStockCreate, tradeLogCreate, savedPortfolio);

        return savedPortfolio;
    }

    private HoldingStockCreate getHoldingStockCreate() {
        return HoldingStockCreate.builder()
                .stockCode(TEST_STOCK_CODE)
                .build();
    }

    private PortfolioCreate getCreatePortfolio(PortfolioType type) {
        return PortfolioCreate.builder()
                .name(PORTFOLIO_CREATE_NAME)
                .description(PORTFOLIO_CREATE_DESC)
                .currency(KRW)
                .type(type)
                .build();
    }

    private TradeLogCreate getCreateTradeLog() {
        return TradeLogCreate.builder()
                .type(BUY)
                .price(getBD(33000))
                .tradeDate(now())
                .quantity(ONE)
                .build();
    }

    private PortfolioUpdate getUpdatePortfolio(Long id, IncludeType includeType) {
        return PortfolioUpdate.builder()
                .id(id)
                .name(PORTFOLIO_UPDATE_NAME)
                .description(PORTFOLIO_UPDATE_DESC)
                .currency(KRW)
                .type(REAL)
                .includeType(includeType)
                .build();
    }

    private Map<String, Stock> getStockMap() {
        Stock stock = getStock();
        return Map.of(stock.getCode(), stock);
    }

    private Stock getStock() {
        return Stock.builder()
                .code(TEST_STOCK_CODE)
                .name("SK하이닉스")
                .price(getBD(66000))
                .sector("반도체")
                .build();
    }
}