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

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.DepositCashCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.AddAmountToPortfolioCommand;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.coin.repositories.CoinQueryRepository;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static Logger logger = LoggerFactory.getLogger(AdminController.class);

    private CommandBus commandBus;
    private PortfolioQueryRepository portfolioQueryRepository;
    private OrderBookQueryRepository orderBookQueryRepository;
    private CoinQueryRepository coinQueryRepository;

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

        Iterable<CoinEntry> coins = coinQueryRepository.findAll();
        model.addAttribute("coins", coins);

        return "admin/portfolio/detail";
    }

    @RequestMapping(value = "/portfolio/{identifier}/money")
    public String addMoney(@PathVariable("identifier") String portfolioIdentifier,
                           @RequestParam("amount") BigDecimal amountOfMoney) {
        DepositCashCommand command =
                new DepositCashCommand(new PortfolioId(portfolioIdentifier), BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, amountOfMoney));
        commandBus.dispatch(new GenericCommandMessage<DepositCashCommand>(command));
        return "redirect:/admin/portfolio/{identifier}";
    }

    @RequestMapping(value = "/portfolio/{identifier}/item")
    public String addItem(@PathVariable("identifier") String portfolioIdentifier,
                          @RequestParam("coin") String coin,
                          @RequestParam("amount") BigDecimal amount) {

        final CoinEntry coinEntry = coinQueryRepository.findOne(coin);

        if (coinEntry == null) {
            logger.warn("cannot add amount {} to user for coin, coin {} not found.", amount, coin);
            return "redirect:/admin/portfolio/{identifier}";
        }

        AddAmountToPortfolioCommand command = new AddAmountToPortfolioCommand(
                new PortfolioId(portfolioIdentifier),
                new CoinId(coinEntry.getPrimaryKey()),
                BigMoney.of(CurrencyUnit.of(coinEntry.getPrimaryKey()), amount));
        commandBus.dispatch(new GenericCommandMessage<AddAmountToPortfolioCommand>(command));
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

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setCoinQueryRepository(CoinQueryRepository coinQueryRepository) {
        this.coinQueryRepository = coinQueryRepository;
    }
}
