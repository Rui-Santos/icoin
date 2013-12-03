package com.icoin.trading.tradeengine.domain.model.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-3
 * Time: AM12:31
 * To change this template use File | Settings | File Templates.
 */
public class  OrderComparator<T extends AbstractOrder> implements Comparator<T>, Serializable {

    public int compare(T o1, T o2) {
        // copied from Java 7 Long.compareTo to support java 6
        BigDecimal x = o1.getItemPrice();
        BigDecimal y = o2.getItemPrice();
        int result = x.compareTo(y);

        return result == 0 ? o1.getPlaceDate().compareTo(o2.getPlaceDate()) : result;
    }
}