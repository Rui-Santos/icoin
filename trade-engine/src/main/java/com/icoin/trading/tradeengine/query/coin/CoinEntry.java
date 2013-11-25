package com.icoin.trading.tradeengine.query.coin;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-11-24
 * Time: PM4:48
 * To change this template use File | Settings | File Templates.
 */
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

/**
 * @author Jettro Coenradie
 */
public class CoinEntry {

    @Id
    private String identifier;
    private String name;
    private BigDecimal coinInitialPrice;
    private BigDecimal coinInitialAmount;
    private boolean tradeStarted;

    public BigDecimal getCoinInitialAmount() {
        return coinInitialAmount;
    }

    public void setCoinInitialAmount(BigDecimal coinInitialAmount) {
        this.coinInitialAmount = coinInitialAmount;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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

    public BigDecimal getCoinInitialPrice() {
        return coinInitialPrice;
    }

    public void setCoinInitialPrice(BigDecimal coinInitialPrice) {
        this.coinInitialPrice = coinInitialPrice;
    }
}