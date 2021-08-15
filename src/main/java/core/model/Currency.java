package core.model;

import java.util.HashMap;
import java.util.Map;

public class Currency {

    private final String code;
    private long updateFrequency;
    private long lastUpdateTime;
    private Map<String, RateInfo> rates = new HashMap<>();

    public Currency(String code, long updateFrequency) {
        this.code = code.toUpperCase();
        this.updateFrequency = updateFrequency;
    }

    public String getCode() {
        return code;
    }

    public long getUpdateFrequency() {
        return updateFrequency;
    }

    public void setUpdateFrequency(long updateFrequency) {
        this.updateFrequency = updateFrequency;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Map<String, RateInfo> getRates() {
        return rates;
    }

    public void setRates(Map<String, RateInfo> rates) {
        this.rates = rates;
    }
}
