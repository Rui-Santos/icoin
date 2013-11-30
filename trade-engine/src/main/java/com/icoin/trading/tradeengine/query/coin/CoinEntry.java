package com.icoin.trading.tradeengine.query.coin;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-11-24
 * Time: PM4:48
 * To change this template use File | Settings | File Templates.
 */

import com.homhon.mongo.domainsupport.modelsupport.entity.AuditAwareEntitySupport;

import java.math.BigDecimal;

/**
 * @author Jettro Coenradie
 */
public class CoinEntry extends AuditAwareEntitySupport<CoinEntry, String, Long> {

    private String name;
    private BigDecimal coinPrice;
    private BigDecimal coinAmount;
    private boolean tradeStarted;

    public BigDecimal getCoinAmount() {
        return coinAmount;
    }

    public void setCoinAmount(BigDecimal coinAmount) {
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

    public BigDecimal getCoinPrice() {
        return coinPrice;
    }

    public void setCoinPrice(BigDecimal coinPrice) {
        this.coinPrice = coinPrice;
    }
}