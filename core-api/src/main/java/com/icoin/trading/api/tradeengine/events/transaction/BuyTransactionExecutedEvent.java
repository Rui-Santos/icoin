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

package com.icoin.trading.api.tradeengine.events.transaction;


import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * @author Jettro Coenradie
 */
public class BuyTransactionExecutedEvent extends AbstractTransactionExecutedEvent<BuyTransactionExecutedEvent> {

    public BuyTransactionExecutedEvent(TransactionId transactionIdentifier,
                                       CoinId coinId,
                                       BigMoney amountOfItems,
                                       BigMoney itemPrice,
                                       BigMoney executedMoney,
                                       BigMoney commission,
                                       Date time) {
        super(transactionIdentifier, coinId, amountOfItems, itemPrice, executedMoney, commission, time);
    }
}
