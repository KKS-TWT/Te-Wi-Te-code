package spofo.medium.portfolio.service;

import static java.math.BigDecimal.ZERO;
import static java.util.Collections.EMPTY_LIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
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
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.global.domain.exception.PortfolioNotFound;
import spofo.holdingstock.domain.HoldingStock;
import spofo.portfolio.controller.request.PortfolioSearchCondition;
import spofo.portfolio.domain.Portfolio;
import spofo.portfolio.domain.PortfolioCreate;
import spofo.portfolio.domain.PortfolioStatistic;
import spofo.portfolio.domain.PortfolioUpdate;
import spofo.portfolio.domain.TotalPortfoliosStatistic;
import spofo.stock.domain.Stock;
import spofo.support.service.ServiceTestSupport;
import spofo.tradelog.domain.TradeLog;

public class PortfolioServiceTest extends ServiceTestSupport {

    private static final String PORTFOLIO_CREATE_NAME = "포트폴리오 생성";
    private static final String PORTFOLIO_CREATE_DESC = "포트폴리오 생성입니다.";
    private static final String PORTFOLIO_UPDATE_NAME = "포트폴리오 수정";
    private static final String PORTFOLIO_UPDATE_DESC = "포트폴리오 수정입니다.";
    private static final Long MEMBER_ID = 1L;
    private static final String TEST_STOCK_CODE = "000660";

    @Test
    @DisplayName("회원 1명이 등록한 전체 포트폴리오의 개요를 조회한다.")
    void getPortfoliosStatistic() {
        // given
        TradeLog tradeLog = getTradeLog();
        HoldingStock holdingStock = getHoldingStock(tradeLog);

        Portfolio portfolio1 = Portfolio.builder()
                .includeType(Y)
                .type(REAL)
                .holdingStocks(List.of(holdingStock))
                .build();

        Portfolio portfolio2 = Portfolio.builder()
                .includeType(Y)
                .type(REAL)
                .holdingStocks(List.of(holdingStock))
                .build();

        given(portfolioRepository.findByMemberIdWithTradeLogs(MEMBER_ID))
                .willReturn(List.of(portfolio1, portfolio2));

        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        PortfolioSearchCondition condition = PortfolioSearchCondition
                .builder()
                .type(REAL)
                .build();

        // when
        TotalPortfoliosStatistic totalPortfoliosStatistic =
                portfolioService.getPortfoliosStatistic(MEMBER_ID, condition);

        // then
        assertThat(totalPortfoliosStatistic.getTotalAsset()).isEqualTo(getBD(132000));
        assertThat(totalPortfoliosStatistic.getGain()).isEqualTo(getBD(66000));
        assertThat(totalPortfoliosStatistic.getGainRate()).isEqualTo(getBD(100));
    }

    @Test
    @DisplayName("포트폴리오 포함여부가 N인 포트폴리오는 전체 통계 계산 시 제외된다.")
    void getPortfoliosStatisticWithNoInclude() {
        // given
        TradeLog tradeLog = getTradeLog();
        HoldingStock holdingStock = getHoldingStock(tradeLog);

        Portfolio portfolio = Portfolio.builder()
                .includeType(N)
                .type(REAL)
                .holdingStocks(List.of(holdingStock))
                .build();

        given(portfolioRepository.findByMemberIdWithTradeLogs(MEMBER_ID))
                .willReturn(List.of(portfolio));

        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

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
        given(portfolioRepository.findByMemberIdWithTradeLogs(MEMBER_ID))
                .willReturn(EMPTY_LIST);

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
        TradeLog tradeLog = getTradeLog();
        HoldingStock holdingStock = getHoldingStock(tradeLog);

        Portfolio portfolio1 = Portfolio.builder()
                .id(1L)
                .includeType(Y)
                .type(REAL)
                .holdingStocks(List.of(holdingStock))
                .build();

        Portfolio portfolio2 = Portfolio.builder()
                .id(2L)
                .includeType(Y)
                .type(FAKE)
                .holdingStocks(List.of(holdingStock, holdingStock))
                .build();

        given(portfolioRepository.findByMemberIdWithTradeLogs(MEMBER_ID))
                .willReturn(List.of(portfolio1, portfolio2));

        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        PortfolioSearchCondition condition = PortfolioSearchCondition.builder().build();

        // when
        List<PortfolioStatistic> portfolios = portfolioService.getPortfolios(MEMBER_ID,
                condition);

        // then
        assertThat(portfolios).hasSize(2)
                .extracting("portfolio.id", "totalAsset", "totalBuy", "totalGain", "gainRate")
                .containsExactlyInAnyOrder(
                        tuple(1L, getBD(66000), getBD(33000),
                                getBD(33000), getBD(100)),

                        tuple(2L, getBD(132000), getBD(66000),
                                getBD(66000), getBD(100))
                );
    }

    @Test
    @DisplayName("포트폴리오를 [REAL] 필터링하여 조회한다.")
    void getPortfoliosWithREAL() {
        // given
        TradeLog tradeLog = getTradeLog();
        HoldingStock holdingStock = getHoldingStock(tradeLog);

        Portfolio portfolio1 = Portfolio.builder()
                .id(1L)
                .includeType(Y)
                .type(REAL)
                .holdingStocks(List.of(holdingStock))
                .build();

        Portfolio portfolio2 = Portfolio.builder()
                .id(2L)
                .includeType(Y)
                .type(FAKE)
                .holdingStocks(List.of(holdingStock, holdingStock))
                .build();

        Portfolio portfolio3 = Portfolio.builder()
                .id(3L)
                .includeType(Y)
                .type(LINK)
                .holdingStocks(List.of(holdingStock, holdingStock, holdingStock))
                .build();

        given(portfolioRepository.findByMemberIdWithTradeLogs(MEMBER_ID))
                .willReturn(List.of(portfolio1, portfolio2, portfolio3));

        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        PortfolioSearchCondition condition = PortfolioSearchCondition.builder().type(REAL)
                .build();

        // when
        List<PortfolioStatistic> portfolios = portfolioService.getPortfolios(MEMBER_ID,
                condition);

        // then
        assertThat(portfolios).hasSize(1)
                .extracting("portfolio.id", "totalAsset", "totalBuy", "totalGain", "gainRate")
                .containsExactlyInAnyOrder(
                        tuple(1L, getBD(66000), getBD(33000),
                                getBD(33000), getBD(100))
                );
    }

    @Test
    @DisplayName("포트폴리오를 [FAKE] 필터링하여 조회한다.")
    void getPortfoliosWithFAKE() {
        // given
        TradeLog tradeLog = getTradeLog();
        HoldingStock holdingStock = getHoldingStock(tradeLog);

        Portfolio portfolio1 = Portfolio.builder()
                .id(1L)
                .includeType(Y)
                .type(REAL)
                .holdingStocks(List.of(holdingStock))
                .build();

        Portfolio portfolio2 = Portfolio.builder()
                .id(2L)
                .includeType(Y)
                .type(FAKE)
                .holdingStocks(List.of(holdingStock, holdingStock))
                .build();

        Portfolio portfolio3 = Portfolio.builder()
                .id(3L)
                .includeType(Y)
                .type(LINK)
                .holdingStocks(List.of(holdingStock, holdingStock, holdingStock))
                .build();

        given(portfolioRepository.findByMemberIdWithTradeLogs(MEMBER_ID))
                .willReturn(List.of(portfolio1, portfolio2, portfolio3));

        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        PortfolioSearchCondition condition = PortfolioSearchCondition.builder().type(FAKE)
                .build();

        // when
        List<PortfolioStatistic> portfolios = portfolioService.getPortfolios(MEMBER_ID,
                condition);

        // then
        assertThat(portfolios).hasSize(1)
                .extracting("portfolio.id", "totalAsset", "totalBuy", "totalGain", "gainRate")
                .containsExactlyInAnyOrder(
                        tuple(2L, getBD(132000), getBD(66000),
                                getBD(66000), getBD(100))
                );
    }

    @Test
    @DisplayName("포트폴리오를 [LINK] 필터링하여 조회한다.")
    void getPortfoliosWithLINK() {
        // given
        TradeLog tradeLog = getTradeLog();
        HoldingStock holdingStock = getHoldingStock(tradeLog);

        Portfolio portfolio1 = Portfolio.builder()
                .id(1L)
                .includeType(Y)
                .type(REAL)
                .holdingStocks(List.of(holdingStock))
                .build();

        Portfolio portfolio2 = Portfolio.builder()
                .id(2L)
                .includeType(Y)
                .type(FAKE)
                .holdingStocks(List.of(holdingStock, holdingStock))
                .build();

        Portfolio portfolio3 = Portfolio.builder()
                .id(3L)
                .includeType(Y)
                .type(LINK)
                .holdingStocks(List.of(holdingStock, holdingStock, holdingStock))
                .build();

        given(portfolioRepository.findByMemberIdWithTradeLogs(MEMBER_ID))
                .willReturn(List.of(portfolio1, portfolio2, portfolio3));

        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        PortfolioSearchCondition condition = PortfolioSearchCondition.builder().type(LINK)
                .build();

        // when
        List<PortfolioStatistic> portfolios = portfolioService.getPortfolios(MEMBER_ID,
                condition);

        // then
        assertThat(portfolios).hasSize(1)
                .extracting("portfolio.id", "totalAsset", "totalBuy", "totalGain", "gainRate")
                .containsExactlyInAnyOrder(
                        tuple(3L, getBD(198000), getBD(99000),
                                getBD(99000), getBD(100))
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
        Portfolio portfolio = Portfolio.builder()
                .memberId(MEMBER_ID)
                .name("포트폴리오 이름")
                .description("포트폴리오 설명")
                .currency(KRW)
                .includeType(Y)
                .type(REAL)
                .build();

        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        // when
        Portfolio foundPortfolio = portfolioService.getPortfolio(savedPortfolio.getId());

        // then
        assertThat(foundPortfolio.getId()).isNotNull();
        assertThat(foundPortfolio.getMemberId()).isEqualTo(MEMBER_ID);
        assertThat(foundPortfolio.getName()).isEqualTo("포트폴리오 이름");
        assertThat(foundPortfolio.getDescription()).isEqualTo("포트폴리오 설명");
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
        TradeLog tradeLog = getTradeLog();
        HoldingStock holdingStock = getHoldingStock(tradeLog);

        Portfolio portfolio = Portfolio.builder()
                .holdingStocks(List.of(holdingStock))
                .build();

        given(portfolioRepository.findByIdWithTradeLogs(anyLong()))
                .willReturn(Optional.of(portfolio));

        given(mockStockServerService.getStocks(anyList()))
                .willReturn(getStockMap());

        // when
        PortfolioStatistic statistic = portfolioService.getPortfolioStatistic(MEMBER_ID);

        // then
        assertThat(statistic.getTotalAsset()).isEqualTo(getBD(66000));
        assertThat(statistic.getTotalBuy()).isEqualTo(getBD(33000));
        assertThat(statistic.getTotalGain()).isEqualTo(getBD(33000));
        assertThat(statistic.getGainRate()).isEqualTo(getBD(100));
    }

    @Test
    @DisplayName("포트폴리오 1건을 생성한다.")
    void createPortfolio() {
        // given
        PortfolioCreate createPortfolio = getCreatePortfolio();

        // when
        Portfolio savedPortfolio = portfolioService.create(createPortfolio, MEMBER_ID);

        // then
        assertThat(savedPortfolio.getId()).isNotNull();
        assertThat(savedPortfolio.getName()).isEqualTo(PORTFOLIO_CREATE_NAME);
        assertThat(savedPortfolio.getDescription()).isEqualTo(PORTFOLIO_CREATE_DESC);
        assertThat(savedPortfolio.getType()).isEqualTo(REAL);
        assertThat(savedPortfolio.getCurrency()).isEqualTo(KRW);
        assertThat(savedPortfolio.getMemberId()).isEqualTo(MEMBER_ID);
    }

    @Test
    @DisplayName("포트폴리오 1건을 수정한다.")
    void updatePortfolio() {
        // given
        Portfolio savedPortfolio = portfolioService.create(getCreatePortfolio(), MEMBER_ID);
        PortfolioUpdate updatePortfolio = getUpdatePortfolio(savedPortfolio.getId());

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
        Portfolio savedPortfolio = portfolioService.create(getCreatePortfolio(), MEMBER_ID);

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
        Portfolio savedPortfolio = portfolioService.create(getCreatePortfolio(), MEMBER_ID);
        HoldingStock holdingStock = HoldingStock.builder()
                .portfolio(savedPortfolio)
                .id(1L).build();
        holdingStockRepository.save(holdingStock);

        // when
        portfolioService.delete(savedPortfolio.getId());

        // then
        assertThatThrownBy(() -> portfolioService.getPortfolio(savedPortfolio.getId()))
                .isInstanceOf(PortfolioNotFound.class)
                .hasMessage(PORTFOLIO_NOT_FOUND.getMessage());
        assertThat(holdingStockService.getByPortfolioId(1L)).hasSize(0);
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

    private PortfolioCreate getCreatePortfolio() {
        return PortfolioCreate.builder()
                .name(PORTFOLIO_CREATE_NAME)
                .description(PORTFOLIO_CREATE_DESC)
                .currency(KRW)
                .type(REAL)
                .build();
    }

    private PortfolioUpdate getUpdatePortfolio(Long id) {
        return PortfolioUpdate.builder()
                .id(id)
                .name(PORTFOLIO_UPDATE_NAME)
                .description(PORTFOLIO_UPDATE_DESC)
                .currency(KRW)
                .type(REAL)
                .build();
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
