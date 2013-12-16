package com.icoin.trading.tradeengine.query.order;

import com.homhon.base.domain.model.ValueObjectSupport;
import com.icoin.trading.tradeengine.MoneyUtils;
import org.joda.money.BigMoney;
import org.springframework.data.annotation.Id;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-10
 * Time: PM11:13
 * To change this template use File | Settings | File Templates.
 */
public class PriceAggregate2 extends ValueObjectSupport<PriceAggregate2> {
    @Id
    private long price;
    private long amount;
    private long total;
    private String ccy;
    private String coinCcy;

    public PriceAggregate2(long price, long amount, String priceCcy, String coinCcy) {
        this.price = price;
        this.amount = amount;
        this.ccy = priceCcy;
        this.coinCcy = coinCcy;
        this.total = amount * price;
    }

    public BigMoney getPrice() {
        return MoneyUtils.convertToBigMoney(ccy, price);
    }

    public BigMoney getAmount() {
        return MoneyUtils.convertToBigMoney(coinCcy, amount);
    }

    public BigMoney getTotal() {
        return MoneyUtils.convertToBigMoney(ccy, total);
    }
}
