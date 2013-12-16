package com.icoin.trading.webui.trade;

import com.homhon.util.Strings;
import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.application.command.transaction.command.StartBuyTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.StartSellTransactionCommand;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.coin.repositories.CoinQueryRepository;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.OrderEntry;
import com.icoin.trading.tradeengine.query.order.OrderType;
import com.icoin.trading.tradeengine.query.order.PriceAggregate;
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
import org.axonframework.commandhandling.GenericCommandMessage;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-9
 * Time: AM1:21
 * Trade controller.
 */
@Controller
@RequestMapping("/")
public class TradeController {
    public static final String DEFUALT_COIN = "BTC";
    private static Logger logger = LoggerFactory.getLogger(TradeController.class);
    private CoinQueryRepository coinRepository;
    private OrderBookQueryRepository orderBookRepository;
    private UserQueryRepository userRepository;
    private TradeExecutedQueryRepository tradeExecutedRepository;
    private PortfolioQueryRepository portfolioQueryRepository;
    private CommandBus commandBus;
    private OrderQueryRepository orderQueryRepository;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public TradeController(CoinQueryRepository coinRepository,
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

    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String get(Model model) {
        OrderBookEntry orderBookEntry = obtainOrderBookForCoin(DEFUALT_COIN);
        model.addAttribute("orderBook", orderBookEntry);

        SellOrder sellOrder = new SellOrder();
        prepareInitialOrder(DEFUALT_COIN, sellOrder, orderBookEntry, OrderType.SELL);
        model.addAttribute("sellOrder", sellOrder);

        BuyOrder buyOrder = new BuyOrder();
        prepareInitialOrder(DEFUALT_COIN, buyOrder, orderBookEntry, OrderType.BUY);
        model.addAttribute("buyOrder", buyOrder);

        CoinEntry coin = coinRepository.findOne(DEFUALT_COIN);
        final List<OrderBookEntry> bookEntryList = orderBookRepository.findByCoinIdentifier(coin.getPrimaryKey());
        OrderBookEntry bookEntry = bookEntryList.get(0);

        final String userId = SecurityUtil.obtainLoggedinUserIdentifierSafely();
        if (Strings.hasLength(userId)) {
            final PortfolioEntry portfolioEntry = obtainPortfolioForUser();
            sellOrder.setBalance(portfolioEntry
                    .obtainAmountOfAvailableItemsFor(DEFUALT_COIN, orderBookEntry.getBaseCurrency())
                    .getAmount());
            buyOrder.setBalance(portfolioEntry.getAmountOfMoney().getAmount());

            final List<OrderEntry> activeOrders = orderQueryRepository.findUserActiveOrders(portfolioEntry.getPrimaryKey(), bookEntry.getPrimaryKey());
            logger.info("queried active orders for user {} with order book {}: {}", portfolioEntry.getPrimaryKey(), bookEntry.getPrimaryKey(), activeOrders);
            model.addAttribute("activeOrders", activeOrders);
        }

        final List<PriceAggregate> buyOrders =
                orderQueryRepository.findOrderAggregatedPrice(
                        bookEntry.getPrimaryKey(),
                        OrderType.BUY);

        final List<PriceAggregate> sellOrders =
                orderQueryRepository.findOrderAggregatedPrice(
                        bookEntry.getPrimaryKey(),
                        OrderType.SELL);

        List<TradeExecutedEntry> executedTrades = tradeExecutedRepository.findByOrderBookIdentifier(bookEntry
                .getPrimaryKey());
        model.addAttribute("coin", coin);
        model.addAttribute("buyOrders", buyOrders);
        model.addAttribute("sellOrders", sellOrders);
        model.addAttribute("executedTrades", executedTrades);

        return "index";
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
                        OrderStatus.PENDING);

        final List<OrderEntry> sellOrders =
                orderQueryRepository.findByOrderBookIdentifierAndTypeAndOrderStatus(
                        bookEntry.getPrimaryKey(),
                        OrderType.SELL,
                        OrderStatus.PENDING);

        List<TradeExecutedEntry> executedTrades = tradeExecutedRepository.findByOrderBookIdentifier(bookEntry
                .getPrimaryKey());
        model.addAttribute("coin", coin);
        model.addAttribute("sellOrders", sellOrders);
        model.addAttribute("buyOrders", buyOrders);
        model.addAttribute("executedTrades", executedTrades);
        return "/index";
    }

    @RequestMapping(value = "/sell/{coinId}", method = RequestMethod.POST)
    public String sell(@ModelAttribute("sellOrder") @Valid SellOrder order, BindingResult bindingResult, Model model) {
        if (!bindingResult.hasErrors()) {
            OrderBookEntry bookEntry = obtainOrderBookForCoin(order.getCoinId());
            PortfolioEntry portfolioEntry = obtainPortfolioForUser();

            final BigDecimal tradeAmount = order.getTradeAmount();
            final BigDecimal itemPrice = order.getItemPrice();

            final Money price = Money.of(Constants.DEFAULT_CURRENCY_UNIT, itemPrice, RoundingMode.HALF_EVEN);
            final Money btcAmount = Money.of(Constants.CURRENCY_UNIT_BTC, tradeAmount, RoundingMode.HALF_EVEN);

            if (portfolioEntry.obtainAmountOfAvailableItemsFor(bookEntry.getPrimaryKey(), Constants.CURRENCY_UNIT_BTC).isLessThan(btcAmount)) {
                bindingResult.rejectValue("tradeAmount",
                        "error.order.sell.tomanyitems",
                        "Not enough items available to create sell order.");
                BuyOrder buyOrder = new BuyOrder();
                prepareInitialOrder(DEFUALT_COIN, buyOrder, bookEntry, OrderType.BUY);
                model.addAttribute("buyOrder", buyOrder);
//                addPortfolioItemInfoToModel(order.getCoinId(), model);
                return "/index";
            }

            logger.info("placing a sell order with price {}, amount {}: {}.", price, btcAmount, order);

            StartSellTransactionCommand command = new StartSellTransactionCommand(new TransactionId(),
                    new OrderBookId(bookEntry.getPrimaryKey()),
                    new PortfolioId(portfolioEntry.getIdentifier()),
                    btcAmount.toBigMoney(),
                    price.toBigMoney());

            commandBus.dispatch(new GenericCommandMessage<StartSellTransactionCommand>(command));
            logger.info("Sell order {} dispatched... ", order);

            return "redirect:/index";
        }

//        addPortfolioItemInfoToModel(order.getCoinId(), model);
        return "/index";
    }

    @RequestMapping(value = "/buy/{coinId}", method = RequestMethod.POST)
    public String buy(@ModelAttribute("buyOrder") @Valid BuyOrder order, BindingResult bindingResult, Model model) {
        if (!bindingResult.hasErrors()) {

            OrderBookEntry bookEntry = obtainOrderBookForCoin(order.getCoinId());
            PortfolioEntry portfolioEntry = obtainPortfolioForUser();

            final BigDecimal tradeAmount = order.getTradeAmount();
            final BigDecimal itemPrice = order.getItemPrice();

            final Money price = Money.of(Constants.DEFAULT_CURRENCY_UNIT, itemPrice, RoundingMode.HALF_EVEN);
            final Money btcAmount = Money.of(Constants.CURRENCY_UNIT_BTC, tradeAmount, RoundingMode.HALF_EVEN);
            final Money totalMoney = btcAmount.convertedTo(price.getCurrencyUnit(), btcAmount.getAmount(), RoundingMode.HALF_EVEN);


            if (portfolioEntry.obtainMoneyToSpend().isLessThan(totalMoney)) {
                bindingResult.rejectValue("tradeAmount",
                        "error.order.buy.notenoughmoney",
                        "Not enough cash to spend to buy the items for the price you want");
                SellOrder sellOrder = new SellOrder();
                prepareInitialOrder(DEFUALT_COIN, sellOrder, bookEntry, OrderType.SELL);
                model.addAttribute("sellOrder", sellOrder);
//                addPortfolioMoneyInfoToModel(portfolioEntry, model);
                return "/index";
            }

            logger.info("placing a buy order with price {}, amount {}, total money {}: {}.", price, btcAmount, totalMoney, order);
            StartBuyTransactionCommand command = new StartBuyTransactionCommand(new TransactionId(),
                    new OrderBookId(bookEntry.getPrimaryKey()),
                    new PortfolioId(portfolioEntry.getIdentifier()),
                    btcAmount.toBigMoney(),
                    price.toBigMoney());

            commandBus.dispatch(new GenericCommandMessage<StartBuyTransactionCommand>(command));
            logger.info("Buy order {} dispatched... ", order);
            return "redirect:/index";
        }

        addPortfolioMoneyInfoToModel(model);
        return "/index";
    }

    private void addPortfolioItemInfoToModel(String identifier, Model model) {
        PortfolioEntry portfolioEntry = obtainPortfolioForUser();
        OrderBookEntry orderBookEntry = obtainOrderBookForCoin(identifier);
        addPortfolioItemInfoToModel(portfolioEntry, orderBookEntry.getPrimaryKey(), orderBookEntry.getBaseCurrency(), model);
    }

    private void addPortfolioItemInfoToModel(PortfolioEntry entry, String orderBookIdentifier, CurrencyUnit currencyUnit, Model model) {
        model.addAttribute("itemsInPossession", entry.obtainAmountOfItemsInPossessionFor(orderBookIdentifier, currencyUnit));
        model.addAttribute("itemsReserved", entry.obtainAmountOfReservedItemsFor(orderBookIdentifier, currencyUnit));
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
        final List<OrderBookEntry> byCoinIdentifier = orderBookRepository.findByCoinIdentifier(coinId);
        if (logger.isDebugEnabled()) {
            logger.debug("Find by coin Id {} : {}", coinId, byCoinIdentifier);
        }
        return byCoinIdentifier.get(0);
    }

    /**
     * For now we work with only one portfolio per user. This might change in the future.
     *
     * @return The found portfolio for the logged in user.
     */
    private PortfolioEntry obtainPortfolioForUser() {
        final String user = SecurityUtil.obtainLoggedinUsername();
        UserEntry username = userRepository.findByUsername(user);
        return portfolioQueryRepository.findByUserIdentifier(username.getPrimaryKey());
    }

    private void prepareInitialOrder(String identifier, AbstractOrder order, OrderBookEntry orderBook, OrderType type) {
        CoinEntry coin = coinRepository.findOne(identifier);
        order.setCoinId(identifier);
        order.setCoinName(coin.getName());

        BigDecimal amount = BigDecimal.ZERO;

        switch (type) {
            case BUY:
                if(orderBook != null && orderBook.getLowestSellPrice() !=null){
                    amount =  orderBook.getLowestSellPrice().getAmount();
                }

                order.setSuggestedPrice(amount);
                break;
            default:
                if(orderBook != null && orderBook.getHighestBuyPrice() !=null){
                    amount =  orderBook.getHighestBuyPrice().getAmount();
                }
                order.setSuggestedPrice(amount);
        }
    }
}