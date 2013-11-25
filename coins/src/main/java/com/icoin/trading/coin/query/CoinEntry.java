package com.icoin.trading.coin.query;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-11-24
 * Time: PM4:48
 * To change this template use File | Settings | File Templates.
 */
import org.springframework.data.annotation.Id;

/**
 * @author Jettro Coenradie
 */
public class CoinEntry {

    @Id
    private String identifier;
    private String name;
    private long coinInitialPrice;
    private long coinInitialAmount;
    private boolean tradeStarted;

    public long getCoinInitialAmount() {
        return coinInitialAmount;
    }

    public void setCoinInitialAmount(long coinInitialAmount) {
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

    public long getValue() {
        return coinInitialPrice;
    }

    public void setValue(long coinInitialPrice) {
        this.coinInitialPrice = coinInitialPrice;
    }
}