package com.icoin.trading.tradeengine.query.coin;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-11-24
 * Time: PM4:48
 * To change this template use File | Settings | File Templates.
 */

import com.homhon.mongo.domainsupport.modelsupport.entity.AuditAwareEntitySupport;
import org.joda.money.BigMoney;

/**
 * @author Jettro Coenradie
 */
public class CoinEntry extends AuditAwareEntitySupport<CoinEntry, String, Long> {

    private String name;
    private BigMoney coinPrice;
    private BigMoney coinAmount;
    private boolean tradeStarted;

    public BigMoney getCoinAmount() {
        return coinAmount;
    }

    public void setCoinAmount(BigMoney coinAmount) {
        this.coinAmount = coinAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTradeStarted() {
        return tradeStarted;
    }

    public void setTradeStarted(boolean tradeStarted) {
        this.tradeStarted = tradeStarted;
    }

    public BigMoney getCoinPrice() {
        return coinPrice;
    }

    public void setCoinPrice(BigMoney coinPrice) {
        this.coinPrice = coinPrice;
    }
}