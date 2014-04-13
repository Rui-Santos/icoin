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

package com.icoin.trading.tradeengine.domain.events.portfolio.cash;


import com.homhon.base.domain.event.EventSupport;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * @author Jettro Coenradie
 */
public class CashDepositedEvent extends EventSupport<CashDepositedEvent> {
    private PortfolioId portfolioId;
    private BigMoney moneyAdded;
    private Date time;

    public CashDepositedEvent(PortfolioId portfolioId, BigMoney moneyAdded, Date time) {
        this.portfolioId = portfolioId;
        this.moneyAdded = moneyAdded;
        this.time = time;
    }

    public PortfolioId getPortfolioIdentifier() {
        return portfolioId;
    }

    public BigMoney getMoneyAdded() {
        return moneyAdded;
    }

    public PortfolioId getPortfolioId() {
        return portfolioId;
    }

    public Date getTime() {
        return time;
    }
}
