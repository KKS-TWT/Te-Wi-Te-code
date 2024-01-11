package spofo.small.holdingstock.domain;

import static java.math.BigDecimal.ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static spofo.global.component.utils.CommonUtils.getBD;
import static spofo.tradelog.domain.enums.TradeType.BUY;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.holdingstock.domain.HoldingStock;
import spofo.holdingstock.domain.HoldingStockStatistic;
import spofo.stock.domain.Stock;
import spofo.tradelog.domain.TradeLog;

public class HoldingStockStatisticTest {

    private final String TEST_STOCK_CODE = "000660";
    private final String TEST_STOCK_NAME = "SK하이닉스";
    private final String TEST_STOCK_SECTOR = "반도체";
    private final BigDecimal TEST_STOCK_PRICE = getBD(66000);

    @Test
    @DisplayName("2건의 매매이력으로 보유종목 통계를 만든다.")
    void createHoldingStockStatisticFromHoldingStock() {
        // given
        TradeLog log1 = getTradeLog(getBD(33000), ONE);
        TradeLog log2 = getTradeLog(getBD(28600), ONE);

        HoldingStock holdingStock = getHoldingStock(List.of(log1, log2));

        // when
        HoldingStockStatistic statistic = HoldingStockStatistic.of(holdingStock, getStock());

        // then
        assertThat(statistic.getHoldingStockInfo().getCode()).isEqualTo(TEST_STOCK_CODE);
        assertThat(statistic.getHoldingStockInfo().getName()).isEqualTo(TEST_STOCK_NAME);
        assertThat(statistic.getHoldingStockInfo().getPrice()).isEqualTo(TEST_STOCK_PRICE);
        assertThat(statistic.getHoldingStockInfo().getSector()).isEqualTo(TEST_STOCK_SECTOR);
        assertThat(statistic.getTotalAsset()).isEqualTo(getBD(132_000));
        assertThat(statistic.getGain()).isEqualTo(getBD(70_400));
        assertThat(statistic.getGainRate()).isEqualTo(getBD(114.29));
        assertThat(statistic.getAvgPrice()).isEqualTo(getBD(30_800));
        assertThat(statistic.getCurrentPrice()).isEqualTo(getBD(66_000));
        assertThat(statistic.getQuantity()).isEqualTo(getBD(2));
    }

    @Test
    @DisplayName("3건의 매매이력으로 보유종목 통계를 만든다.")
    void createHoldingStockStatisticFromHoldingStock2() {
        // given
        TradeLog log1 = getTradeLog(getBD(33000), ONE);
        TradeLog log2 = getTradeLog(getBD(28600), ONE);
        TradeLog log3 = getTradeLog(getBD(77620), getBD(2));

        HoldingStock holdingStock = getHoldingStock(List.of(log1, log2, log3));

        // when
        HoldingStockStatistic statistic = HoldingStockStatistic.of(holdingStock, getStock());

        // then
        assertThat(statistic.getHoldingStockInfo().getCode()).isEqualTo(TEST_STOCK_CODE);
        assertThat(statistic.getHoldingStockInfo().getName()).isEqualTo(TEST_STOCK_NAME);
        assertThat(statistic.getHoldingStockInfo().getPrice()).isEqualTo(TEST_STOCK_PRICE);
        assertThat(statistic.getHoldingStockInfo().getSector()).isEqualTo(TEST_STOCK_SECTOR);
        assertThat(statistic.getTotalAsset()).isEqualTo(getBD(264_000));
        assertThat(statistic.getGain()).isEqualTo(getBD(47_160));
        assertThat(statistic.getGainRate()).isEqualTo(getBD(21.75));
        assertThat(statistic.getAvgPrice()).isEqualTo(getBD(54_210));
        assertThat(statistic.getCurrentPrice()).isEqualTo(getBD(66_000));
        assertThat(statistic.getQuantity()).isEqualTo(getBD(4));
    }

    private HoldingStock getHoldingStock(List<TradeLog> tradeLog) {
        return HoldingStock.builder()
                .stockCode(TEST_STOCK_CODE)
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

    private Stock getStock() {
        return Stock.builder()
                .code(TEST_STOCK_CODE)
                .name(TEST_STOCK_NAME)
                .price(TEST_STOCK_PRICE)
                .sector(TEST_STOCK_SECTOR)
                .build();
    }
}
