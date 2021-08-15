package core.currency;

import core.exception.CurrencyValidationException;
import core.model.Currency;
import core.model.CurrencyRate;
import core.model.RateInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class  CurrenciesBoard {

    private static final Logger logger = LoggerFactory.getLogger(CurrenciesBoard.class);
    private final Set<Currency> currencies = new HashSet<>();

    private final int minRanksUpdateInterval;
    private final int maxRanksUpdateInterval;

    public CurrenciesBoard(int minRanksUpdateInterval, int maxRanksUpdateInterval) {
        if (minRanksUpdateInterval <= 0 || minRanksUpdateInterval >= maxRanksUpdateInterval)
            throw new IllegalArgumentException("Intervals cannot be less or equal 0 and max must be greater than min");

        this.minRanksUpdateInterval = minRanksUpdateInterval;
        this.maxRanksUpdateInterval = maxRanksUpdateInterval;

        CurrencyRateUpdateTask task = new CurrencyRateUpdateTask(currencies);
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(task::update, 1, 1, TimeUnit.SECONDS);
    }

    public synchronized void addCurrency(String code, Integer updateFrequencyInSeconds) {
        validateCurrency(code, updateFrequencyInSeconds);
        if (currencyExists(currencies, code))
            throw new CurrencyValidationException("Currency is already added: " + code);

        currencies.add(new Currency(code, updateFrequencyInSeconds));
        updateAllCurrencies();
        logger.info("Added currency {}, {}s", code, updateFrequencyInSeconds);
    }

    public synchronized void updateCurrencyRateUpdateFrequency(String code, Integer updateFrequencyInSeconds) {
        validateCurrency(code, updateFrequencyInSeconds);
        if (!currencyExists(currencies, code))
            throw new CurrencyValidationException("Currency not exists");

        Currency currencyToUpdate = findCurrencyByCode(currencies, code);
        currencyToUpdate.setUpdateFrequency(updateFrequencyInSeconds);
        logger.info("Updated currency {}, {}s", code, updateFrequencyInSeconds);
    }

    private void updateAllCurrencies() {
        Set<String> currenciesCodes = currencies.stream()
                .map(Currency::getCode)
                .collect(Collectors.toSet());

        currencies.forEach(c -> {
            Map<String, RateInfo> rates = c.getRates();
            currenciesCodes.forEach(cc -> {
                if (!rates.containsKey(cc) && !cc.equals(c.getCode()))
                    rates.put(cc, new RateInfo());
            });
        });
    }

    public synchronized void initCurrencyRates(String baseCode, String quoteCode, String initialRate,
                                               String maxPercentageRateDiff) {

        if (!currencyExists(currencies, baseCode) || !currencyExists(currencies, quoteCode))
            throw new CurrencyValidationException("Currency not exists: " + baseCode + ", " + quoteCode);
        BigDecimal bigInitialRate = new BigDecimal(initialRate).setScale(4, RoundingMode.HALF_UP);

        if (bigInitialRate.compareTo(BigDecimal.ZERO) <= 0)
            throw new CurrencyValidationException("Initial rate cannot be less or equal zero");

        BigDecimal bigMaxPercentageRateDiff = new BigDecimal(maxPercentageRateDiff);
        if (bigMaxPercentageRateDiff.compareTo(BigDecimal.ZERO) <= 0)
            throw new CurrencyValidationException(" Rate diff cannot be less or equal zero");

        BigDecimal invertedBigInitialRate = BigDecimal.ONE.divide(bigInitialRate, 4, RoundingMode.HALF_UP);

        initSingleCurrencyRate(baseCode, quoteCode, bigInitialRate, bigMaxPercentageRateDiff);
        initSingleCurrencyRate(quoteCode, baseCode, invertedBigInitialRate, bigMaxPercentageRateDiff);
    }

    private void initSingleCurrencyRate(String firstCode, String secondCode, BigDecimal initialRate,
                                        BigDecimal maxPercentageRateDiff) {

        Currency currencyToInit = findCurrencyByCode(currencies, firstCode);

        currencyToInit.getRates().computeIfPresent(secondCode, (code, rateInfo) -> {
            rateInfo.setRate(initialRate);
            rateInfo.setMaxPercentageRateDiff(maxPercentageRateDiff);
            return rateInfo;
        });
    }

    public synchronized List<CurrencyRate> readCurrencyRates(String baseCode) {
        if (!currencyExists(currencies, baseCode))
            throw new CurrencyValidationException("Currency not exists: " + baseCode);

        List<CurrencyRate> currenciesRates = new ArrayList<>();
        findCurrencyByCode(currencies, baseCode).getRates()
                .forEach((k, v) -> currenciesRates.add(new CurrencyRate(baseCode, k, String.valueOf(v.getRate()))));
        return currenciesRates;
    }

    public synchronized List<CurrencyRate> readAllCurrenciesRates() {
        List<CurrencyRate> currenciesRates = new ArrayList<>();
        currencies.forEach(baseCode -> baseCode.getRates().forEach((quoteCode, rate) ->
                currenciesRates.add(new CurrencyRate(baseCode.getCode(), quoteCode, String.valueOf(rate.getRate())))));

        return currenciesRates;
    }

    private void validateCurrency(String code, Integer updateFrequencyInSeconds) {
        if (code == null || code.trim().isEmpty())
            throw new CurrencyValidationException("Code is invalid");

        if (updateFrequencyInSeconds == null || updateFrequencyInSeconds < minRanksUpdateInterval
                || updateFrequencyInSeconds > maxRanksUpdateInterval)
            throw new CurrencyValidationException("Currency update rate is not in range. " +
                    minRanksUpdateInterval + "s - " + maxRanksUpdateInterval + "s");
    }

    public static Currency findCurrencyByCode(Set<Currency> currencies, String baseCode) {
        return currencies.stream().filter(c -> c.getCode().equals(baseCode))
                .findFirst().orElseThrow(() -> new CurrencyValidationException("Currency does not exists"));
    }

    public static boolean currencyExists(Set<Currency> currencies, String code) {
        return currencies.stream().anyMatch(c -> c.getCode().equals(code));
    }
}
