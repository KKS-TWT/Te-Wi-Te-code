package spofo.small.holdingstock.domain;

import static java.math.BigDecimal.ONE;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.holdingstock.domain.HoldingStock;
import spofo.holdingstock.domain.HoldingStockInfo;
import spofo.stock.domain.Stock;

class HoldingStockInfoTest {

    @Test
    @DisplayName("보유종목과 주식 정보를 가지고 주식정보를 담은 보유종목을 만든다.")
    void createHoldingStockInfo() {
        // given
        String stockCode = "101010";

        HoldingStock holdingStock = HoldingStock.builder()
                .id(1L)
                .stockCode(stockCode)
                .build();

        Stock stock = Stock.builder()
                .code(stockCode)
                .name("하이닉스")
                .price(ONE)
                .market("코스피")
                .sector("반도체")
                .imageUrl("이미지 경로")
                .build();

        // when
        HoldingStockInfo stockInfo = HoldingStockInfo.of(holdingStock, stock);

        // then
        assertThat(stockInfo).extracting(
                i -> i.getHoldingStock().getId(), i -> i.getHoldingStock().getStockCode(),
                HoldingStockInfo::getName, HoldingStockInfo::getPrice, HoldingStockInfo::getMarket,
                HoldingStockInfo::getSector, HoldingStockInfo::getImageUrl
        ).containsExactly(
                1L, "101010",
                "하이닉스", ONE, "코스피",
                "반도체", "이미지 경로"
        );
    }
}
