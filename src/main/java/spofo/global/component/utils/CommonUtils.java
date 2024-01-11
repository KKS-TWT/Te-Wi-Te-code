package spofo.global.component.utils;

import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class CommonUtils {

    public static boolean isZero(BigDecimal value) {
        return value.compareTo(ZERO) == 0;
    }

    public static BigDecimal toPercent(BigDecimal value) {
        return value.multiply(BigDecimal.valueOf(100));
    }

    public static int getGlobalScale() {
        return 2;
    }

    public static BigDecimal getBD(long value) {
        return BigDecimal.valueOf(value);
    }

    public static BigDecimal getBD(double value) {
        return BigDecimal.valueOf(value);
    }

    public static BigDecimal round(BigDecimal value) {
        return value.setScale(getGlobalScale());
    }

    public static BigDecimal format(BigDecimal value) {
        if (value == null) {
            return ZERO;
        }

        if (value.remainder(ONE).compareTo(ZERO) == 0) {
            return getBD(parseLong(new DecimalFormat("####").format(value)));
        }

        return getBD(parseDouble(new DecimalFormat("####.##").format(value)));
    }
}
