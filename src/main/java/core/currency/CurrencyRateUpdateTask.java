package core.currency;

import core.model.Currency;
import core.model.RateInfo;
import core.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CurrencyRateUpdateTask {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyRateUpdateTask.class);
    private final Set<Currency> currencies;
    private final StringBuilder sb = new StringBuilder();

    public CurrencyRateUpdateTask(Set<Currency> currencies) {
        this.currencies = currencies;
    }

    public synchronized void update() {
        long now = System.currentTimeMillis() / 1000;

        Set<Currency> currenciesToUpdate = currencies.stream()
                .filter(c -> shouldUpdateCurrency(c, now))
                .collect(Collectors.toSet());
        updateRates(currenciesToUpdate, now);
    }

    private void updateRates(Set<Currency> currenciesToUpdate, long now) {
        Map<String, BigDecimal> temporaryRatesMap = new HashMap<>();
        for (Currency base : currenciesToUpdate) {
            for (Currency quote : currenciesToUpdate) {
                if (base.getCode().equals(quote.getCode())) continue;
                updateRate(base, quote, temporaryRatesMap, now);
            }
        }
    }

    private void updateRate(Currency base, Currency quote, Map<String, BigDecimal> tempMap, long now) {
        RateInfo rateInfo = base.getRates().get(quote.getCode());

        String currencyPair = combineRateKey(base.getCode(), quote.getCode());
        String invertedCurrencyPair = combineRateKey(quote.getCode(), base.getCode());

        if (!tempMap.containsKey(currencyPair) && !tempMap.containsKey(invertedCurrencyPair)) {
            if (rateInfo.getRate().equals(BigDecimal.ZERO)) return;

            BigDecimal calculatedRate = calculateRate(rateInfo);
            rateInfo.setRate(calculatedRate);
            tempMap.put(currencyPair, calculatedRate);
        } else {
            BigDecimal rate = tempMap.containsKey(currencyPair) ?
                    tempMap.get(currencyPair) : tempMap.get(invertedCurrencyPair);
            BigDecimal invertedRate = BigDecimal.ONE.divide(rate, 4, RoundingMode.HALF_UP);
            rateInfo.setRate(invertedRate);
        }
        base.setLastUpdateTime(now);
    }

    private BigDecimal calculateRate(RateInfo rateInfo) {
        BigDecimal maxPercentageRateDiff = rateInfo.getMaxPercentageRateDiff();

        if (rateInfo.getRate().equals(BigDecimal.ZERO) || maxPercentageRateDiff.equals(BigDecimal.ZERO))
            return BigDecimal.ZERO;

        BigDecimal percentageDiff = RandomUtil.nextDouble(maxPercentageRateDiff.negate(), maxPercentageRateDiff);
        BigDecimal currencyDiff = rateInfo.getRate().multiply(percentageDiff);
        return currencyDiff.add(rateInfo.getRate()).setScale(4, RoundingMode.HALF_UP);
    }

    private String combineRateKey(String baseCode, String quoteCode) {
        sb.setLength(0);
        return sb.append(baseCode).append(":").append(quoteCode).toString();
    }

    private boolean shouldUpdateCurrency(Currency currency, long now) {
        return (now - currency.getUpdateFrequency() >= currency.getLastUpdateTime());
    }
}
