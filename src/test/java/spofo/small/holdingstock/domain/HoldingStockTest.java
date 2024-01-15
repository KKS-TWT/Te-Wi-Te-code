package spofo.small.holdingstock.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.holdingstock.domain.HoldingStock;
import spofo.holdingstock.domain.HoldingStockCreate;
import spofo.portfolio.domain.Portfolio;

class HoldingStockTest {

    @Test
    @DisplayName("HoldingStockCreate로 보유종목 도메인을 생성한다.")
    void holdingStockCreateToHoldingStock() {
        // given
        String stockCode = "101010";

        Portfolio portfolio = Portfolio.builder().build();
        HoldingStockCreate create = HoldingStockCreate.builder()
                .stockCode(stockCode)
                .build();

        // when
        HoldingStock holdingStock = HoldingStock.of(create, portfolio);

        // then
        assertThat(holdingStock).extracting(HoldingStock::getId, HoldingStock::getStockCode,
                        HoldingStock::getPortfolio)
                .containsExactly(null, stockCode, portfolio);
    }
}
