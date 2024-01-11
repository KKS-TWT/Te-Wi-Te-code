package spofo.small.tradelog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static spofo.global.component.utils.CommonUtils.getBD;
import static spofo.tradelog.domain.enums.TradeType.BUY;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.tradelog.domain.TradeLog;
import spofo.tradelog.domain.TradeLogStatistic;

public class TradeLogStatisticTest {

    @Test
    @DisplayName("1개의 매매이력으로 매매이력 통계를 만든다.")
    void createTradeLogStatistic() {
        // given
        TradeLog log = getTradeLog(getBD(33000), getBD(2));

        // when
        TradeLogStatistic statistic = TradeLogStatistic.of(log);

        // then
        assertThat(statistic.getType()).isEqualTo(log.getType());
        assertThat(statistic.getGain()).isEqualTo(getBD(-44000));
        assertThat(statistic.getTotalPrice()).isEqualTo(getBD(66000));
    }

    private TradeLog getTradeLog(BigDecimal price, BigDecimal quantity) {
        return TradeLog.builder()
                .type(BUY)
                .price(price)
                .quantity(quantity)
                .marketPrice(getBD(11000))
                .build();
    }
}
