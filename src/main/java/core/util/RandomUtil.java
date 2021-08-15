package core.util;

import java.math.BigDecimal;

public final class RandomUtil {

    private RandomUtil() {
    }

    public static BigDecimal nextDouble(BigDecimal min, BigDecimal max) {
        BigDecimal range = max.subtract(min);
        return min.add(range.multiply(BigDecimal.valueOf(Math.random())));
    }
}
