package com.icoin.trading.tradeengine.domain;

import com.icoin.trading.tradeengine.domain.model.admin.TradingSystemStatus;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-7
 * Time: AM12:48
 * To change this template use File | Settings | File Templates.
 */
public interface TradingSystemService {
    TradingSystemStatus currentStatus();
}
