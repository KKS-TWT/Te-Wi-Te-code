package spofo.docs.portfolio;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spofo.global.component.utils.CommonUtils.getBD;
import static spofo.portfolio.domain.enums.Currency.KRW;
import static spofo.portfolio.domain.enums.IncludeType.N;
import static spofo.portfolio.domain.enums.IncludeType.Y;
import static spofo.portfolio.domain.enums.PortfolioType.REAL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import spofo.docs.RestDocsSupport;
import spofo.portfolio.controller.PortfolioController;
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

public class PortfolioControllerDocsTest extends RestDocsSupport {

    public static final String AUTH_TOKEN = "JWT";
    private final PortfolioService portfolioService = mock(PortfolioService.class);

    @Override
    protected Object initController() {
        return new PortfolioController(portfolioService);
    }

    @Test
    @DisplayName("포트폴리오를 생성한다.")
    void createPortfolio() throws Exception {
        // given
        Portfolio portfolio = Portfolio.of(portfolioCreate(), 1L);

        setField(portfolio, "id", 1L);

        given(portfolioService.create(any(PortfolioCreate.class), nullable(Long.class)))
                .willReturn(portfolio);

        // expected
        mockMvc.perform(post("/portfolios")
                        .header(AUTHORIZATION, AUTH_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createParams())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L))
                .andDo(document("portfolio-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(contentTypeHeader(), authorizationHeader()),
                        requestFields(
                                name(),
                                description().optional(),
                                currency(),
                                portfolioType()
                        ),
                        responseHeaders(contentTypeHeader()),
                        responseFields(id())
                ));
    }

    @Test
    @DisplayName("포트폴리오 1건을 조회한다.")
    void getPortfolio() throws Exception {
        // given
        Portfolio portfolio = Portfolio.of(portfolioCreate(), 1L);

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
                .andExpect(jsonPath("memberId").value(1L))
                .andExpect(jsonPath("name").value("포트폴리오 이름입니다."))
                .andExpect(jsonPath("description").value("포트폴리오 설명입니다."))
                .andExpect(jsonPath("currency").value("KRW"))
                .andExpect(jsonPath("includeType").value("true"))
                .andExpect(jsonPath("type").value("REAL"))
                .andDo(document("portfolio-retrieve",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(portfolioIdPathParam()),
                        requestHeaders(contentTypeHeader(), authorizationHeader()),
                        responseHeaders(contentTypeHeader()),
                        responseFields(
                                id(),
                                memberId(),
                                name(),
                                memberId(),
                                description(),
                                currency(),
                                includeType(),
                                portfolioType()
                        )
                ));
    }

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
                        .header("authorization", "token")
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("totalAsset").value("1000"))
                .andExpect(jsonPath("gain").value("100"))
                .andExpect(jsonPath("gainRate").value("10"));
    }

    @DisplayName("포트폴리오 여러 건을 조회한다.")
    void getPortfolioSimple() throws Exception {
        // given
        Long memberId = 1L;
        PortfolioCreate portfolioCreate = portfolioCreate();
        Portfolio portfolio = Portfolio.of(portfolioCreate, memberId);

        setField(portfolio, "id", 1L);

        PortfolioStatistic statistic = PortfolioStatistic.builder()
                .portfolio(portfolio)
                .totalAsset(getBD(100))
                .totalBuy(getBD(60))
                .totalGain(getBD(40))
                .gainRate(getBD(40))
                .build();

        given(portfolioService.getPortfolios(anyLong(), any()))
                .willReturn(List.of(statistic));

        // expected
        mockMvc.perform(get("/portfolios")
                        .header("authorization", "token")
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].name").value(portfolioCreate.getName()))
                .andExpect(jsonPath("$.[0].description").value(portfolioCreate.getDescription()))
                .andExpect(jsonPath("$.[0].totalAsset").value("100"))
                .andExpect(jsonPath("$.[0].totalBuy").value("60"))
                .andExpect(jsonPath("$.[0].gain").value("40"))
                .andExpect(jsonPath("$.[0].gainRate").value("40"))
                .andExpect(jsonPath("$.[0].includeType").value("true"))
                .andExpect(jsonPath("$.[0].type").value(REAL.toString()));
    }

    @DisplayName("포트폴리오 1개의 통계를 담은 데이터를 조회한다.")
    void getPortfolioStatistic() throws Exception {
        // given
        Long memberId = 1L;
        PortfolioCreate portfolioCreate = portfolioCreate();
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
                        .header("authorization", "token")
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value("1"))
                .andExpect(jsonPath("memberId").value("1"))
                .andExpect(jsonPath("name").value(portfolioCreate.getName()))
                .andExpect(jsonPath("description").value(portfolioCreate.getDescription()))
                .andExpect(jsonPath("currency").value("KRW"))
                .andExpect(jsonPath("includeType").value("true"))
                .andExpect(jsonPath("type").value("REAL"))
                .andExpect(jsonPath("totalAsset").value("100"))
                .andExpect(jsonPath("totalBuy").value("60"))
                .andExpect(jsonPath("gain").value("40"))
                .andExpect(jsonPath("gainRate").value("40"));
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

        PortfolioCreate portfolioCreate = portfolioCreate();

        PortfolioUpdate portfolioUpdate =
                update(1L, updatedName, description, currency, updatedIncludeType, type);

        Portfolio portfolio = Portfolio.of(portfolioCreate, memberId);
        Portfolio updatedPortfolio = portfolio.update(portfolioUpdate, memberId);

        given(portfolioService.update(any(PortfolioUpdate.class), anyLong(), anyLong()))
                .willReturn(updatedPortfolio);

        // expected
        mockMvc.perform(put("/portfolios/" + 1)
                        .header("authorization", "token")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("포트폴리오를 삭제한다.")
    void deletePortfolio() throws Exception {
        // given
        // expected
        mockMvc.perform(delete("/portfolios/" + 1)
                        .header("authorization", "token")
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    private Map<String, String> createParams() {
        Map<String, String> params = new HashMap<>();

        params.put("name", "포트폴리오 이름입니다.");
        params.put("description", "포트폴리오 설명입니다.");
        params.put("currency", KRW.toString());
        params.put("type", REAL.toString());

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

    private PortfolioCreate portfolioCreate() {
        return PortfolioCreate.builder()
                .name("포트폴리오 이름입니다.")
                .description("포트폴리오 설명입니다.")
                .currency(KRW)
                .type(REAL)
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

    private ParameterDescriptor portfolioIdPathParam() {
        return parameterWithName("portfolioId")
                .attributes(field("type", NUMBER.toString()))
                .attributes(koreanTitle("포트폴리오 아이디"))
                .description("포트폴리오의 아이디");
    }

    private FieldDescriptor id() {
        return fieldWithPath("id")
                .type(NUMBER)
                .attributes(koreanTitle("포트폴리오 아이디"))
                .description("포트폴리오의 아이디");
    }

    private FieldDescriptor memberId() {
        return fieldWithPath("memberId")
                .type(NUMBER)
                .attributes(koreanTitle("회원 아이디"))
                .description("포트폴리오를 소유한 회원의 아이디");
    }

    private FieldDescriptor name() {
        return fieldWithPath("name")
                .type(STRING)
                .attributes(koreanTitle("포트폴리오 이름"))
                .description("포트폴리오의 이름");
    }

    private FieldDescriptor description() {
        return fieldWithPath("description")
                .type(STRING)
                .attributes(koreanTitle("포트폴리오 설명"))
                .description("포트폴리오의 설명");
    }

    private FieldDescriptor currency() {
        return fieldWithPath("currency")
                .type(STRING)
                .attributes(koreanTitle("기준통화"))
                .description(enumDesc(Currency.class, "포트폴리오의 기준통화"));
    }

    private FieldDescriptor includeType() {
        return fieldWithPath("includeType")
                .type(BOOLEAN)
                .attributes(koreanTitle("개요 포함 여부"))
                .description("포트폴리오의 개요 포함 여부");
    }

    private FieldDescriptor portfolioType() {
        return fieldWithPath("type")
                .type(STRING)
                .attributes(koreanTitle("포트폴리오 타입"))
                .description(enumDesc(PortfolioType.class, "포트폴리오의 타입"));
    }
}
