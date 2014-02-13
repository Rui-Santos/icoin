package com.icoin.trading.tradeengine.query.order;

import com.homhon.base.domain.model.ValueObjectSupport;
import com.icoin.trading.tradeengine.MoneyUtils;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-10
 * Time: PM11:13
 * To change this template use File | Settings | File Templates.
 */
public class PriceAggregate extends ValueObjectSupport<PriceAggregate> {

    //    { "price" : 1034567 , "priceCurrency" : "CNY" , "amountCurrency" : "BTC" , "sumUpAmountPerPrice" : 102765449987770}
//    @Id
    private Long price;
    private Long sumUpAmountPerPrice;
    private String priceCurrency;
    private String amountCurrency;
    private BigMoney total;

    public PriceAggregate(Long price, String priceCurrency, String amountCurrency, Long sumUpAmountPerPrice) {
        this.price = price;
        this.sumUpAmountPerPrice = sumUpAmountPerPrice;
        this.priceCurrency = priceCurrency;
        this.amountCurrency = amountCurrency;
        double d = ((double) price) / MoneyUtils.getMultiplier(priceCurrency);

        this.total = MoneyUtils.convertToBigMoney(amountCurrency, sumUpAmountPerPrice)
                .convertedTo(CurrencyUnit.of(priceCurrency), BigDecimal.valueOf(d));
    }

    public BigMoney getPrice() {
        return MoneyUtils.convertToBigMoney(priceCurrency, price);
    }

    public BigMoney getSumUpAmountPerPrice() {
        return MoneyUtils.convertToBigMoney(amountCurrency, sumUpAmountPerPrice);
    }

    public Money getTotal() {
        return total.toMoney(RoundingMode.HALF_EVEN);
    }
}