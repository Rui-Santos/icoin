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

package com.icoin.trading.tradeengine.query.order.repositories;

import com.icoin.trading.tradeengine.query.order.OrderEntry;
import com.icoin.trading.tradeengine.query.order.OrderType;
import com.icoin.trading.tradeengine.query.order.PriceAggregate;

import java.util.Date;
import java.util.List;

/**
 * @author Jettro Coenradie
 */
public interface OrderQueryRepositoryCustom {

    List<PriceAggregate> findOrderAggregatedPrice(String orderBookIdentifier, OrderType type, Date toDate, int limit);

    List<OrderEntry> findAllUserOrders(String userId, int start, int limit);

//    List<OrderEntry> findActiveHintSellOrders(String orderBookId, int start, int limit);

//    List<OrderEntry> findActiveHintBuyOrders(String orderBookId, int start, int limit);

    List<OrderEntry> findUserActiveOrders(String userId, String orderBookId);
}
