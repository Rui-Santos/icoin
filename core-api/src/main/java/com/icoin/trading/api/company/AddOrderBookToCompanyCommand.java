/*
 * Copyright (c) 2012. Axon Framework
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

package com.icoin.trading.api.company;

import com.icoin.trading.api.orders.trades.OrderBookId;

/**
 * <p>Create the relation between a company and an OrderBook</p>
 *
 * @author Jettro Coenradie
 */
public class AddOrderBookToCompanyCommand {
    private CompanyId companyId;
    private OrderBookId orderBookId;

    public AddOrderBookToCompanyCommand(CompanyId companyId, OrderBookId orderBookId) {
        this.companyId = companyId;
        this.orderBookId = orderBookId;
    }

    public CompanyId getCompanyId() {
        return companyId;
    }

    public OrderBookId getOrderBookId() {
        return orderBookId;
    }
}
