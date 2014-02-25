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

package com.icoin.trading.webui.coins;

import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.coin.repositories.CoinQueryRepository;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.OrderEntry;
import com.icoin.trading.tradeengine.query.order.OrderType;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.order.repositories.OrderQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import com.icoin.trading.tradeengine.query.tradeexecuted.TradeExecutedEntry;
import com.icoin.trading.tradeengine.query.tradeexecuted.repositories.TradeExecutedQueryRepository;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import com.icoin.trading.webui.order.AbstractOrder;
import com.icoin.trading.webui.order.BuyOrder;
import com.icoin.trading.webui.order.SellOrder;
import com.icoin.trading.webui.util.SecurityUtil;
import org.axonframework.commandhandling.CommandBus;
import org.joda.money.CurrencyUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author Jettro Coenradie
 */
@Controller
@RequestMapping("/coin")
public class CoinController {
    private static Logger logger = LoggerFactory.getLogger(CoinController.class);
    private CoinQueryRepository coinRepository;
    private OrderBookQueryRepository orderBookRepository;
    private UserQueryRepository userRepository;
    private TradeExecutedQueryRepository tradeExecutedRepository;
    private PortfolioQueryRepository portfolioQueryRepository;
    private CommandBus commandBus;
    private OrderQueryRepository orderQueryRepository;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public CoinController(CoinQueryRepository coinRepository,
                          CommandBus commandBus,
                          UserQueryRepository userRepository,
                          OrderBookQueryRepository orderBookRepository,
                          OrderQueryRepository orderQueryRepository,
                          TradeExecutedQueryRepository tradeExecutedRepository,
                          PortfolioQueryRepository portfolioQueryRepository) {
        this.coinRepository = coinRepository;
        this.commandBus = commandBus;
        this.userRepository = userRepository;
        this.orderBookRepository = orderBookRepository;
        this.tradeExecutedRepository = tradeExecutedRepository;
        this.portfolioQueryRepository = portfolioQueryRepository;
        this.orderQueryRepository = orderQueryRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String get(Model model) {
        model.addAttribute("items", coinRepository.findAll());
        return "coin/list";
    }

    @RequestMapping(value = "/{coinId}", method = RequestMethod.GET)
    public String details(@PathVariable String coinId, Model model) {
        CoinEntry coin = coinRepository.findOne(coinId);
        final List<OrderBookEntry> bookEntryList = orderBookRepository.findByCoinIdentifier(coin.getPrimaryKey());
        OrderBookEntry bookEntry = bookEntryList.get(0);

        final List<OrderEntry> buyOrders =
                orderQueryRepository.findByOrderBookIdentifierAndTypeAndOrderStatus(
                        bookEntry.getPrimaryKey(),
                        OrderType.BUY,
                        OrderStatus.PENDING,
                        new PageRequest(0, 100));

        final List<OrderEntry> sellOrders =
                orderQueryRepository.findByOrderBookIdentifierAndTypeAndOrderStatus(
                        bookEntry.getPrimaryKey(),
                        OrderType.SELL,
                        OrderStatus.PENDING,
                        new PageRequest(0, 100));

        List<TradeExecutedEntry> executedTrades = tradeExecutedRepository.findByOrderBookIdentifier(bookEntry
                .getPrimaryKey(), new PageRequest(0,20, Sort.Direction.DESC, "tradeTime"));
        model.addAttribute("coin", coin);
        model.addAttribute("sellOrders", sellOrders);
        model.addAttribute("buyOrders", buyOrders);
        model.addAttribute("executedTrades", executedTrades);
        return "coin/details";
    }


    @RequestMapping(value = "/buy/{coinId}", method = RequestMethod.GET)
    public String buyForm(@PathVariable String coinId, Model model) {
        addPortfolioMoneyInfoToModel(model);

        BuyOrder order = new BuyOrder();
        prepareInitialOrder(coinId, order);
        model.addAttribute("order", order);
        return "coin/buy";
    }

    @RequestMapping(value = "/sell/{coinId}", method = RequestMethod.GET)
    public String sellForm(@PathVariable String coinId, Model model) {
        addPortfolioItemInfoToModel(coinId, model);

        SellOrder order = new SellOrder();
        prepareInitialOrder(coinId, order);
        model.addAttribute("order", order);
        return "coin/sell";
    }

//    @RequestMapping(value = "/sell/{coinId}", method = RequestMethod.POST)
//    public String sell(@ModelAttribute("order") @Valid SellOrder order, BindingResult bindingResult, Model model) {
//        if (!bindingResult.hasErrors()) {
//            OrderBookEntry bookEntry = obtainOrderBookForCoin(order.getCoinId());
//            PortfolioEntry portfolioEntry = obtainPortfolioForUser();
//
//            final BigDecimal tradeAmount = order.getTradeAmount();
//            final BigDecimal itemPrice = order.getItemPrice();
//
//            final Money price = Money.of(Constants.DEFAULT_CURRENCY_UNIT, itemPrice, RoundingMode.HALF_EVEN);
//            final Money btcAmount = Money.of(Constants.CURRENCY_UNIT_BTC, tradeAmount, RoundingMode.HALF_EVEN);
////            final Money totalMoney = price.convertedTo(price.getCurrencyUnit(), btcAmount.getAmount(), RoundingMode.HALF_EVEN);
//
//            if (portfolioEntry.obtainAmountOfAvailableItemFor(bookEntry.getPrimaryKey(), Constants.CURRENCY_UNIT_BTC).isLessThan(btcAmount)) {
//                bindingResult.rejectValue("tradeAmount",
//                        "error.order.sell.tomanyitems",
//                        "Not enough items available to create sell order.");
//                addPortfolioItemInfoToModel(order.getCoinId(), model);
//                return "coin/sell";
//            }
//
//            logger.info("placing a sell order with price {}, amount {}: {}.", price, btcAmount, order);
//
//            StartSellTransactionCommand command = new StartSellTransactionCommand(new TransactionId(),
//                    new OrderBookId(bookEntry.getPrimaryKey()),
//                    new PortfolioId(portfolioEntry.getIdentifier()),
//                    btcAmount.toBigMoney(),
//                    price.toBigMoney());
//
//            commandBus.dispatch(new GenericCommandMessage<StartSellTransactionCommand>(command));
//            logger.info("Sell order {} dispatched... ", order);
//
//            return "redirect:/coin/{coinId}";
//        }
//
//        addPortfolioItemInfoToModel(order.getCoinId(), model);
//        return "coin/sell";
//    }

//    @RequestMapping(value = "/buy/{coinId}", method = RequestMethod.POST)
//    public String buy(@ModelAttribute("order") @Valid BuyOrder order, BindingResult bindingResult, Model model) {
//        if (!bindingResult.hasErrors()) {
//
//            OrderBookEntry bookEntry = obtainOrderBookForCoin(order.getCoinId());
//            PortfolioEntry portfolioEntry = obtainPortfolioForUser();
//
//            final BigDecimal tradeAmount = order.getTradeAmount();
//            final BigDecimal itemPrice = order.getItemPrice();
//
//            final Money price = Money.of(Constants.DEFAULT_CURRENCY_UNIT, itemPrice, RoundingMode.HALF_EVEN);
//            final Money btcAmount = Money.of(Constants.CURRENCY_UNIT_BTC, tradeAmount, RoundingMode.HALF_EVEN);
//            final Money totalMoney = btcAmount.convertedTo(price.getCurrencyUnit(), btcAmount.getAmount(), RoundingMode.HALF_EVEN);
//
//
//            if (portfolioEntry.obtainMoneyToSpend().isLessThan(totalMoney)) {
//                bindingResult.rejectValue("tradeAmount",
//                        "error.order.buy.notenoughmoney",
//                        "Not enough cash to spend to buy the items for the price you want");
//                addPortfolioMoneyInfoToModel(portfolioEntry, model);
//                return "coin/buy";
//            }
//
//            logger.info("placing a buy order with price {}, amount {}, total money {}: {}.", price, btcAmount, totalMoney, order);
//            StartBuyTransactionCommand command = new StartBuyTransactionCommand(new TransactionId(),
//                    new OrderBookId(bookEntry.getPrimaryKey()),
//                    new PortfolioId(portfolioEntry.getIdentifier()),
//                    btcAmount.toBigMoney(),
//                    price.toBigMoney());
//            commandBus.dispatch(new GenericCommandMessage<StartBuyTransactionCommand>(command));
//            logger.info("Buy order {} dispatched... ", order);
//            return "redirect:/coin/{coinId}";
//        }
//
//        addPortfolioMoneyInfoToModel(model);
//        return "coin/buy";
//    }

    private void addPortfolioItemInfoToModel(String identifier, Model model) {
        PortfolioEntry portfolioEntry = obtainPortfolioForUser();
        OrderBookEntry orderBookEntry = obtainOrderBookForCoin(identifier);
        addPortfolioItemInfoToModel(portfolioEntry, orderBookEntry.getPrimaryKey(), orderBookEntry.getBaseCurrency(), model);
    }

    private void addPortfolioItemInfoToModel(PortfolioEntry entry, String orderBookIdentifier, CurrencyUnit currencyUnit, Model model) {
        model.addAttribute("itemsInPossession", entry.obtainAmountOfItemInPossessionFor(orderBookIdentifier, currencyUnit));
        model.addAttribute("itemsReserved", entry.obtainAmountOfReservedItemFor(orderBookIdentifier, currencyUnit));
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
     * At the moment we handle the first orderBook found for a coin.
     *
     * @param coinId Identifier for the coin to obtain the orderBook for
     * @return Found OrderBook for the coin belonging to the provided identifier
     */
    private OrderBookEntry obtainOrderBookForCoin(String coinId) {
        return orderBookRepository.findByCoinIdentifier(coinId).get(0);
    }

    /**
     * For now we work with only one portfolio per user. This might change in the future.
     *
     * @return The found portfolio for the logged in user.
     */
    private PortfolioEntry obtainPortfolioForUser() {
        UserEntry username = userRepository.findByUsername(SecurityUtil.obtainLoggedinUsername());
        return portfolioQueryRepository.findByUserIdentifier(username.getPrimaryKey());
    }

    private void prepareInitialOrder(String identifier, AbstractOrder order) {
        CoinEntry coin = coinRepository.findOne(identifier);
        order.setCoinId(identifier);
        order.setCoinName(coin.getName());
    }
}
