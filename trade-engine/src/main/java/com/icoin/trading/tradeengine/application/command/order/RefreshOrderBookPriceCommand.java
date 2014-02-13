/*
 * Copyright (c) 2010-2012. Axon Framework
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

package com.icoin.trading.tradeengine.application.command.order;

import com.homhon.base.command.CommandSupport;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;

/**
 * <p>Abstract parent class for all commands that are order related.</p>
 *
 * @author Allard Buijze
 */
public class RefreshOrderBookPriceCommand extends CommandSupport<RefreshOrderBookPriceCommand> {

    @TargetAggregateIdentifier
    private OrderBookId orderBookId;

    public RefreshOrderBookPriceCommand(OrderBookId orderBookId) {
        this.orderBookId = orderBookId;
    }

    public OrderBookId getOrderBookId() {
        return orderBookId;
    }
}
