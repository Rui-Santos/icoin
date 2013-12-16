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

import com.homhon.base.domain.model.probability.Probability;
import com.icoin.trading.tradeengine.domain.model.order.AbstractOrder;

import java.math.BigDecimal;

import static com.homhon.util.Asserts.notNull;

/**
 * Sample Policy impl<br>
 *
 * @author Slawek
 */
public class DefaultCommissionPolicy implements CommissionPolicy {
    private Probability probability = Probability.from(BigDecimal.valueOf(0.001));

    @Override
    public Commission calculateCommission(AbstractOrder order) {
        notNull(order);
        BigDecimal ratio = BigDecimal.valueOf(0.001);
        String desc = "0.1% (D)";

//        Money effectiveCost = order.getItemRemaining();

//        final Probability commision = probability.combinedWith(Probability.from(order.getItemRemaining()));
//
//        if (effectiveCost == null || Money.lessOrEqualsZero(effectiveCost)) {
//            return Commission.ZERO_COMMISSION;
//        }
//        Money tax = effectiveCost.multiplyBy(ratio);
//        return new Commission(tax, desc);
        return null;
    }

}