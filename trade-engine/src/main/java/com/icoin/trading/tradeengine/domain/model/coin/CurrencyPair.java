package com.icoin.trading.tradeengine.domain.model.coin;


import com.homhon.base.domain.model.ValueObjectSupport;
import org.joda.money.CurrencyUnit;
import org.springframework.data.annotation.PersistenceConstructor;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-11-23
 * Time: PM10:57
 * To change this template use File | Settings | File Templates.
 */
public class CurrencyPair extends ValueObjectSupport<CurrencyPair> {
    public static final String CCY_DEFAULT = "CNY";
    public static final String CCY_USD = "USD";
    // Provide some standard major symbols
    public static final CurrencyPair EUR_USD = new CurrencyPair("EUR", "USD");
    public static final CurrencyPair GBP_USD = new CurrencyPair("GBP", "USD");
    public static final CurrencyPair USD_JPY = new CurrencyPair("USD", "JPY");
    public static final CurrencyPair USD_CHF = new CurrencyPair("USD", "CHF");
    public static final CurrencyPair USD_AUD = new CurrencyPair("USD", "AUD");
    public static final CurrencyPair USD_CAD = new CurrencyPair("USD", "CAD");

    // Provide some courtesy BTC major symbols
    public static final CurrencyPair BTC_USD = new CurrencyPair("BTC", "USD");
    public static final CurrencyPair BTC_GBP = new CurrencyPair("BTC", "GBP");
    public static final CurrencyPair BTC_EUR = new CurrencyPair("BTC", "EUR");
    public static final CurrencyPair BTC_JPY = new CurrencyPair("BTC", "JPY");
    public static final CurrencyPair BTC_CHF = new CurrencyPair("BTC", "CHF");
    public static final CurrencyPair BTC_AUD = new CurrencyPair("BTC", "AUD");
    public static final CurrencyPair BTC_CAD = new CurrencyPair("BTC", "CAD");
    public static final CurrencyPair BTC_CNY = new CurrencyPair("BTC", "CNY");
    public static final CurrencyPair BTC_DKK = new CurrencyPair("BTC", "DKK");
    public static final CurrencyPair BTC_HKD = new CurrencyPair("BTC", "HKD");
    public static final CurrencyPair BTC_NZD = new CurrencyPair("BTC", "NZD");
    public static final CurrencyPair BTC_PLN = new CurrencyPair("BTC", "PLN");
    public static final CurrencyPair BTC_RUB = new CurrencyPair("BTC", "RUB");
    public static final CurrencyPair BTC_SEK = new CurrencyPair("BTC", "SEK");
    public static final CurrencyPair BTC_SGD = new CurrencyPair("BTC", "SGD");
    public static final CurrencyPair BTC_NOK = new CurrencyPair("BTC", "NOK");
    public static final CurrencyPair BTC_THB = new CurrencyPair("BTC", "THB");

    public static final CurrencyPair BTC_RUR = new CurrencyPair("BTC", "RUR");
    public static final CurrencyPair LTC_BTC = new CurrencyPair("LTC", "BTC");
    public static final CurrencyPair LTC_USD = new CurrencyPair("LTC", "USD");
    public static final CurrencyPair LTC_RUR = new CurrencyPair("LTC", "RUR");
    public static final CurrencyPair LTC_EUR = new CurrencyPair("LTC", "EUR");
    public static final CurrencyPair LTC_CNY = new CurrencyPair("LTC", "CNY");
    public static final CurrencyPair NMC_BTC = new CurrencyPair("NMC", "BTC");
    public static final CurrencyPair NMC_USD = new CurrencyPair("NMC", "USD");
    public static final CurrencyPair USD_RUR = new CurrencyPair("USD", "RUR");

    public static final CurrencyPair NVC_BTC = new CurrencyPair("NVC", "BTC");
    public static final CurrencyPair NVC_USD = new CurrencyPair("NVC", "USD");
    public static final CurrencyPair TRC_BTC = new CurrencyPair("TRC", "BTC");
    public static final CurrencyPair PPC_BTC = new CurrencyPair("PPC", "BTC");
    public static final CurrencyPair PPC_USD = new CurrencyPair("PPC", "USD");
    public static final CurrencyPair PPC_CNY = new CurrencyPair("PPC", "CNY");
    public static final CurrencyPair FTC_BTC = new CurrencyPair("FTC", "BTC");
    public static final CurrencyPair XPM_BTC = new CurrencyPair("XPM", "BTC");
    public static final CurrencyPair XPM_CNY = new CurrencyPair("XPM", "CNY");

    public static final CurrencyPair BTC_ZAR = new CurrencyPair("BTC", "ZAR");
    public static final CurrencyPair BTC_BRL = new CurrencyPair("BTC", "BRL");
    public static final CurrencyPair BTC_CZK = new CurrencyPair("BTC", "CZK");
    public static final CurrencyPair BTC_ILS = new CurrencyPair("BTC", "ILS");

    private final String baseCurrency;
    private final String counterCurrency;

    /**
     * <p>
     * Reduced constructor using the global reserve currency symbol (USD) as the default counter
     * </p>
     *
     * @param baseCurrency The base symbol (single unit)
     */
    public CurrencyPair(String baseCurrency) {

        this(baseCurrency, CCY_DEFAULT);
    }

    /**
     * <p>
     * Full constructor
     * </p>
     *
     * @param baseCurrency    The base symbol (single unit)
     * @param counterCurrency The counter symbol (multiple units)
     */

    @PersistenceConstructor
    public CurrencyPair(String baseCurrency, String counterCurrency) {

        this.baseCurrency = baseCurrency;
        this.counterCurrency = counterCurrency;
    }

    @Override
    public String toString() {

        return baseCurrency + "/" + counterCurrency;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public CurrencyUnit getBaseCurrencyUnit(){
        return CurrencyUnit.of(baseCurrency);
    }

    public String getCounterCurrency() {
        return counterCurrency;
    }

    public CurrencyUnit getCounterCurrencyUnit(){
        return CurrencyUnit.of(counterCurrency);
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((baseCurrency == null) ? 0 : baseCurrency.hashCode());
        result = prime * result + ((counterCurrency == null) ? 0 : counterCurrency.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CurrencyPair other = (CurrencyPair) obj;
        if (baseCurrency == null) {
            if (other.baseCurrency != null) {
                return false;
            }
        } else if (!baseCurrency.equals(other.baseCurrency)) {
            return false;
        }
        if (counterCurrency == null) {
            if (other.counterCurrency != null) {
                return false;
            }
        } else if (!counterCurrency.equals(other.counterCurrency)) {
            return false;
        }
        return true;
    }

}
