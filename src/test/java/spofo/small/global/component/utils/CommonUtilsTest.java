package spofo.small.global.component.utils;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static spofo.global.component.utils.CommonUtils.getBD;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spofo.global.component.utils.CommonUtils;

public class CommonUtilsTest {

    @Test
    @DisplayName("소수점 아래 2번째 자리를 넘으면 2번째 자리까지 반올림한다.")
    void roundingTest() {
        // given
        BigDecimal value = getBD(3.146);

        // when
        BigDecimal formatted = CommonUtils.format(value);

        // then
        assertThat(formatted).isEqualTo(getBD(3.15));
    }

    @Test
    @DisplayName("소수점 아래 0만 존재하면 정수로 포맷을 변경한다.")
    void formatTest() {
        // given
        BigDecimal value = getBD(3.0000000);

        // when
        BigDecimal formatted = CommonUtils.format(value);

        // then
        assertThat(formatted).isEqualTo(getBD(3));
    }

    @Test
    @DisplayName("전달받는 값이 null이면 0을 반환한다.")
    void formatTestWithNull() {
        // given
        BigDecimal value = null;

        // when
        BigDecimal formatted = CommonUtils.format(value);

        // then
        assertThat(formatted).isEqualTo(ZERO);
    }

    @Test
    @DisplayName("음수인 경우도 포맷을 잡아준다.")
    void formatTestWithNegative() {
        // given
        BigDecimal value = getBD(-1241.12525);

        // when
        BigDecimal formatted = CommonUtils.format(value);

        // then
        assertThat(formatted).isEqualTo(getBD(-1241.13));
    }
}
