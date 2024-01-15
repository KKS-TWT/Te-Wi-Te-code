package spofo.small.portfolio.service;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.global.domain.exception.PortfolioNotFound;
import spofo.holdingstock.domain.HoldingStock;
import spofo.mock.FakeHoldingStockRepository;
import spofo.mock.FakeHoldingStockService;
import spofo.mock.FakePortfolioRepository;
import spofo.mock.FakeStockServerService;
import spofo.portfolio.controller.request.PortfolioSearchCondition;
import spofo.portfolio.domain.Portfolio;
import spofo.portfolio.domain.PortfolioCreate;
import spofo.portfolio.domain.PortfolioStatistic;
import spofo.portfolio.domain.PortfolioUpdate;
import spofo.portfolio.domain.TotalPortfoliosStatistic;
import spofo.portfolio.domain.enums.Currency;
import spofo.portfolio.domain.enums.IncludeType;
import spofo.portfolio.domain.enums.PortfolioType;
import spofo.portfolio.service.PortfolioService;
import spofo.stock.domain.Stock;
import spofo.tradelog.domain.TradeLog;

class PortfolioServiceTest {

    private PortfolioService portfolioService;
    private FakeStockServerService fakeStockServerService;
    private FakeHoldingStockService fakeHoldingStockService;
    private FakePortfolioRepository fakePortfolioRepository;
    private FakeHoldingStockRepository fakeHoldingStockRepository;
    private final String TEST_STOCK_CODE = "101001";

    @BeforeEach
    void setup() {
        fakeHoldingStockRepository = new FakeHoldingStockRepository();
        fakePortfolioRepository = new FakePortfolioRepository();
        fakeStockServerService = new FakeStockServerService();
        fakeHoldingStockService = new FakeHoldingStockService(fakeHoldingStockRepository);
        portfolioService = null;

        Stock stock = Stock.builder()
                .code(TEST_STOCK_CODE)
                .name("삼성전자")
                .price(getBD(66000))
                .sector("반도체")
                .build();

        fakeStockServerService.save(stock);
    }

    @Test
    @DisplayName("회원 1명이 등록한 전체 포트폴리오의 개요를 조회한다.")
    void getPortfoliosStatistic() {
        // given
        Long memberId = 1L;
        TradeLog tradeLog = getTradeLog();
        HoldingStock holdingStock = getHoldingStock(tradeLog);

        Portfolio portfolio = Portfolio.builder()
                .memberId(memberId)
                .includeType(Y)
                .type(REAL)
                .holdingStocks(List.of(holdingStock))
                .build();

        fakePortfolioRepository.save(portfolio);

        PortfolioSearchCondition condition = PortfolioSearchCondition
                .builder()
                .type(REAL)
                .build();

        // when
        TotalPortfoliosStatistic totalPortfoliosStatistic = portfolioService.getPortfoliosStatistic(
                memberId, condition);

        // then
        assertThat(totalPortfoliosStatistic.getTotalAsset()).isEqualTo(getBD(66000));
        assertThat(totalPortfoliosStatistic.getGain()).isEqualTo(getBD(33000));
        assertThat(totalPortfoliosStatistic.getGainRate()).isEqualTo(getBD(100));
    }

    @Test
    @DisplayName("포트폴리오 포함여부가 N인 포트폴리오는 전체 통계 계산 시 제외된다.")
    void getPortfoliosStatisticWithNoInclude() {
        // given
        Long memberId = 1L;
        TradeLog tradeLog = getTradeLog();
        HoldingStock holdingStock = getHoldingStock(tradeLog);

        Portfolio portfolio = Portfolio.builder()
                .memberId(memberId)
                .includeType(N)
                .type(REAL)
                .holdingStocks(List.of(holdingStock))
                .build();

        fakePortfolioRepository.save(portfolio);

        PortfolioSearchCondition condition = PortfolioSearchCondition
                .builder()
                .type(REAL)
                .build();

        // when
        TotalPortfoliosStatistic totalPortfoliosStatistic = portfolioService.getPortfoliosStatistic(
                memberId, condition);

        // then
        assertThat(totalPortfoliosStatistic.getTotalAsset()).isEqualTo(getBD(0));
        assertThat(totalPortfoliosStatistic.getGain()).isEqualTo(getBD(0));
        assertThat(totalPortfoliosStatistic.getGainRate()).isEqualTo(ZERO);
    }

    @Test
    @DisplayName("회원 1명이 등록한 포트폴리오가 존재하지 않으면 통계 결과는 모두 0이다.")
    void getPortfoliosStatisticWithNoResult() {
        // given
        Long memberId = 1L;

        PortfolioSearchCondition condition = PortfolioSearchCondition
                .builder()
                .type(REAL)
                .build();

        // when
        TotalPortfoliosStatistic totalPortfoliosStatistic = portfolioService.getPortfoliosStatistic(
                memberId, condition);

        // then
        assertThat(totalPortfoliosStatistic.getTotalAsset()).isEqualTo(getBD(0));
        assertThat(totalPortfoliosStatistic.getGain()).isEqualTo(getBD(0));
        assertThat(totalPortfoliosStatistic.getGainRate()).isEqualTo(ZERO);
    }

    @Test
    @DisplayName("회원 1명이 가진 여러 개의 포트폴리오를 조회한다.")
    void getPortfolios() {
        // given
        Long memberId = 1L;
        TradeLog tradeLog = getTradeLog();
        HoldingStock holdingStock = getHoldingStock(tradeLog);

        Portfolio portfolio1 = Portfolio.builder()
                .memberId(memberId)
                .holdingStocks(List.of(holdingStock))
                .build();

        Portfolio portfolio2 = Portfolio.builder()
                .memberId(memberId)
                .holdingStocks(List.of(holdingStock, holdingStock))
                .build();

        fakePortfolioRepository.save(portfolio1);
        fakePortfolioRepository.save(portfolio2);

        PortfolioSearchCondition condition = PortfolioSearchCondition.builder().type(null)
                .build();

        // when
        List<PortfolioStatistic> portfolios = portfolioService.getPortfolios(memberId,
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
        Long memberId = 1L;
        TradeLog tradeLog = getTradeLog();
        HoldingStock holdingStock = getHoldingStock(tradeLog);

        Portfolio portfolio1 = Portfolio.builder()
                .memberId(memberId)
                .type(REAL)
                .holdingStocks(List.of(holdingStock))
                .build();

        Portfolio portfolio2 = Portfolio.builder()
                .memberId(memberId)
                .type(FAKE)
                .holdingStocks(List.of(holdingStock, holdingStock))
                .build();

        Portfolio portfolio3 = Portfolio.builder()
                .memberId(memberId)
                .type(LINK)
                .holdingStocks(List.of(holdingStock, holdingStock))
                .build();

        fakePortfolioRepository.save(portfolio1);
        fakePortfolioRepository.save(portfolio2);
        fakePortfolioRepository.save(portfolio3);

        PortfolioSearchCondition condition = PortfolioSearchCondition.builder().type(REAL)
                .build();

        // when
        List<PortfolioStatistic> portfolios = portfolioService.getPortfolios(memberId,
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
        Long memberId = 1L;
        TradeLog tradeLog = getTradeLog();
        HoldingStock holdingStock = getHoldingStock(tradeLog);

        Portfolio portfolio1 = Portfolio.builder()
                .memberId(memberId)
                .type(REAL)
                .holdingStocks(List.of(holdingStock))
                .build();

        Portfolio portfolio2 = Portfolio.builder()
                .memberId(memberId)
                .type(FAKE)
                .holdingStocks(List.of(holdingStock, holdingStock))
                .build();

        Portfolio portfolio3 = Portfolio.builder()
                .memberId(memberId)
                .type(LINK)
                .holdingStocks(List.of(holdingStock, holdingStock, holdingStock))
                .build();

        fakePortfolioRepository.save(portfolio1);
        fakePortfolioRepository.save(portfolio2);
        fakePortfolioRepository.save(portfolio3);

        PortfolioSearchCondition condition = PortfolioSearchCondition.builder().type(FAKE)
                .build();

        // when
        List<PortfolioStatistic> portfolios = portfolioService.getPortfolios(memberId,
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
        Long memberId = 1L;
        TradeLog tradeLog = getTradeLog();
        HoldingStock holdingStock = getHoldingStock(tradeLog);

        Portfolio portfolio1 = Portfolio.builder()
                .memberId(memberId)
                .type(REAL)
                .holdingStocks(List.of(holdingStock))
                .build();

        Portfolio portfolio2 = Portfolio.builder()
                .memberId(memberId)
                .type(FAKE)
                .holdingStocks(List.of(holdingStock, holdingStock))
                .build();

        Portfolio portfolio3 = Portfolio.builder()
                .memberId(memberId)
                .type(LINK)
                .holdingStocks(List.of(holdingStock, holdingStock, holdingStock))
                .build();

        fakePortfolioRepository.save(portfolio1);
        fakePortfolioRepository.save(portfolio2);
        fakePortfolioRepository.save(portfolio3);

        PortfolioSearchCondition condition = PortfolioSearchCondition.builder().type(LINK)
                .build();

        // when
        List<PortfolioStatistic> portfolios = portfolioService.getPortfolios(memberId,
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
        Long memberId = 1L;

        // when
        List<PortfolioStatistic> portfolios = portfolioService.getPortfolios(memberId, any());

        // then
        assertThat(portfolios).isEmpty();
    }

    @Test
    @DisplayName("포트폴리오 1건을 조회한다.")
    void getPortfolio() {
        // given
        Long portfolioId = 1L;

        Portfolio portfolio = Portfolio.builder()
                .memberId(1L)
                .name("포트폴리오 이름")
                .description("포트폴리오 설명")
                .currency(KRW)
                .includeType(Y)
                .type(REAL)
                .build();

        fakePortfolioRepository.save(portfolio);

        // when
        Portfolio savedPortfolio = portfolioService.getPortfolio(portfolioId);

        // then
        assertThat(savedPortfolio.getId()).isEqualTo(portfolioId);
        assertThat(savedPortfolio.getMemberId()).isEqualTo(1L);
        assertThat(savedPortfolio.getName()).isEqualTo("포트폴리오 이름");
        assertThat(savedPortfolio.getDescription()).isEqualTo("포트폴리오 설명");
        assertThat(savedPortfolio.getCurrency()).isEqualTo(KRW);
        assertThat(savedPortfolio.getIncludeType()).isEqualTo(Y);
        assertThat(savedPortfolio.getType()).isEqualTo(REAL);
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
        Long portfolioId = 1L;
        String name = "포트폴리오 이름";
        String description = "포트폴리오 설명";
        TradeLog tradeLog = getTradeLog();
        HoldingStock holdingStock = getHoldingStock(tradeLog);

        Portfolio portfolio = Portfolio.builder()
                .memberId(1L)
                .name(name)
                .description(description)
                .currency(KRW)
                .includeType(Y)
                .type(REAL)
                .holdingStocks(List.of(holdingStock))
                .build();

        fakePortfolioRepository.save(portfolio);

        // when
        PortfolioStatistic statistic = portfolioService.getPortfolioStatistic(1L);

        // then
        Portfolio savedPortfolio = statistic.getPortfolio();

        assertThat(savedPortfolio.getId()).isEqualTo(portfolioId);
        assertThat(savedPortfolio.getMemberId()).isEqualTo(1L);
        assertThat(savedPortfolio.getName()).isEqualTo(name);
        assertThat(savedPortfolio.getDescription()).isEqualTo(description);
        assertThat(savedPortfolio.getCurrency()).isEqualTo(KRW);
        assertThat(savedPortfolio.getIncludeType()).isEqualTo(Y);
        assertThat(savedPortfolio.getType()).isEqualTo(REAL);
        assertThat(statistic.getTotalAsset()).isEqualTo(getBD(66000));
        assertThat(statistic.getTotalBuy()).isEqualTo(getBD(33000));
        assertThat(statistic.getTotalGain()).isEqualTo(getBD(33000));
        assertThat(statistic.getGainRate()).isEqualTo(getBD(100));
    }

    @Test
    @DisplayName("포트폴리오 1건을 생성한다.")
    void createPortfolio() {
        // given
        Long memberId = 1L;
        String name = "portfolio name";
        String description = "portfolio description";
        Currency currency = KRW;
        PortfolioType type = REAL;

        PortfolioCreate portfolioCreate = PortfolioCreate.builder()
                .name(name)
                .description(description)
                .currency(currency)
                .type(type)
                .build();

        // when
        Portfolio savedPortfolio = portfolioService.create(portfolioCreate, memberId);

        // then
        assertThat(savedPortfolio.getId()).isEqualTo(1L);
        assertThat(savedPortfolio.getMemberId()).isEqualTo(memberId);
        assertThat(savedPortfolio.getName()).isEqualTo(name);
        assertThat(savedPortfolio.getDescription()).isEqualTo(description);
        assertThat(savedPortfolio.getCurrency()).isEqualTo(currency);
        assertThat(savedPortfolio.getIncludeType()).isEqualTo(Y);
        assertThat(savedPortfolio.getType()).isEqualTo(REAL);
    }

    @Test
    @DisplayName("포트폴리오 1건을 수정한다.")
    void updatePortfolio() {
        // given
        Long memberId = 1L;
        String updateName = "변경된 포트폴리오 이름";
        String updatedDescription = "변경된 포트폴리오 설명";
        Currency currency = KRW;
        IncludeType updatedIncludeYn = N;
        PortfolioType updatedType = FAKE;

        Portfolio portfolio = Portfolio.builder()
                .memberId(memberId)
                .name("포트폴리오 이름")
                .description("포트폴리오 설명")
                .currency(currency)
                .includeType(Y)
                .type(REAL)
                .build();

        Portfolio savedPortfolio = fakePortfolioRepository.save(portfolio);

        PortfolioUpdate portfolioUpdate = PortfolioUpdate.builder()
                .id(savedPortfolio.getId())
                .name(updateName)
                .description(updatedDescription)
                .currency(savedPortfolio.getCurrency())
                .includeType(updatedIncludeYn)
                .type(updatedType)
                .build();

        // when
        Portfolio updatedPortfolio =
                portfolioService.update(portfolioUpdate, savedPortfolio.getId(), memberId);

        // then
        assertThat(updatedPortfolio.getId()).isEqualTo(1L);
        assertThat(updatedPortfolio.getMemberId()).isEqualTo(memberId);
        assertThat(updatedPortfolio.getName()).isEqualTo(updateName);
        assertThat(updatedPortfolio.getDescription()).isEqualTo(updatedDescription);
        assertThat(updatedPortfolio.getCurrency()).isEqualTo(KRW);
        assertThat(updatedPortfolio.getIncludeType()).isEqualTo(updatedIncludeYn);
        assertThat(updatedPortfolio.getType()).isEqualTo(updatedType);
    }

    @Test
    @DisplayName("존재하지 않는 포트폴리오를 수정할 수 없다.")
    void updatePortfolioWithNoResult() {
        // given
        Long memberId = 1L;
        Long portfolioId = 1L;
        PortfolioUpdate portfolioUpdate = PortfolioUpdate.builder().build();

        // expected
        assertThatThrownBy(() -> portfolioService.update(portfolioUpdate, portfolioId, memberId))
                .isInstanceOf(PortfolioNotFound.class)
                .hasMessage(PORTFOLIO_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("보유종목이 없을 때 포트폴리오 1건을 삭제한다.")
    void deletePortfolio() {
        // given
        Long portfolioId = 1L;
        Portfolio portfolio = Portfolio.builder().build();

        fakePortfolioRepository.save(portfolio);

        // when
        portfolioService.delete(portfolioId);

        // then
        assertThatThrownBy(() -> portfolioService.getPortfolio(portfolioId))
                .isInstanceOf(PortfolioNotFound.class)
                .hasMessage(PORTFOLIO_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("보유종목이 있을 때 포트폴리오 1건을 삭제한다.")
    void deletePortfolioWithHoldingStock() {
        // given
        Long portfolioId = 1L;
        Portfolio portfolio = Portfolio.builder().id(portfolioId).build();
        HoldingStock holdingStock = HoldingStock.builder()
                .id(1L)
                .portfolio(portfolio).build();

        fakePortfolioRepository.save(portfolio);
        fakeHoldingStockRepository.save(holdingStock);

        // when
        portfolioService.delete(portfolioId);

        // then
        assertThatThrownBy(() -> portfolioService.getPortfolio(portfolioId))
                .isInstanceOf(PortfolioNotFound.class)
                .hasMessage(PORTFOLIO_NOT_FOUND.getMessage());
        assertThat(fakeHoldingStockService.getByPortfolioId(portfolioId)).isEmpty();
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
}
