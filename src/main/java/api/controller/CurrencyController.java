package api.controller;

import core.model.CurrencyRate;
import core.currency.CurrenciesBoard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rate")
public class CurrencyController {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyController.class);

    private final CurrenciesBoard currenciesBoard;

    public CurrencyController(CurrenciesBoard currenciesBoard) {
        this.currenciesBoard = currenciesBoard;
    }

    //tego endpointa UI nie uzywa ale dałem zeby pokazac ze jest mozliwosc filtrowania po kodzie waluty
    @GetMapping
    public ResponseEntity<List<CurrencyRate>> getCurrencyRates(@RequestParam(name = "code") String code) {
        return ResponseEntity.ok(currenciesBoard.readCurrencyRates(code));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<CurrencyRate>> getAllCurrenciesRates() {
        return ResponseEntity.ok(currenciesBoard.readAllCurrenciesRates());
    }
}
