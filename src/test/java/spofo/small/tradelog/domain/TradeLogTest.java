package spofo.small.tradelog.domain;

import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.holdingstock.domain.HoldingStock;
import spofo.stock.domain.Stock;
import spofo.tradelog.domain.TradeLog;
import spofo.tradelog.domain.TradeLogCreate;

public class TradeLogTest {

    @Test
    @DisplayName("TradeLogCreate 로 매매이력 도메인을 생성한다.")
    void TradeLogCreateToTradeLog() {
        // given
        HoldingStock holdingStock = HoldingStock.builder().build();

        TradeLogCreate create = TradeLogCreate.builder()
                .price(TEN)
                .build();

        Stock stock = Stock.builder().build();

        // when
        TradeLog tradeLog = TradeLog.of(create, holdingStock, stock);

        // then
        assertThat(tradeLog.getId()).isNull();
        assertThat(tradeLog.getPrice()).isEqualTo(TEN);
    }

}
