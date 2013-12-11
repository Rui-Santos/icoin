package com.icoin.trading.tradeengine.query.order;

import com.homhon.base.domain.model.ValueObjectSupport;
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
public class PriceAggregate extends ValueObjectSupport<PriceAggregate>{
    @Id
    private BigDecimal price;
    private BigDecimal amount;
    private BigDecimal total;

    public PriceAggregate(BigDecimal price, BigDecimal amount) {
        this.price = nullSafe(price, BigDecimal.ZERO);
        this.amount = nullSafe(amount, BigDecimal.ZERO);
        this.total = price.multiply(amount);
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
