package core.model;

public class CurrencyRate {
    private String baseCode;
    private String quoteCode;
    private String rate;

    public CurrencyRate(String baseCode, String quoteCode, String rate) {
        this.baseCode = baseCode;
        this.quoteCode = quoteCode;
        this.rate = rate;
    }

    public String getBaseCode() {
        return baseCode;
    }

    public void setBaseCode(String baseCode) {
        this.baseCode = baseCode;
    }

    public String getQuoteCode() {
        return quoteCode;
    }

    public void setQuoteCode(String quoteCode) {
        this.quoteCode = quoteCode;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
