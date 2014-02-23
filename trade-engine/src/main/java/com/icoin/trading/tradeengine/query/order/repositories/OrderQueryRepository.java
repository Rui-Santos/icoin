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

import com.homhon.base.domain.repository.GenericCrudRepository;
import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;
import com.icoin.trading.tradeengine.query.order.OrderEntry;
import com.icoin.trading.tradeengine.query.order.OrderType;
import com.icoin.trading.tradeengine.query.order.PriceAggregate;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

/**
 * @author Jettro Coenradie
 */
public interface OrderQueryRepository extends
        PagingAndSortingRepository<OrderEntry, String>,
        OrderQueryRepositoryCustom,
        GenericCrudRepository<OrderEntry, String> {

    List<OrderEntry> findByOrderBookIdentifier(String orderBookIdentifier);

    List<OrderEntry> findByOrderBookIdentifierAndOrderStatus(String orderBookIdentifier,
                                                             OrderStatus orderStatus);

    List<OrderEntry> findByOrderBookIdentifierAndType(String orderBookIdentifier,
                                                      OrderType type);

    List<OrderEntry> findByOrderBookIdentifierAndTypeAndOrderStatus(String orderBookIdentifier,
                                                                    OrderType type,
                                                                    OrderStatus orderStatus);

    @Query(value = "{ 'userId' : ?0 , " +
            "'orderBookIdentifier' : ?1 , " +
            "'orderStatus' : 'PENDING' }, " +
            "Sort: { 'placeDate' : -1 }")
    List<OrderEntry> findUserActiveOrders(String userId, String orderBookId);

    List<OrderEntry> findActiveHintSellOrders(String orderBookId, int start, int limit);

    List<OrderEntry> findActiveHintBuyOrders(String orderBookId, int start, int limit);

    List<OrderEntry> findAllUserOrders(String userId, int start, int limit);

    @Override
    List<PriceAggregate> findOrderAggregatedPrice(String orderBookIdentifier, OrderType type, Date toDate, int limit);
}
