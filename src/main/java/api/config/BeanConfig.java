package api.config;

import core.currency.CurrenciesBoard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {


    @Bean
    public CurrenciesBoard currenciesBoard() {
        return initCurrenciesBoard(new CurrenciesBoard(10, 60));
    }

    private CurrenciesBoard initCurrenciesBoard(CurrenciesBoard currenciesBoard) {
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
        return currenciesBoard;
    }
}
