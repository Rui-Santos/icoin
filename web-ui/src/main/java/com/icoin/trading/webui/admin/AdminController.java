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

package com.icoin.trading.webui.admin;

import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.query.orderbook.OrderBookEntry;
import com.icoin.trading.tradeengine.query.orderbook.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.GenericCommandMessage;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.AddItemsToPortfolioCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.DepositCashCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * @author Jettro Coenradie
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private CommandBus commandBus;
    private PortfolioQueryRepository portfolioQueryRepository;
    private OrderBookQueryRepository orderBookQueryRepository;

    @RequestMapping(value = "/portfolio")
    public String show(Model model) {
        Iterable<PortfolioEntry> portfolios = portfolioQueryRepository.findAll();
        model.addAttribute("portfolios", portfolios);

        return "admin/portfolio/list";
    }

    @RequestMapping(value = "/portfolio/{identifier}")
    public String showPortfolio(@PathVariable("identifier") String portfolioIdentifier,
                                Model model) {
        PortfolioEntry portfolio = portfolioQueryRepository.findOne(portfolioIdentifier);
        model.addAttribute("portfolio", portfolio);

        Iterable<OrderBookEntry> orderBooks = orderBookQueryRepository.findAll();
        model.addAttribute("orderbooks", orderBooks);

        return "admin/portfolio/detail";
    }

    @RequestMapping(value = "/portfolio/{identifier}/money")
    public String addMoney(@PathVariable("identifier") String portfolioIdentifier,
                           @RequestParam("amount") BigDecimal amountOfMoney
    ) {
        DepositCashCommand command =
                new DepositCashCommand(new PortfolioId(portfolioIdentifier), amountOfMoney);
        commandBus.dispatch(new GenericCommandMessage<DepositCashCommand>(command));
        return "redirect:/admin/portfolio/{identifier}";
    }

    @RequestMapping(value = "/portfolio/{identifier}/item")
    public String addItem(@PathVariable("identifier") String portfolioIdentifier,
                          @RequestParam("orderbook") String orderBookIdentifier,
                          @RequestParam("amount") BigDecimal amount
    ) {
        AddItemsToPortfolioCommand command = new AddItemsToPortfolioCommand(new PortfolioId(
                portfolioIdentifier),
                new OrderBookId(
                        orderBookIdentifier),
                amount);
        commandBus.dispatch(new GenericCommandMessage<AddItemsToPortfolioCommand>(command));
        return "redirect:/admin/portfolio/{identifier}";
    }

    /* Setters */
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setCommandBus(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setOrderBookQueryRepository(OrderBookQueryRepository orderBookQueryRepository) {
        this.orderBookQueryRepository = orderBookQueryRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setPortfolioQueryRepository(PortfolioQueryRepository portfolioQueryRepository) {
        this.portfolioQueryRepository = portfolioQueryRepository;
    }
}
