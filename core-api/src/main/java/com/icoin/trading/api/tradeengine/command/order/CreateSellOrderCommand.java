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

package com.icoin.trading.api.tradeengine.command.order;


import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.OrderId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * <p>Create a new Sell Order using the amount of items to sell for the provided price.</p>
 *
 * @author Allard Buijze
 */
public class CreateSellOrderCommand extends CreateOrderCommand<CreateSellOrderCommand> {

    public CreateSellOrderCommand(OrderId orderId,
                                  PortfolioId portfolioId,
                                  OrderBookId orderBookId,
                                  TransactionId transactionId,
                                  BigMoney tradeCount,
                                  BigMoney itemPrice,
                                  BigMoney totalCommission,
                                  Date placeDate) {
        super(orderId, portfolioId, orderBookId, transactionId, tradeCount, itemPrice, totalCommission, placeDate);
    }
}
