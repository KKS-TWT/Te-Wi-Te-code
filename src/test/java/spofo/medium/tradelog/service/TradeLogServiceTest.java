package spofo.medium.tradelog.service;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static spofo.global.component.utils.CommonUtils.getBD;
import static spofo.tradelog.domain.enums.TradeType.BUY;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.holdingstock.domain.HoldingStock;
import spofo.stock.domain.Stock;
import spofo.support.service.ServiceTestSupport;
import spofo.tradelog.domain.TradeLog;
import spofo.tradelog.domain.TradeLogCreate;
import spofo.tradelog.domain.TradeLogStatistic;

public class TradeLogServiceTest extends ServiceTestSupport {

    private static final String TEST_STOCK_CODE = "101010";

    @Test
    @DisplayName("매매이력 1건을 생성한다.")
    void createTradeLog() {
        // given
        HoldingStock holdingStock = HoldingStock.builder()
                .stockCode(TEST_STOCK_CODE)
                .build();
        TradeLogCreate tradeLogCreate = getTradeLogCreate(TEN, ONE);
        given(mockStockServerService.getStock(TEST_STOCK_CODE)).willReturn(Stock.builder()
                .code(TEST_STOCK_CODE)
                .price(TEN).build());

        // when
        TradeLog savedTradeLog = tradeLogService.create(tradeLogCreate, holdingStock);

        // then
        assertThat(savedTradeLog.getId()).isNotNull();
        assertThat(savedTradeLog.getPrice()).isEqualTo(TEN);
        assertThat(savedTradeLog.getType()).isEqualTo(BUY);
        assertThat(savedTradeLog.getQuantity()).isEqualTo(ONE);
        assertThat(savedTradeLog.getMarketPrice()).isEqualTo(TEN);
    }

    @Test
    @DisplayName("1개의 종목이력 ID로 1개 매매이력 통계를 조회한다.")
    void getTradeLogStatistics1() {
        // given
        HoldingStock holdingStock = getHoldingStock();
        TradeLogCreate tradeLogCreate = getTradeLogCreate(getBD(33_000), ONE);
        Stock stock = getStock();

        TradeLog log1 = TradeLog.of(tradeLogCreate, holdingStock, stock);

        given(holdingStockRepository.findByPortfolioId(anyLong())).willReturn(
                List.of(holdingStock));
        given(tradeLogRepository.findByHoldingStockEntityId(anyLong())).willReturn(List.of(log1));

        // when
        List<TradeLogStatistic> statistics = tradeLogService.getStatistics(1L);

        // then
        assertThat(statistics.get(0).getTotalPrice()).isEqualTo(getBD(33_000));
        assertThat(statistics.get(0).getTotalPrice()).isEqualTo(getBD(33_000));
    }

    @Test
    @DisplayName("1개의 종목이력 ID로 2개 매매이력 통계를 조회한다.")
    void getTradeLogStatistics2() {
        // given
        HoldingStock holdingStock = getHoldingStock();
        TradeLogCreate tradeLogCreate1 = getTradeLogCreate(getBD(33_000), ONE);
        TradeLogCreate tradeLogCreate2 = getTradeLogCreate(getBD(66_000), getBD(2));
        Stock stock = getStock();

        TradeLog log1 = TradeLog.of(tradeLogCreate1, holdingStock, stock);
        TradeLog log2 = TradeLog.of(tradeLogCreate2, holdingStock, stock);

        given(holdingStockRepository.findByPortfolioId(anyLong())).willReturn(
                List.of(holdingStock));
        given(tradeLogRepository.findByHoldingStockEntityId(anyLong())).willReturn(
                List.of(log1, log2));

        // when
        List<TradeLogStatistic> statistics = tradeLogService.getStatistics(1L);

        // then
        assertThat(statistics.size()).isEqualTo(2);
        assertThat(statistics.get(0).getTotalPrice()).isEqualTo(getBD(33_000));
        assertThat(statistics.get(0).getTotalPrice()).isEqualTo(getBD(33_000));

        assertThat(statistics.get(1).getTotalPrice()).isEqualTo(getBD(132000));
        assertThat(statistics.get(1).getGain()).isEqualTo(ZERO);
    }

    private TradeLogCreate getTradeLogCreate(BigDecimal price, BigDecimal quantity) {
        return TradeLogCreate.builder()
                .type(BUY)
                .price(price)
                .tradeDate(now())
                .quantity(quantity)
                .build();
    }

    private HoldingStock getHoldingStock() {
        return HoldingStock.builder()
                .id(1L)
                .stockCode(TEST_STOCK_CODE)
                .build();
    }

    private Stock getStock() {
        return Stock.builder()
                .code(TEST_STOCK_CODE)
                .price(getBD(66_000))
                .build();
    }
}
