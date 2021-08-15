package core.model;

import java.math.BigDecimal;

public class RateInfo {

    private BigDecimal maxPercentageRateDiff;
    private BigDecimal rate;

    public RateInfo() {
        this.rate = BigDecimal.ZERO;
    }

    public BigDecimal getMaxPercentageRateDiff() {
        return maxPercentageRateDiff;
    }

    public void setMaxPercentageRateDiff(BigDecimal maxPercentageRateDiff) {
        this.maxPercentageRateDiff = maxPercentageRateDiff;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
