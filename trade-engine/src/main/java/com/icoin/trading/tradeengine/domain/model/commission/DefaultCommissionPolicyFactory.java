package com.icoin.trading.tradeengine.domain.model.commission;

import com.icoin.trading.tradeengine.domain.model.order.Order;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-9-2
 * Time: PM9:01
 * To change this template use File | Settings | File Templates.
 */
public class DefaultCommissionPolicyFactory implements CommissionPolicyFactory {
    @Override
    public CommissionPolicy createCommissionPolicy(Order order) {
        return new DefaultCommissionPolicy();
    }
}
