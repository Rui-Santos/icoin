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

package com.icoin.trading.api.tradeengine.command.portfolio;


import com.homhon.base.command.CommandSupport;
import com.icoin.trading.api.tradeengine.events.portfolio.PortfolioId;
import com.icoin.trading.api.users.event.UserId;

import java.util.Date;

/**
 * @author Jettro Coenradie
 */
public class CreatePortfolioCommand extends CommandSupport<CreatePortfolioCommand> {

    private PortfolioId portfolioId;
    private UserId userId;
    private Date createdTime;

    public CreatePortfolioCommand(PortfolioId portfolioId, UserId userId, Date createdTime) {
        this.portfolioId = portfolioId;
        this.userId = userId;
        this.createdTime = createdTime;
    }

    public UserId getUserId() {
        return userId;
    }

    public PortfolioId getPortfolioId() {
        return portfolioId;
    }

    public Date getCreatedTime() {
        return createdTime;
    }
}
