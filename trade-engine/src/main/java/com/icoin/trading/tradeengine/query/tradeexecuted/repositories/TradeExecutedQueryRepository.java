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

package com.icoin.trading.tradeengine.query.tradeexecuted.repositories;

import com.homhon.base.domain.repository.GenericCrudRepository;
import com.icoin.trading.tradeengine.query.tradeexecuted.OpenHighLowCloseVolume;
import com.icoin.trading.tradeengine.query.tradeexecuted.TradeExecutedEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

/**
 * @author Jettro Coenradie
 */
public interface TradeExecutedQueryRepository extends
        PagingAndSortingRepository<TradeExecutedEntry, String>,
        GenericCrudRepository<TradeExecutedEntry, String>,
        TradeExecutedQueryRepositoryCustom {

    List<TradeExecutedEntry> findByOrderBookIdentifier(String orderBookIdentifier, Pageable pageable);

    @Override
    List<OpenHighLowCloseVolume> ohlc(String orderBookIdentifier, Date startD, Date end, Pageable pageable);
}
