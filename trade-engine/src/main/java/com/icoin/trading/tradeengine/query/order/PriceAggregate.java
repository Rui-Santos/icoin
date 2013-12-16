package com.icoin.trading.tradeengine.query.order;

import com.homhon.base.domain.model.ValueObjectSupport;
import com.icoin.trading.tradeengine.MoneyUtils;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

import static com.homhon.util.Objects.nullSafe;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-10
 * Time: PM11:13
 * To change this template use File | Settings | File Templates.
 */
public class PriceAggregate extends ValueObjectSupport<PriceAggregate> {
    @Id
    private BigMoney price;
    private BigMoney amount;
    private BigMoney total;

    public PriceAggregate(BigMoney price, BigMoney amount) {
        this.price = price;
        this.amount = amount;
        this.total = price.multipliedBy(amount.getAmount());
    }

    public BigMoney getPrice() {
        return price;
    }

    public BigMoney getAmount() {
        return amount;
    }

    public BigMoney getTotal() {
        return total;
    }
}