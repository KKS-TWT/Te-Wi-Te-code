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

class HoldingStockStatisticTest {

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
        assertThat(statistic).extracting(
                s -> s.getHoldingStockInfo().getCode(), s -> s.getHoldingStockInfo().getName(),
                s -> s.getHoldingStockInfo().getPrice(), s -> s.getHoldingStockInfo().getSector(),
                HoldingStockStatistic::getTotalAsset, HoldingStockStatistic::getGain,
                HoldingStockStatistic::getGainRate, HoldingStockStatistic::getAvgPrice,
                HoldingStockStatistic::getCurrentPrice, HoldingStockStatistic::getQuantity
        ).containsExactly(
                TEST_STOCK_CODE, TEST_STOCK_NAME,
                TEST_STOCK_PRICE, TEST_STOCK_SECTOR,
                getBD(132_000), getBD(70_400),
                getBD(114.29), getBD(30_800),
                getBD(66_000), getBD(2)
        );
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
        assertThat(statistic).extracting(
                s -> s.getHoldingStockInfo().getCode(), s -> s.getHoldingStockInfo().getName(),
                s -> s.getHoldingStockInfo().getPrice(), s -> s.getHoldingStockInfo().getSector(),
                HoldingStockStatistic::getTotalAsset, HoldingStockStatistic::getGain,
                HoldingStockStatistic::getGainRate, HoldingStockStatistic::getAvgPrice,
                HoldingStockStatistic::getCurrentPrice, HoldingStockStatistic::getQuantity
        ).containsExactly(
                TEST_STOCK_CODE, TEST_STOCK_NAME,
                TEST_STOCK_PRICE, TEST_STOCK_SECTOR,
                getBD(264_000), getBD(47_160),
                getBD(21.75), getBD(54_210),
                getBD(66_000), getBD(4)
        );
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
