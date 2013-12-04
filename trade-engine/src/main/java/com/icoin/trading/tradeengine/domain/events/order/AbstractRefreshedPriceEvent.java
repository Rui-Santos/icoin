package com.icoin.trading.tradeengine.domain.events.order;

import com.homhon.base.domain.ValueObject;

import java.com.homhon.base.domain.event.EventSupport;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-4
 * Time: AM9:11
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractRefreshedPriceEvent<T extends AbstractRefreshedPriceEvent> extends EventSupport<T> {
   private BigDecimal price;


}
