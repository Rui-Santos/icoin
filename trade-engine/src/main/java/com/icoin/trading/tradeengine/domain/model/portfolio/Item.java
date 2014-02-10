package com.icoin.trading.tradeengine.domain.model.portfolio;

import com.homhon.base.domain.model.ValueObjectSupport;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import org.joda.money.BigMoney;

import java.math.RoundingMode;

import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-25
 * Time: AM11:38
 * To change this template use File | Settings | File Templates.
 */
public class Item extends ValueObjectSupport<Item>{
    private CoinId coinId;
    private BigMoney totalAmount;
    private BigMoney reservedAmount;
//    private BigMoney reservedMoney;
//    private BigMoney reservedCommission;

    public CoinId getCoinId() {
        return coinId;
    }

    public void setCoinId(CoinId coinId) {
        this.coinId = coinId;
    }

    public BigMoney getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigMoney totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigMoney getReservedAmount() {
        return reservedAmount;
    }

    public BigMoney getAvailableAmount() {
        return totalAmount.toMoney(RoundingMode.HALF_EVEN).toBigMoney();
    }

    public void setReservedAmount(BigMoney reservedAmount) {
        this.reservedAmount = reservedAmount;
    }

//    public BigMoney getReservedMoney() {
//        return reservedMoney;
//    }
//
//    public void setReservedMoney(BigMoney reservedMoney) {
//        this.reservedMoney = reservedMoney;
//    }
//
//    public BigMoney getReservedCommission() {
//        return reservedCommission;
//    }
//
//    public void setReservedCommission(BigMoney reservedCommission) {
//        this.reservedCommission = reservedCommission;
//    }

    public Item add(BigMoney amountOfItemAdded) {
        notNull(amountOfItemAdded);
        isTrue(amountOfItemAdded.isPositiveOrZero());
        totalAmount = totalAmount.plus(amountOfItemAdded);
        return this;
    }

    public Item subtract(BigMoney amountOfItemSubtracted) {
        notNull(amountOfItemSubtracted);
        isTrue(totalAmount.compareTo(amountOfItemSubtracted) >= 0);

        totalAmount = totalAmount.minus(amountOfItemSubtracted);
        return this;
    }

    public Item reserve(BigMoney amountOfItemReserved) {
        notNull(amountOfItemReserved);
        isTrue(totalAmount.compareTo(amountOfItemReserved) >= 0);

        totalAmount = totalAmount.minus(amountOfItemReserved);
        reservedAmount = reservedAmount.plus(amountOfItemReserved);
        return this;
    }

    public Item confirmReserved(BigMoney amountOfItemReserved) {
        notNull(amountOfItemReserved);
        isTrue(reservedAmount.compareTo(amountOfItemReserved) >= 0);

        reservedAmount = reservedAmount.minus(amountOfItemReserved);
        return this;
    }

    public Item cancelReserved(BigMoney amountOfItemReserved) {
        notNull(amountOfItemReserved);
        isTrue(reservedAmount.compareTo(amountOfItemReserved) >= 0);

        reservedAmount = reservedAmount.minus(amountOfItemReserved);
        totalAmount = totalAmount.plus(amountOfItemReserved);
        return this;
    }
}
