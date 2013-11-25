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

package com.icoin.trading.webui.companies;

import com.icoin.trading.api.orders.trades.TransactionId;
import com.icoin.trading.tradeengine.application.command.transaction.command.StartSellTransactionCommand;
import com.icoin.trading.query.company.CompanyEntry;
import com.icoin.trading.query.orderbook.repositories.OrderBookQueryRepository;
import com.icoin.trading.query.portfolio.PortfolioEntry;
import com.icoin.trading.query.portfolio.repositories.PortfolioQueryRepository;
import com.icoin.trading.query.tradeexecuted.TradeExecutedEntry;
import com.icoin.trading.query.tradeexecuted.repositories.TradeExecutedQueryRepository;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import com.icoin.trading.webui.order.BuyOrder;
import com.icoin.trading.webui.order.SellOrder;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.GenericCommandMessage;
import com.icoin.trading.tradeengine.application.command.transaction.command.StartBuyTransactionCommand;
import com.icoin.trading.query.company.repositories.CompanyQueryRepository;
import com.icoin.trading.query.orderbook.OrderBookEntry;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.api.orders.trades.OrderBookId;
import com.icoin.trading.api.orders.trades.PortfolioId;
import com.icoin.trading.webui.order.AbstractOrder;
import com.icoin.trading.webui.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Jettro Coenradie
 */
@Controller
@RequestMapping("/company")
public class CompanyController {

    private CompanyQueryRepository companyRepository;
    private OrderBookQueryRepository orderBookRepository;
    private UserQueryRepository userRepository;
    private TradeExecutedQueryRepository tradeExecutedRepository;
    private PortfolioQueryRepository portfolioQueryRepository;
    private CommandBus commandBus;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public CompanyController(CompanyQueryRepository companyRepository,
                             CommandBus commandBus,
                             UserQueryRepository userRepository,
                             OrderBookQueryRepository orderBookRepository,
                             TradeExecutedQueryRepository tradeExecutedRepository,
                             PortfolioQueryRepository portfolioQueryRepository) {
        this.companyRepository = companyRepository;
        this.commandBus = commandBus;
        this.userRepository = userRepository;
        this.orderBookRepository = orderBookRepository;
        this.tradeExecutedRepository = tradeExecutedRepository;
        this.portfolioQueryRepository = portfolioQueryRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String get(Model model) {
        model.addAttribute("items", companyRepository.findAll());
        return "company/list";
    }

    @RequestMapping(value = "/{companyId}", method = RequestMethod.GET)
    public String details(@PathVariable String companyId, Model model) {
        CompanyEntry company = companyRepository.findOne(companyId);
        OrderBookEntry bookEntry = orderBookRepository.findByCompanyIdentifier(company.getIdentifier()).get(0);
        List<TradeExecutedEntry> executedTrades = tradeExecutedRepository.findByOrderBookIdentifier(bookEntry
                .getIdentifier());
        model.addAttribute("company", company);
        model.addAttribute("sellOrders", bookEntry.sellOrders());
        model.addAttribute("buyOrders", bookEntry.buyOrders());
        model.addAttribute("executedTrades", executedTrades);
        return "company/details";
    }


    @RequestMapping(value = "/buy/{companyId}", method = RequestMethod.GET)
    public String buyForm(@PathVariable String companyId, Model model) {
        addPortfolioMoneyInfoToModel(model);

        BuyOrder order = new BuyOrder();
        prepareInitialOrder(companyId, order);
        model.addAttribute("order", order);
        return "company/buy";
    }

    @RequestMapping(value = "/sell/{companyId}", method = RequestMethod.GET)
    public String sellForm(@PathVariable String companyId, Model model) {
        addPortfolioItemInfoToModel(companyId, model);

        SellOrder order = new SellOrder();
        prepareInitialOrder(companyId, order);
        model.addAttribute("order", order);
        return "company/sell";
    }

    @RequestMapping(value = "/sell/{companyId}", method = RequestMethod.POST)
    public String sell(@ModelAttribute("order") @Valid SellOrder order, BindingResult bindingResult, Model model) {
        if (!bindingResult.hasErrors()) {
            OrderBookEntry bookEntry = obtainOrderBookForCompany(order.getCompanyId());
            PortfolioEntry portfolioEntry = obtainPortfolioForUser();

            if (portfolioEntry.obtainAmountOfAvailableItemsFor(bookEntry.getIdentifier()) < order.getTradeCount()) {
                bindingResult.rejectValue("tradeCount",
                        "error.order.sell.tomanyitems",
                        "Not enough items available to create sell order.");
                addPortfolioItemInfoToModel(order.getCompanyId(), model);
                return "company/sell";
            }

            StartSellTransactionCommand command = new StartSellTransactionCommand(new TransactionId(),
                    new OrderBookId(bookEntry.getIdentifier()),
                    new PortfolioId(portfolioEntry.getIdentifier()),
                    order.getTradeCount(),
                    order.getItemPrice());

            commandBus.dispatch(new GenericCommandMessage<StartSellTransactionCommand>(command));

            return "redirect:/company/{companyId}";
        }

        addPortfolioItemInfoToModel(order.getCompanyId(), model);
        return "company/sell";
    }

    @RequestMapping(value = "/buy/{companyId}", method = RequestMethod.POST)
    public String buy(@ModelAttribute("order") @Valid BuyOrder order, BindingResult bindingResult, Model model) {
        if (!bindingResult.hasErrors()) {

            OrderBookEntry bookEntry = obtainOrderBookForCompany(order.getCompanyId());
            PortfolioEntry portfolioEntry = obtainPortfolioForUser();

            if (portfolioEntry.obtainMoneyToSpend() < order.getTradeCount() * order.getItemPrice()) {
                bindingResult.rejectValue("tradeCount",
                        "error.order.buy.notenoughmoney",
                        "Not enough cash to spend to buy the items for the price you want");
                addPortfolioMoneyInfoToModel(portfolioEntry, model);
                return "company/buy";
            }

            StartBuyTransactionCommand command = new StartBuyTransactionCommand(new TransactionId(),
                    new OrderBookId(bookEntry.getIdentifier()),
                    new PortfolioId(portfolioEntry.getIdentifier()),
                    order.getTradeCount(),
                    order.getItemPrice());
            commandBus.dispatch(new GenericCommandMessage<StartBuyTransactionCommand>(command));
            return "redirect:/company/{companyId}";
        }

        addPortfolioMoneyInfoToModel(model);
        return "company/buy";
    }

    private void addPortfolioItemInfoToModel(String identifier, Model model) {
        PortfolioEntry portfolioEntry = obtainPortfolioForUser();
        OrderBookEntry orderBookEntry = obtainOrderBookForCompany(identifier);
        addPortfolioItemInfoToModel(portfolioEntry, orderBookEntry.getIdentifier(), model);
    }

    private void addPortfolioItemInfoToModel(PortfolioEntry entry, String orderBookIdentifier, Model model) {
        model.addAttribute("itemsInPossession", entry.obtainAmountOfItemsInPossessionFor(orderBookIdentifier));
        model.addAttribute("itemsReserved", entry.obtainAmountOfReservedItemsFor(orderBookIdentifier));
    }

    private void addPortfolioMoneyInfoToModel(Model model) {
        PortfolioEntry portfolioEntry = obtainPortfolioForUser();
        addPortfolioMoneyInfoToModel(portfolioEntry, model);
    }

    private void addPortfolioMoneyInfoToModel(PortfolioEntry portfolioEntry, Model model) {
        model.addAttribute("moneyInPossession", portfolioEntry.getAmountOfMoney());
        model.addAttribute("moneyReserved", portfolioEntry.getReservedAmountOfMoney());
    }

    /**
     * At the moment we handle the first orderBook found for a company.
     *
     * @param companyId Identifier for the company to obtain the orderBook for
     * @return Found OrderBook for the company belonging to the provided identifier
     */
    private OrderBookEntry obtainOrderBookForCompany(String companyId) {
        return orderBookRepository.findByCompanyIdentifier(companyId).get(0);
    }

    /**
     * For now we work with only one portfolio per user. This might change in the future.
     *
     * @return The found portfolio for the logged in user.
     */
    private PortfolioEntry obtainPortfolioForUser() {
        UserEntry username = userRepository.findByUsername(SecurityUtil.obtainLoggedinUsername());
        return portfolioQueryRepository.findByUserIdentifier(username.getIdentifier());
    }

    private void prepareInitialOrder(String identifier, AbstractOrder order) {
        CompanyEntry company = companyRepository.findOne(identifier);
        order.setCompanyId(identifier);
        order.setCompanyName(company.getName());
    }
}
