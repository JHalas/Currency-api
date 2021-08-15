package core;

import core.model.CurrencyRate;
import core.currency.CurrenciesBoard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        CurrenciesBoard currenciesBoard = new CurrenciesBoard(10, 60);

        currenciesBoard.addCurrency("PLN", 10);
        currenciesBoard.addCurrency("EUR", 10);
        currenciesBoard.addCurrency("USD", 12);
        currenciesBoard.addCurrency("CHF", 10);

        currenciesBoard.initCurrencyRates("EUR", "PLN", "4.56", "0.05");
        currenciesBoard.initCurrencyRates("USD", "PLN", "3.91", "0.05");
        currenciesBoard.initCurrencyRates("USD", "EUR", "0.85", "0.03");
        currenciesBoard.initCurrencyRates("CHF", "PLN", "4.25", "0.05");
        currenciesBoard.initCurrencyRates("CHF", "USD", "1.08", "0.03");
        currenciesBoard.initCurrencyRates("CHF", "EUR", "0.92", "0.03");

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            printCurrencyRates(currenciesBoard, "PLN");
            printCurrencyRates(currenciesBoard, "EUR");
            printCurrencyRates(currenciesBoard, "USD");
            printCurrencyRates(currenciesBoard, "CHF");
            logger.info("-----------------------");
            logger.info("-----------------------");
        }, 0, 15, TimeUnit.SECONDS);
    }

    private static void printCurrencyRates(CurrenciesBoard currenciesBoard, String code) {
        List<CurrencyRate> currencyRates = currenciesBoard.readCurrencyRates(code);
        currencyRates.forEach(k -> logger.info("{}/{} = {}", code, k.getQuoteCode(), k.getRate()));
    }
}
