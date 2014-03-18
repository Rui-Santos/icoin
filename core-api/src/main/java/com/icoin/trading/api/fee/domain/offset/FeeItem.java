package com.icoin.trading.api.fee.domain.offset;

import com.homhon.base.domain.model.ValueObjectSupport;
import org.joda.money.BigMoney;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM7:27
 * To change this template use File | Settings | File Templates.
 */
public class FeeItem extends ValueObjectSupport<FeeItem> {
    private String identifier;
    private FeeItemType type;
    private BigMoney amount;

    public FeeItem(String identifier, FeeItemType type, BigMoney amount) {
        notNull(identifier);
        notNull(type);
        notNull(amount);
        this.identifier = identifier;
        this.type = type;
        this.amount = amount;
    }

    public String getIdentifier() {
        return identifier;
    }

    public FeeItemType getType() {
        return type;
    }

    public BigMoney getAmount() {
        return amount;
    }
}
