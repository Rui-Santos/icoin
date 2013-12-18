package com.icoin.trading.tradeengine.domain.model.commission;

import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-15
 * Time: AM10:15
 * To change this template use File | Settings | File Templates.
 */
public class DefaultCommissionPolicyFactoryTest {
    @Test
    public void testCreateCommissionPolicy() throws Exception {
        final DefaultCommissionPolicyFactory factory = new DefaultCommissionPolicyFactory();

        final SellOrder sellOrder = new SellOrder();
        final CommissionPolicy sellPolicy = factory.createCommissionPolicy(sellOrder);

        final BuyOrder buyOrder = new BuyOrder();
        final CommissionPolicy buyPolicy = factory.createCommissionPolicy(buyOrder);

        assertThat(sellPolicy, instanceOf(FixedRateCommissionPolicy.class));
        assertThat(buyPolicy, instanceOf(FixedRateCommissionPolicy.class));
    }
}
