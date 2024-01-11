package spofo.medium.holdingstock.controller;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spofo.global.component.utils.CommonUtils.getBD;
import static spofo.global.domain.exception.ErrorCode.HOLDING_STOCK_NOT_FOUND;
import static spofo.tradelog.domain.enums.TradeType.BUY;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.global.domain.exception.HoldingStockNotFound;
import spofo.holdingstock.controller.request.HoldingStockRequest;
import spofo.holdingstock.domain.HoldingStock;
import spofo.holdingstock.domain.HoldingStockCreate;
import spofo.holdingstock.domain.HoldingStockStatistic;
import spofo.portfolio.domain.Portfolio;
import spofo.stock.domain.Stock;
import spofo.support.controller.ControllerTestSupport;
import spofo.tradelog.domain.TradeLog;
import spofo.tradelog.domain.TradeLogCreate;

public class HoldingStockControllerTest extends ControllerTestSupport {

    public static final String AUTH_TOKEN = "JWT";
    private static final String TEST_STOCK_CODE = "101010";


    private Portfolio getPortfolio() {
        return Portfolio.builder()
                .build();
    }

    private TradeLog getTradeLog(BigDecimal price, BigDecimal quantity) {
        return TradeLog.builder()
                .type(BUY)
                .price(price)
                .quantity(quantity)
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

    private Stock getStock() {
        return Stock.builder()
                .code(TEST_STOCK_CODE)
                .name("SK하이닉스")
                .price(getBD(66000))
                .sector("반도체")
                .build();
    }

    @Test
    @DisplayName("1개의 포트폴리오에 속한 보유종목 리스트를 조회한다.")
    void getHoldingStocks() throws Exception {
        TradeLog log1 = getTradeLog(getBD(33000), ONE);
        TradeLog log2 = getTradeLog(getBD(28600), ONE);

        HoldingStock holdingStock = getHoldingStock(TEST_STOCK_CODE, getPortfolio(),
                List.of(log1, log2));

        HoldingStockStatistic statistic = HoldingStockStatistic.of(holdingStock, getStock());

        given(holdingStockService.getHoldingStockStatistics(anyLong()))
                .willReturn(List.of(statistic));

        // expected
        mockMvc.perform(get("/portfolios/{portfolioId}/stocks", 1L)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalAsset").value("132000"))
                .andExpect(jsonPath("$[0].gain").value("70400"))
                .andExpect(jsonPath("$[0].gainRate").value("114.29"))
                .andExpect(jsonPath("$[0].avgPrice").value("30800"))
                .andExpect(jsonPath("$[0].currentPrice").value("66000"))
                .andExpect(jsonPath("$[0].quantity").value("2"));
    }

    @Test
    @DisplayName("1개의 포트폴리오에 속한 보유종목이 없으면 비어있는 리스트를 조회한다.")
    void getHoldingStocksWithNoResult() throws Exception {
        given(holdingStockService.getHoldingStockStatistics(anyLong()))
                .willReturn(emptyList());

        // expected
        mockMvc.perform(get("/portfolios/{portfolioId}/stocks", 1L)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    @DisplayName("보유종목을 생성한다.")
    void createHoldingStock() throws Exception {
        // given
        HoldingStockRequest params = HoldingStockRequest.builder()
                .code(TEST_STOCK_CODE)
                .tradeDate(LocalDateTime.now())
                .quantity(ONE)
                .avgPrice(ONE)
                .build();

        HoldingStock holdingStock = HoldingStock.builder()
                .id(1L)
                .build();

        given(portfolioService.getPortfolio(anyLong()))
                .willReturn(Portfolio.builder().build());

        given(holdingStockService.create(any(HoldingStockCreate.class), any(TradeLogCreate.class),
                any(Portfolio.class)))
                .willReturn(holdingStock);

        // expected
        mockMvc.perform(put("/portfolios/{portfolioId}/stocks", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L));

        verify(holdingStockService, times(1))
                .create(any(HoldingStockCreate.class), any(TradeLogCreate.class),
                        any(Portfolio.class));
    }

    @Test
    @DisplayName("보유종목을 생성 시 이미 존재하는 보유종목에 대해서는 매매이력만 생성한다.")
    void createHoldingStockWithExistsHoldingStock() throws Exception {
        // given
        HoldingStockRequest params = HoldingStockRequest.builder()
                .code(TEST_STOCK_CODE)
                .tradeDate(LocalDateTime.now())
                .quantity(ONE)
                .avgPrice(ONE)
                .build();

        HoldingStock holdingStock = HoldingStock.builder()
                .id(1L)
                .build();

        given(portfolioService.getPortfolio(anyLong()))
                .willReturn(Portfolio.builder().build());

        given(holdingStockService.get(any(Portfolio.class), anyString()))
                .willReturn(holdingStock);

        given(tradeLogService.create(any(TradeLogCreate.class), any(HoldingStock.class)))
                .willReturn(TradeLog.builder().build());

        // expected
        mockMvc.perform(put("/portfolios/{portfolioId}/stocks", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L));

        verify(tradeLogService, times(1))
                .create(any(TradeLogCreate.class), any(HoldingStock.class));
    }

    @Test
    @DisplayName("보유종목 생성 시 종목코드는 필수 입력이다.")
    void createHoldingStockWithNoStockCode() throws Exception {
        // given
        HoldingStockRequest params = HoldingStockRequest.builder()
                .code(" ")
                .tradeDate(LocalDateTime.now())
                .quantity(ONE)
                .avgPrice(ONE)
                .build();

        // expected
        mockMvc.perform(put("/portfolios/{portfolioId}/stocks", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("code"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("종목코드는 필수 입력입니다."));
    }

    @Test
    @DisplayName("보유종목 생성 시 매수날짜 필수 입력이다.")
    void createHoldingStockWithNoTradeDate() throws Exception {
        // given
        HoldingStockRequest params = HoldingStockRequest.builder()
                .code(TEST_STOCK_CODE)
                .tradeDate(null)
                .quantity(ONE)
                .avgPrice(ONE)
                .build();

        // expected
        mockMvc.perform(put("/portfolios/{portfolioId}/stocks", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("tradeDate"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("매수날짜 필수 입력입니다."));
    }

    @Test
    @DisplayName("보유종목 생성 시 수량은 필수 입력이다.")
    void createHoldingStockWithNoQuantity() throws Exception {
        // given
        HoldingStockRequest params = HoldingStockRequest.builder()
                .code(TEST_STOCK_CODE)
                .tradeDate(LocalDateTime.now())
                .quantity(null)
                .avgPrice(ONE)
                .build();

        // expected
        mockMvc.perform(put("/portfolios/{portfolioId}/stocks", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("quantity"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("수량은 필수 입력입니다."));
    }

    @Test
    @DisplayName("보유종목 생성 시 평균단가는 필수 입력이다.")
    void createHoldingStockWithNoAvgPrice() throws Exception {
        // given
        HoldingStockRequest params = HoldingStockRequest.builder()
                .code(TEST_STOCK_CODE)
                .tradeDate(LocalDateTime.now())
                .quantity(ONE)
                .avgPrice(null)
                .build();

        // expected
        mockMvc.perform(put("/portfolios/{portfolioId}/stocks", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("avgPrice"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("평균단가는 필수 입력입니다."));
    }

    @Test
    @DisplayName("보유종목 생성 시 수량에 0을 입력할 수 없다.")
    void createHoldingStockWithZeroQuantity() throws Exception {
        // given
        HoldingStockRequest params = HoldingStockRequest.builder()
                .code(TEST_STOCK_CODE)
                .tradeDate(LocalDateTime.now())
                .quantity(ZERO)
                .avgPrice(ONE)
                .build();

        // expected
        mockMvc.perform(put("/portfolios/{portfolioId}/stocks", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("quantity"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("수량은 0보다 커야 합니다."));
    }

    @Test
    @DisplayName("보유종목 생성 시 수량은 0보다 커야 한다.")
    void createHoldingStockWithNegativeQuantity() throws Exception {
        // given
        HoldingStockRequest params = HoldingStockRequest.builder()
                .code(TEST_STOCK_CODE)
                .tradeDate(LocalDateTime.now())
                .quantity(getBD(-1))
                .avgPrice(ONE)
                .build();

        // expected
        mockMvc.perform(put("/portfolios/{portfolioId}/stocks", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("quantity"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("수량은 0보다 커야 합니다."));
    }

    @Test
    @DisplayName("보유종목 생성 시 평균단가에 0을 입력할 수 없다.")
    void createHoldingStockWithZeroAvgPrice() throws Exception {
        // given
        HoldingStockRequest params = HoldingStockRequest.builder()
                .code(TEST_STOCK_CODE)
                .tradeDate(LocalDateTime.now())
                .quantity(ONE)
                .avgPrice(ZERO)
                .build();

        // expected
        mockMvc.perform(put("/portfolios/{portfolioId}/stocks", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("avgPrice"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("평균단가는 0보다 커야 합니다."));
    }

    @Test
    @DisplayName("보유종목 생성 시 평균단가는 0보다 커야 한다.")
    void createHoldingStockWithNegativeAvgPrice() throws Exception {
        // given
        HoldingStockRequest params = HoldingStockRequest.builder()
                .code(TEST_STOCK_CODE)
                .tradeDate(LocalDateTime.now())
                .quantity(ONE)
                .avgPrice(ZERO)
                .build();

        // expected
        mockMvc.perform(put("/portfolios/{portfolioId}/stocks", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("avgPrice"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("평균단가는 0보다 커야 합니다."));
    }

    @Test
    @DisplayName("보유종목에 매매이력을 추가한다.")
    void addTradeLog() throws Exception {
        // given
        HoldingStockRequest params = HoldingStockRequest.builder()
                .code(TEST_STOCK_CODE)
                .tradeDate(LocalDateTime.now())
                .quantity(ONE)
                .avgPrice(ONE)
                .build();

        HoldingStock holdingStock = HoldingStock.builder()
                .id(1L)
                .build();

        given(holdingStockService.get(anyLong()))
                .willReturn(holdingStock);

        given(tradeLogService.create(any(TradeLogCreate.class), any(HoldingStock.class)))
                .willReturn(any(TradeLog.class));

        // expected
        mockMvc.perform(post("/portfolios/{portfolioId}/stocks/{stockId}", 1L, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L));
    }

    @Test
    @DisplayName("보유종목이 존재하지 않으면 매매이력을 추가할 수 없다.")
    void addTradeLogWithNoHoldingStock() throws Exception {
        // given
        HoldingStockRequest params = HoldingStockRequest.builder()
                .code(TEST_STOCK_CODE)
                .tradeDate(LocalDateTime.now())
                .quantity(ONE)
                .avgPrice(ONE)
                .build();

        given(holdingStockService.get(anyLong()))
                .willThrow(new HoldingStockNotFound());

        // expected
        mockMvc.perform(post("/portfolios/{portfolioId}/stocks/{stockId}", 1L, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(HOLDING_STOCK_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("보유종목에 매매이력 추가 시 종목코드는 필수 입력이다.")
    void addTradeLogWithNoStockCode() throws Exception {
        // given
        HoldingStockRequest params = HoldingStockRequest.builder()
                .code(" ")
                .tradeDate(LocalDateTime.now())
                .quantity(ONE)
                .avgPrice(ONE)
                .build();

        // expected
        mockMvc.perform(post("/portfolios/{portfolioId}/stocks/{stockId}", 1L, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("code"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("종목코드는 필수 입력입니다."));
    }

    @Test
    @DisplayName("보유종목에 매매이력 추가 시 매수날짜 필수 입력이다.")
    void addTradeLogWithNoTradeDate() throws Exception {
        // given
        HoldingStockRequest params = HoldingStockRequest.builder()
                .code(TEST_STOCK_CODE)
                .tradeDate(null)
                .quantity(ONE)
                .avgPrice(ONE)
                .build();

        // expected
        mockMvc.perform(post("/portfolios/{portfolioId}/stocks/{stockId}", 1L, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("tradeDate"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("매수날짜 필수 입력입니다."));
    }

    @Test
    @DisplayName("보유종목에 매매이력 추가 시 수량은 필수 입력이다.")
    void addTradeLogWithNoQuantity() throws Exception {
        // given
        HoldingStockRequest params = HoldingStockRequest.builder()
                .code(TEST_STOCK_CODE)
                .tradeDate(LocalDateTime.now())
                .quantity(null)
                .avgPrice(ONE)
                .build();

        // expected
        mockMvc.perform(post("/portfolios/{portfolioId}/stocks/{stockId}", 1L, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("quantity"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("수량은 필수 입력입니다."));
    }

    @Test
    @DisplayName("보유종목에 매매이력 추가 시 평균단가는 필수 입력이다.")
    void addTradeLogWithNoAvgPrice() throws Exception {
        // given
        HoldingStockRequest params = HoldingStockRequest.builder()
                .code(TEST_STOCK_CODE)
                .tradeDate(LocalDateTime.now())
                .quantity(ONE)
                .avgPrice(null)
                .build();

        // expected
        mockMvc.perform(post("/portfolios/{portfolioId}/stocks/{stockId}", 1L, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("avgPrice"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("평균단가는 필수 입력입니다."));
    }

    @Test
    @DisplayName("보유종목에 매매이력 추가 시 수량에 0을 입력할 수 없다.")
    void addTradeLogWithZeroQuantity() throws Exception {
        // given
        HoldingStockRequest params = HoldingStockRequest.builder()
                .code(TEST_STOCK_CODE)
                .tradeDate(LocalDateTime.now())
                .quantity(ZERO)
                .avgPrice(ONE)
                .build();

        // expected
        mockMvc.perform(post("/portfolios/{portfolioId}/stocks/{stockId}", 1L, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("quantity"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("수량은 0보다 커야 합니다."));
    }

    @Test
    @DisplayName("보유종목에 매매이력 추가 시 수량은 0보다 커야 한다.")
    void addTradeLogWithNegativeQuantity() throws Exception {
        // given
        HoldingStockRequest params = HoldingStockRequest.builder()
                .code(TEST_STOCK_CODE)
                .tradeDate(LocalDateTime.now())
                .quantity(getBD(-1))
                .avgPrice(ONE)
                .build();

        // expected
        mockMvc.perform(post("/portfolios/{portfolioId}/stocks/{stockId}", 1L, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("quantity"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("수량은 0보다 커야 합니다."));
    }

    @Test
    @DisplayName("보유종목에 매매이력 추가 시 평균단가에 0을 입력할 수 없다.")
    void addTradeLogWithZeroAvgPrice() throws Exception {
        // given
        HoldingStockRequest params = HoldingStockRequest.builder()
                .code(TEST_STOCK_CODE)
                .tradeDate(LocalDateTime.now())
                .quantity(ONE)
                .avgPrice(ZERO)
                .build();

        // expected
        mockMvc.perform(post("/portfolios/{portfolioId}/stocks/{stockId}", 1L, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("avgPrice"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("평균단가는 0보다 커야 합니다."));
    }

    @Test
    @DisplayName("보유종목에 매매이력 추가 시 평균단가는 0보다 커야 한다.")
    void addTradeLogWithNegativeAvgPrice() throws Exception {
        // given
        HoldingStockRequest params = HoldingStockRequest.builder()
                .code(TEST_STOCK_CODE)
                .tradeDate(LocalDateTime.now())
                .quantity(ONE)
                .avgPrice(ZERO)
                .build();

        // expected
        mockMvc.perform(post("/portfolios/{portfolioId}/stocks/{stockId}", 1L, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("avgPrice"))
                .andExpect(jsonPath("fieldErrors[0].errorMessage").value("평균단가는 0보다 커야 합니다."));
    }

    @Test
    @DisplayName("보유종목을 삭제한다.")
    void deleteHoldingStock() throws Exception {
        // given
        willDoNothing()
                .given(holdingStockService)
                .delete(anyLong());

        // expected
        mockMvc.perform(delete("/portfolios/{portfolioId}/stocks/{stockId}", 1L, 1L)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("존재하지 않는 보유종목을 삭제할 수 없다.")
    void deleteHoldingStockWithNoHoldingStock() throws Exception {
        // given
        willThrow(new HoldingStockNotFound())
                .willDoNothing()
                .given(holdingStockService)
                .delete(anyLong());

        // expected
        mockMvc.perform(delete("/portfolios/{portfolioId}/stocks/{stockId}", 1L, 1L)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(HOLDING_STOCK_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("보유종목 1건의 통계를 조회한다.")
    void getStatistic() throws Exception {
        // given
        TradeLog log1 = getTradeLog(getBD(33000), ONE);
        TradeLog log2 = getTradeLog(getBD(28600), ONE);

        HoldingStock holdingStock = getHoldingStock(TEST_STOCK_CODE, getPortfolio(),
                List.of(log1, log2));

        HoldingStockStatistic statistic = HoldingStockStatistic.of(holdingStock, getStock());

        given(holdingStockService.getStatistic(anyLong()))
                .willReturn(statistic);

        // expected
        mockMvc.perform(get("/portfolios/{portfolioId}/stocks/{stockId}", 1L, 1L)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value("1"))
                .andExpect(jsonPath("code").value("101010"))
                .andExpect(jsonPath("name").value("SK하이닉스"))
                .andExpect(jsonPath("sector").value("반도체"))
                .andExpect(jsonPath("totalAsset").value("132000"))
                .andExpect(jsonPath("gain").value("70400"))
                .andExpect(jsonPath("gainRate").value("114.29"))
                .andExpect(jsonPath("avgPrice").value("30800"))
                .andExpect(jsonPath("currentPrice").value("66000"))
                .andExpect(jsonPath("quantity").value("2"))
                .andExpect(jsonPath("imagePath").doesNotExist());
    }

    @Test
    @DisplayName("존재하지 않는 보유종목을 조회할 수 없다.")
    void getStatisticWithNoResult() throws Exception {
        // given
        given(holdingStockService.getStatistic(anyLong()))
                .willThrow(new HoldingStockNotFound());

        // expected
        mockMvc.perform(get("/portfolios/{portfolioId}/stocks/{stockId}", 1L, 1L)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value("400"))
                .andExpect(jsonPath("errorMessage").value(HOLDING_STOCK_NOT_FOUND.getMessage()));
    }
}
