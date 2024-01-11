package spofo.medium.tradelog.controller;

import static java.math.BigDecimal.ONE;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spofo.global.component.utils.CommonUtils.getBD;
import static spofo.tradelog.domain.enums.TradeType.BUY;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.support.controller.ControllerTestSupport;
import spofo.tradelog.domain.TradeLog;
import spofo.tradelog.domain.TradeLogStatistic;

public class TradeLogControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("1개의 보유종목의 1개의 매매이력을 조회한다.")
    void getTradeLogs1() throws Exception {
        TradeLog log1 = getTradeLog(getBD(33_000), ONE);
        TradeLogStatistic statistic = TradeLogStatistic.of(log1);

        given(tradeLogService.getStatistics(anyLong())).willReturn(List.of(statistic));

        // expected
        mockMvc.perform(get("/portfolios/{portfolioId}/stocks/{stockId}/trade-log", 1L, 1L)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("BUY"))
                .andExpect(jsonPath("$[0].avgPrice").value("33000"))
                .andExpect(jsonPath("$[0].quantity").value("1"))
                .andExpect(jsonPath("$[0].gain").value("33000"))
                .andExpect(jsonPath("$[0].totalPrice").value("33000"));
    }

    @Test
    @DisplayName("1개의 보유종목의 2개의 매매이력을 조회한다.")
    void getTradeLogs2() throws Exception {
        TradeLog log1 = getTradeLog(getBD(33_000), ONE);
        TradeLog log2 = getTradeLog(getBD(66_000), getBD(2));
        TradeLogStatistic statistic1 = TradeLogStatistic.of(log1);
        TradeLogStatistic statistic2 = TradeLogStatistic.of(log2);

        given(tradeLogService.getStatistics(anyLong())).willReturn(List.of(statistic1, statistic2));

        // expected
        mockMvc.perform(get("/portfolios/{portfolioId}/stocks/{stockId}/trade-log", 1L, 1L)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("BUY"))
                .andExpect(jsonPath("$[0].avgPrice").value("33000"))
                .andExpect(jsonPath("$[0].quantity").value("1"))
                .andExpect(jsonPath("$[0].gain").value("33000"))
                .andExpect(jsonPath("$[0].totalPrice").value("33000"))
                .andExpect(jsonPath("$[1].type").value("BUY"))
                .andExpect(jsonPath("$[1].avgPrice").value("66000"))
                .andExpect(jsonPath("$[1].quantity").value("2"))
                .andExpect(jsonPath("$[1].gain").value("0"))
                .andExpect(jsonPath("$[1].totalPrice").value("132000"));
    }

    private TradeLog getTradeLog(BigDecimal price, BigDecimal quantity) {
        return TradeLog.builder()
                .type(BUY)
                .price(price)
                .quantity(quantity)
                .marketPrice(getBD(66_000))
                .build();
    }
}
