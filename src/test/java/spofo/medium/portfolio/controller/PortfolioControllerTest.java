package spofo.medium.portfolio.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spofo.global.component.utils.CommonUtils.getBD;
import static spofo.portfolio.domain.enums.Currency.KRW;
import static spofo.portfolio.domain.enums.IncludeType.N;
import static spofo.portfolio.domain.enums.IncludeType.Y;
import static spofo.portfolio.domain.enums.PortfolioType.FAKE;
import static spofo.portfolio.domain.enums.PortfolioType.LINK;
import static spofo.portfolio.domain.enums.PortfolioType.REAL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.global.domain.exception.PortfolioNotFound;
import spofo.portfolio.controller.request.PortfolioSearchCondition;
import spofo.portfolio.domain.Portfolio;
import spofo.portfolio.domain.PortfolioCreate;
import spofo.portfolio.domain.PortfolioStatistic;
import spofo.portfolio.domain.PortfolioUpdate;
import spofo.portfolio.domain.TotalPortfoliosStatistic;
import spofo.portfolio.domain.enums.Currency;
import spofo.portfolio.domain.enums.IncludeType;
import spofo.portfolio.domain.enums.PortfolioType;
import spofo.support.controller.ControllerTestSupport;

public class PortfolioControllerTest extends ControllerTestSupport {

    public static final String AUTH_TOKEN = "JWT";

    @Test
    @DisplayName("포트폴리오를 생성한다.")
    void createPortfolio() throws Exception {
        // given
        Long memberId = 1L;
        String name = "portfolio name";
        String description = "portfolio description";

        Map<String, String> params = createParams(name, description, KRW, REAL);
        PortfolioCreate portfolioCreate = create(name, description, KRW, REAL);
        Portfolio portfolio = Portfolio.of(portfolioCreate, memberId);

        setField(portfolio, "id", 1L);

        given(portfolioService.create(any(PortfolioCreate.class), anyLong()))
                .willReturn(portfolio);

        // expected
        mockMvc.perform(post("/portfolios")
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L));
    }

    @Test
    @DisplayName("포트폴리오 생성 시 이름은 필수 입력이다.")
    void createPortfolioWithNoName() throws Exception {
        // given
        String name = "   ";
        String description = "portfolio description";

        Map<String, String> params = createParams(name, description, KRW, REAL);

        // expected
        mockMvc.perform(post("/portfolios")
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("name"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("포트폴리오 이름은 필수 입력입니다."));
    }

    @Test
    @DisplayName("포트폴리오 생성 시 설명은 필수 입력이 아니다.")
    void createPortfolioWithNoDescription() throws Exception {
        // given
        Long memberId = 1L;
        String name = "portfolio name";

        Map<String, String> params = createParams(name, null, KRW, REAL);
        PortfolioCreate portfolioCreate = create(name, null, KRW, REAL);
        Portfolio portfolio = Portfolio.of(portfolioCreate, memberId);

        setField(portfolio, "id", 1L);

        given(portfolioService.create(any(PortfolioCreate.class), anyLong()))
                .willReturn(portfolio);

        // expected
        mockMvc.perform(post("/portfolios")
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L));
    }

    @Test
    @DisplayName("포트폴리오 생성 시 통화는 필수 입력이다.")
    void createPortfolioWithNoCurrency() throws Exception {
        // given
        String name = "portfolio name";
        String description = "portfolio description";

        Map<String, String> params = createParams(name, description, null, REAL);

        // expected
        mockMvc.perform(post("/portfolios")
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("currency"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("포트폴리오 통화는 필수 입력입니다."));
    }

    @Test
    @DisplayName("포트폴리오 생성 시 타입는 필수 입력이다.")
    void createPortfolioWithNoType() throws Exception {
        // given
        String name = "portfolio name";
        String description = "portfolio description";

        Map<String, String> params = createParams(name, description, KRW, null);

        // expected
        mockMvc.perform(post("/portfolios")
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("type"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("포트폴리오 타입은 필수 입력입니다."));
    }

    @Test
    @DisplayName("포트폴리오 1건을 조회한다.")
    void getPortfolio() throws Exception {
        // given
        Long memberId = 1L;
        String name = "portfolio name";
        String description = "portfolio description";
        PortfolioCreate portfolioCreate = create(name, description, KRW, REAL);
        Portfolio portfolio = Portfolio.of(portfolioCreate, memberId);

        setField(portfolio, "id", 1L);

        given(portfolioService.getPortfolio(anyLong()))
                .willReturn(portfolio);

        // expected
        mockMvc.perform(get("/portfolios/{portfolioId}", 1L)
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("memberId").value(memberId))
                .andExpect(jsonPath("name").value(name))
                .andExpect(jsonPath("description").value(description))
                .andExpect(jsonPath("currency").value("KRW"))
                .andExpect(jsonPath("includeType").value("true"))
                .andExpect(jsonPath("type").value("REAL"));
    }

    @Test
    @DisplayName("전체 포트폴리오에 대한 개요를 조회한다.")
    void getPortfoliosStatistic() throws Exception {
        // given
        TotalPortfoliosStatistic statistic = TotalPortfoliosStatistic.builder()
                .totalAsset(getBD(1000))
                .gain(getBD(100))
                .gainRate(getBD(10))
                .build();

        given(portfolioService.getPortfoliosStatistic(anyLong(),
                any(PortfolioSearchCondition.class)))
                .willReturn(statistic);

        // expected
        mockMvc.perform(get("/portfolios/total")
                        .param("type", "REAL")
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("totalAsset").value("1000"))
                .andExpect(jsonPath("gain").value("100"))
                .andExpect(jsonPath("gainRate").value("10"));
    }

    @Test
    @DisplayName("포트폴리오 여러 건을 조회한다.")
    void getPortfolioSimple() throws Exception {
        // given
        Long memberId = 1L;
        String name = "portfolio name";
        String description = "portfolio description";
        PortfolioCreate portfolioCreate = create(name, description, KRW, REAL);
        Portfolio portfolio = Portfolio.of(portfolioCreate, memberId);

        setField(portfolio, "id", 1L);

        PortfolioStatistic statistic = PortfolioStatistic.builder()
                .portfolio(portfolio)
                .totalAsset(getBD(100))
                .totalBuy(getBD(60))
                .totalGain(getBD(40))
                .gainRate(getBD(40))
                .build();

        PortfolioSearchCondition condition = PortfolioSearchCondition.builder().type(null)
                .build();

        given(portfolioService.getPortfolios(anyLong(), eq(condition)))
                .willReturn(List.of(statistic));

        // expected
        mockMvc.perform(get("/portfolios")
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].name").value(name))
                .andExpect(jsonPath("$.[0].description").value(description))
                .andExpect(jsonPath("$.[0].totalAsset").value("100"))
                .andExpect(jsonPath("$.[0].totalBuy").value("60"))
                .andExpect(jsonPath("$.[0].gain").value("40"))
                .andExpect(jsonPath("$.[0].gainRate").value("40"))
                .andExpect(jsonPath("$.[0].includeType").value("true"))
                .andExpect(jsonPath("$.[0].type").value(REAL.toString()));
    }

    @Test
    @DisplayName("포트폴리오 [REAL] 필터링을 하여 조회한다.")
    void getPortfolioSimpleWithREAL() throws Exception {
        // given
        Long memberId = 1L;
        String name = "portfolio name";
        String description = "portfolio description";
        PortfolioCreate portfolioCreate = create(name, description, KRW, REAL);
        Portfolio portfolio = Portfolio.of(portfolioCreate, memberId);

        setField(portfolio, "id", 1L);

        PortfolioStatistic statistic = PortfolioStatistic.builder()
                .portfolio(portfolio)
                .totalAsset(getBD(100))
                .totalBuy(getBD(60))
                .totalGain(getBD(40))
                .gainRate(getBD(40))
                .build();

        PortfolioSearchCondition condition = PortfolioSearchCondition.builder().type(REAL).build();

        given(portfolioService.getPortfolios(anyLong(), eq(condition)))
                .willReturn(List.of(statistic));

        // expected
        mockMvc.perform(get("/portfolios?type=REAL")
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].name").value(name))
                .andExpect(jsonPath("$.[0].description").value(description))
                .andExpect(jsonPath("$.[0].totalAsset").value("100"))
                .andExpect(jsonPath("$.[0].totalBuy").value("60"))
                .andExpect(jsonPath("$.[0].gain").value("40"))
                .andExpect(jsonPath("$.[0].gainRate").value("40"))
                .andExpect(jsonPath("$.[0].includeType").value("true"))
                .andExpect(jsonPath("$.[0].type").value(REAL.toString()));
    }

    @Test
    @DisplayName("포트폴리오 [FAKE] 필터링을 하여 조회한다.")
    void getPortfolioSimpleWithFAKE() throws Exception {
        // given
        Long memberId = 1L;
        String name = "portfolio name";
        String description = "portfolio description";
        PortfolioCreate portfolioCreate = create(name, description, KRW, FAKE);
        Portfolio portfolio = Portfolio.of(portfolioCreate, memberId);

        setField(portfolio, "id", 1L);

        PortfolioStatistic statistic = PortfolioStatistic.builder()
                .portfolio(portfolio)
                .totalAsset(getBD(100))
                .totalBuy(getBD(60))
                .totalGain(getBD(40))
                .gainRate(getBD(40))
                .build();

        PortfolioSearchCondition condition = PortfolioSearchCondition.builder().type(FAKE)
                .build();

        given(portfolioService.getPortfolios(anyLong(), eq(condition)))
                .willReturn(List.of(statistic));

        // expected
        mockMvc.perform(get("/portfolios?type=FAKE")
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].name").value(name))
                .andExpect(jsonPath("$.[0].description").value(description))
                .andExpect(jsonPath("$.[0].totalAsset").value("100"))
                .andExpect(jsonPath("$.[0].totalBuy").value("60"))
                .andExpect(jsonPath("$.[0].gain").value("40"))
                .andExpect(jsonPath("$.[0].gainRate").value("40"))
                .andExpect(jsonPath("$.[0].includeType").value("true"))
                .andExpect(jsonPath("$.[0].type").value(FAKE.toString()));
    }

    @Test
    @DisplayName("포트폴리오 [LINK] 필터링을 하여 조회한다.")
    void getPortfolioSimpleWithLINK() throws Exception {
        // given
        Long memberId = 1L;
        String name = "portfolio name";
        String description = "portfolio description";
        PortfolioCreate portfolioCreate = create(name, description, KRW, LINK);
        Portfolio portfolio = Portfolio.of(portfolioCreate, memberId);

        setField(portfolio, "id", 1L);

        PortfolioStatistic statistic = PortfolioStatistic.builder()
                .portfolio(portfolio)
                .totalAsset(getBD(100))
                .totalBuy(getBD(60))
                .totalGain(getBD(40))
                .gainRate(getBD(40))
                .build();

        PortfolioSearchCondition condition = PortfolioSearchCondition.builder().type(LINK)
                .build();

        given(portfolioService.getPortfolios(anyLong(), eq(condition)))
                .willReturn(List.of(statistic));

        // expected
        mockMvc.perform(get("/portfolios?type=LINK")
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].name").value(name))
                .andExpect(jsonPath("$.[0].description").value(description))
                .andExpect(jsonPath("$.[0].totalAsset").value("100"))
                .andExpect(jsonPath("$.[0].totalBuy").value("60"))
                .andExpect(jsonPath("$.[0].gain").value("40"))
                .andExpect(jsonPath("$.[0].gainRate").value("40"))
                .andExpect(jsonPath("$.[0].includeType").value("true"))
                .andExpect(jsonPath("$.[0].type").value(LINK.toString()));
    }

    @Test
    @DisplayName("포트폴리오 내에 존재 하지 않는 타입을 필터링할시 조회되는 포트폴리오는 없다.")
    void getPortfolioSimpleNoExitType() throws Exception {
        // given
        Long memberId = 1L;
        String name = "portfolio name";
        String description = "portfolio description";
        PortfolioCreate portfolioCreate = create(name, description, KRW, REAL);
        Portfolio portfolio = Portfolio.of(portfolioCreate, memberId);

        setField(portfolio, "id", 1L);

        PortfolioStatistic statistic = PortfolioStatistic.builder()
                .portfolio(portfolio)
                .totalAsset(getBD(100))
                .totalBuy(getBD(60))
                .totalGain(getBD(40))
                .gainRate(getBD(40))
                .build();

        PortfolioSearchCondition condition = PortfolioSearchCondition.builder().type(LINK)
                .build();

        given(portfolioService.getPortfolios(anyLong(), eq(condition)))
                .willReturn(List.of());

        // expected
        mockMvc.perform(get("/portfolios?filter=LINK")
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("잘못된 타입으로 요청 시 조회되는 포트폴리오는 없다.")
    void getPortfolioSimpleWithWrongType() throws Exception {
        // given
        Long memberId = 1L;
        String name = "portfolio name";
        String description = "portfolio description";
        PortfolioCreate portfolioCreate = create(name, description, KRW, REAL);
        Portfolio portfolio = Portfolio.of(portfolioCreate, memberId);

        setField(portfolio, "id", 1L);

        PortfolioStatistic statistic = PortfolioStatistic.builder()
                .portfolio(portfolio)
                .totalAsset(getBD(100))
                .totalBuy(getBD(60))
                .totalGain(getBD(40))
                .gainRate(getBD(40))
                .build();

        PortfolioSearchCondition condition = PortfolioSearchCondition.builder().type(LINK)
                .build();

        given(portfolioService.getPortfolios(anyLong(), eq(condition)))
                .willReturn(List.of());

        // expected
        mockMvc.perform(get("/portfolios?filter=??")
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("포트폴리오 1개의 통계를 담은 데이터를 조회한다.")
    void getPortfolioStatistic() throws Exception {
        // given
        Long memberId = 1L;
        String name = "portfolio name";
        String description = "portfolio description";
        PortfolioCreate portfolioCreate = create(name, description, KRW, REAL);
        Portfolio portfolio = Portfolio.of(portfolioCreate, memberId);

        setField(portfolio, "id", 1L);

        PortfolioStatistic statistic = PortfolioStatistic.builder()
                .portfolio(portfolio)
                .totalAsset(getBD(100))
                .totalBuy(getBD(60))
                .totalGain(getBD(40))
                .gainRate(getBD(40))
                .build();

        given(portfolioService.getPortfolioStatistic(anyLong()))
                .willReturn(statistic);

        // expected
        mockMvc.perform(get("/portfolios/" + 1 + "/total")
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value("1"))
                .andExpect(jsonPath("memberId").value("1"))
                .andExpect(jsonPath("name").value(name))
                .andExpect(jsonPath("description").value(description))
                .andExpect(jsonPath("currency").value("KRW"))
                .andExpect(jsonPath("includeType").value("true"))
                .andExpect(jsonPath("type").value("REAL"))
                .andExpect(jsonPath("totalAsset").value("100"))
                .andExpect(jsonPath("totalBuy").value("60"))
                .andExpect(jsonPath("gain").value("40"))
                .andExpect(jsonPath("gainRate").value("40"));
    }

    @Test
    @DisplayName("존재하지 않는 포트폴리오는 조회할 수 없다.")
    void getPortfolioWithNotValidId() throws Exception {
        // given
        given(portfolioService.getPortfolio(anyLong()))
                .willThrow(new PortfolioNotFound());

        // expected
        mockMvc.perform(get("/portfolios/" + 1)
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorMessage").value("포트폴리오를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("포트폴리오를 수정한다.")
    void updatePortfolio() throws Exception {
        // given
        Long memberId = 1L;
        String name = "portfolio name";
        String description = "portfolio description";
        Currency currency = KRW;
        PortfolioType type = REAL;

        String updatedName = "updated name";
        IncludeType updatedIncludeType = N;

        Map<String, String> params =
                updateParams(1L, updatedName, description, currency, updatedIncludeType, type);

        PortfolioCreate portfolioCreate = create(name, description, currency, type);

        PortfolioUpdate portfolioUpdate =
                update(1L, updatedName, description, currency, updatedIncludeType, type);

        Portfolio portfolio = Portfolio.of(portfolioCreate, memberId);
        Portfolio updatedPortfolio = portfolio.update(portfolioUpdate, memberId);

        given(portfolioService.update(any(PortfolioUpdate.class), anyLong(), anyLong()))
                .willReturn(updatedPortfolio);

        // expected
        mockMvc.perform(put("/portfolios/" + 1)
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("포트폴리오를 수정 시 포트폴리오 아이디는 필수 입력이다.")
    void updateWithNoId() throws Exception {
        // given
        String name = "portfolio name";
        String description = "portfolio description";
        Currency currency = KRW;
        IncludeType updatedIncludeType = N;
        PortfolioType type = REAL;

        Map<String, String> params =
                updateParams(null, name, description, currency, updatedIncludeType, type);

        // expected
        mockMvc.perform(put("/portfolios/" + 1)
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("id"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("포트폴리오 아이디는 필수 입력입니다."));
    }

    @Test
    @DisplayName("포트폴리오를 수정 시 포트폴리오 이름은 필수 입력이다.")
    void updateWithNoName() throws Exception {
        // given
        String name = "  ";
        String description = "portfolio description";
        Currency currency = KRW;
        IncludeType updatedIncludeType = N;
        PortfolioType type = REAL;

        Map<String, String> params =
                updateParams(1L, name, description, currency, updatedIncludeType, type);

        // expected
        mockMvc.perform(put("/portfolios/" + 1)
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("name"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("포트폴리오 이름은 필수 입력입니다."));
    }

    @Test
    @DisplayName("포트폴리오를 수정 시 포트폴리오 통화는 필수 입력이다.")
    void updateWithNoCurrency() throws Exception {
        // given
        String name = "portfolio name";
        String description = "portfolio description";
        IncludeType updatedIncludeType = N;
        PortfolioType type = REAL;

        Map<String, String> params =
                updateParams(1L, name, description, null, updatedIncludeType, type);

        // expected
        mockMvc.perform(put("/portfolios/" + 1)
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("currency"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("포트폴리오 통화는 필수 입력입니다."));
    }

    @Test
    @DisplayName("포트폴리오를 수정 시 포트폴리오 통화는 필수 입력이다.")
    void updateWithNoincludeType() throws Exception {
        // given
        String name = "portfolio name";
        String description = "portfolio description";
        Currency currency = KRW;
        PortfolioType type = REAL;

        Map<String, String> params =
                updateParams(1L, name, description, currency, null, type);

        // expected
        mockMvc.perform(put("/portfolios/" + 1)
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("includeType"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("포트폴리오 포함여부는 필수 입력입니다."));
    }

    @Test
    @DisplayName("포트폴리오를 수정 시 포트폴리오 타입은 필수 입력이다.")
    void updateWithNoType() throws Exception {
        // given
        String name = "portfolio name";
        String description = "portfolio description";
        Currency currency = KRW;
        IncludeType includeType = Y;

        Map<String, String> params =
                updateParams(1L, name, description, currency, includeType, null);

        // expected
        mockMvc.perform(put("/portfolios/" + 1)
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("type"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("포트폴리오 타입은 필수 입력입니다."));
    }

    @Test
    @DisplayName("포트폴리오를 삭제한다.")
    void deletePortfolio() throws Exception {
        // given
        // expected
        mockMvc.perform(delete("/portfolios/" + 1)
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    private Map<String, String> createParams(String name, String description, Currency currency,
            PortfolioType type) {

        Map<String, String> params = new HashMap<>();

        params.put("name", name);
        params.put("description", description);
        params.put("currency", currency == null ? null : currency.toString());
        params.put("type", type == null ? null : type.toString());

        return params;
    }

    private Map<String, String> updateParams(Long id, String name, String description,
            Currency currency, IncludeType includeType, PortfolioType type) {

        Map<String, String> params = new HashMap<>();

        params.put("id", String.valueOf(id));
        params.put("name", name);
        params.put("description", description);
        params.put("includeType", includeType == null ?
                null : (includeType == Y ? "true" : "false"));
        params.put("currency", currency == null ? null : currency.toString());
        params.put("type", type == null ? null : type.toString());

        return params;
    }

    private PortfolioCreate create(String name, String description, Currency currency,
            PortfolioType type) {

        return PortfolioCreate.builder()
                .name(name)
                .description(description)
                .currency(currency)
                .type(type)
                .build();
    }

    private PortfolioUpdate update(Long id, String name, String description,
            Currency currency, IncludeType includeType, PortfolioType type) {

        return PortfolioUpdate.builder()
                .id(id)
                .name(name)
                .description(description)
                .currency(currency)
                .includeType(includeType)
                .type(type)
                .build();
    }
}
