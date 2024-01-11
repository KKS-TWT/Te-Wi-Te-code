package spofo.small.holdingstock.domain;

import static java.math.BigDecimal.ONE;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.holdingstock.domain.HoldingStock;
import spofo.holdingstock.domain.HoldingStockInfo;
import spofo.stock.domain.Stock;

public class HoldingStockInfoTest {

    @Test
    @DisplayName("보유종목과 주식 정보를 가지고 주식정보를 담은 보유종목을 만든다.")
    void createHoldingStockInfo() {
        // given
        Long id = 1L;
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
        assertThat(stockInfo.getHoldingStock().getId()).isEqualTo(holdingStock.getId());
        assertThat(stockInfo.getHoldingStock().getStockCode())
                .isEqualTo(holdingStock.getStockCode());
        assertThat(stockInfo.getName()).isEqualTo(stock.getName());
        assertThat(stockInfo.getPrice()).isEqualTo(stock.getPrice());
        assertThat(stockInfo.getMarket()).isEqualTo(stock.getMarket());
        assertThat(stockInfo.getSector()).isEqualTo(stock.getSector());
        assertThat(stockInfo.getImageUrl()).isEqualTo(stock.getImageUrl());
    }
}
