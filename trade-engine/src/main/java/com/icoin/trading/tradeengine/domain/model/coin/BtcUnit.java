package com.icoin.trading.tradeengine.domain.model.coin;


import com.homhon.base.domain.ValueObject;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/11/13
 * Time: 4:20 PM
 * To change this template use File | Settings | File Templates.
 */
public enum BtcUnit implements ValueObject<BtcUnit> {
    BTC {
        @Override
        public BigDecimal getBtcPerUnit() {
            return BigDecimal.ONE;
        }
    },

    // 1 kilobitcoin = 1,000 BTC
    KBTC {
        @Override
        public BigDecimal getBtcPerUnit() {
            return BigDecimal.valueOf(1000);
        }
    },

    // 1 hectobitcoin = 100 BTC
    HBTC {
        @Override
        public BigDecimal getBtcPerUnit() {
            return BigDecimal.valueOf(100);
        }
    },

    // 1 decabitcoin = 10 BTC
    DABTC {
        @Override
        public BigDecimal getBtcPerUnit() {
            return BigDecimal.valueOf(10);
        }
    },

    //10 DBTC (deci btc) = 1 BTC
    DBTC {
        @Override
        public BigDecimal getBtcPerUnit() {
            return BigDecimal.valueOf(0.1);
        }
    },

    //100 CBTC (centi btc)= 1 BTC
    CBTC {
        @Override
        public BigDecimal getBtcPerUnit() {
            return BigDecimal.valueOf(0.01);
        }
    },

    //1,000 MBTC (milli) = 1 BTC
    MBTC {
        @Override
        public BigDecimal getBtcPerUnit() {
            return BigDecimal.valueOf(0.001);
        }
    },

    //1,000,000 UBTC (micro) = 1 BTC
    UBTC {
        @Override
        public BigDecimal getBtcPerUnit() {
            return BigDecimal.valueOf(0.000001);
        }
    },

    //100,00000 SATOSHI = 1 BTC
    SATOSHI {
        @Override
        public BigDecimal getBtcPerUnit() {
            return BigDecimal.valueOf(0.00000001);
        }
    };

    public abstract BigDecimal getBtcPerUnit();

    @Override
    public boolean sameValueAs(BtcUnit other) {
        return this == other;
    }

    @Override
    public BtcUnit copy() {
        return this;
    }
}