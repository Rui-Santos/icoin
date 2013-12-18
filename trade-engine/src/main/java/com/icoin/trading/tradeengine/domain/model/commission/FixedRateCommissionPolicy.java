/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.icoin.trading.tradeengine.domain.model.commission;

import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import org.joda.money.BigMoney;

import java.math.BigDecimal;

import static com.homhon.util.Asserts.notNull;

/**
 * Sample Policy impl<br>
 *
 * @author Slawek
 */
public class FixedRateCommissionPolicy implements CommissionPolicy {
    private final BigDecimal fixedRate;

    public FixedRateCommissionPolicy() {
        this(BigDecimal.valueOf(0.005));
    }

    public FixedRateCommissionPolicy(BigDecimal fixedRate) {
        this.fixedRate = fixedRate;
    }

    @Override
    public Commission calculateSellCommission(SellOrder order) {
        notNull(order);
        notNull(order.getTradeAmount());

        return calcCommission(order.getTradeAmount());
    }

    private Commission calcCommission(BigMoney tradeAmount) {
        String desc = "Commission Rate is " + fixedRate.toString();

        BigMoney money = tradeAmount.multipliedBy(fixedRate);

        return new Commission(money, desc);
    }

    @Override
    public Commission calculateRemainingSellCommission(SellOrder order) {
        notNull(order);
        notNull(order.getItemRemaining());

        return calcCommission(order.getItemRemaining());
    }

    @Override
    public Commission calculateRemainingBuyCommission(BuyOrder order) {
        notNull(order);
        notNull(order.getItemRemaining());
        notNull(order.getItemPrice());

        BigMoney tradeAmount =
                order.getItemRemaining()
                        .convertedTo(order.getCounterCurrency(),
                                order.getItemPrice().getAmount());

        return calcCommission(tradeAmount);
    }

    @Override
    public Commission calculateBuyCommission(BuyOrder order) {
        notNull(order);
        notNull(order.getTradeAmount());
        notNull(order.getItemPrice());

        BigMoney tradeAmount =
                order.getTradeAmount()
                .convertedTo(order.getCounterCurrency(),
                        order.getItemPrice().getAmount());
        return calcCommission(tradeAmount);
    }
}