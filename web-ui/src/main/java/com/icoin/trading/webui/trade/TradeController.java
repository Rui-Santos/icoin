package com.icoin.trading.webui.trade;

import com.homhon.util.Strings;
import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.OrderEntry;
import com.icoin.trading.tradeengine.query.order.OrderType;
import com.icoin.trading.tradeengine.query.order.PriceAggregate;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.tradeexecuted.TradeExecutedEntry;
import com.icoin.trading.webui.order.BuyOrder;
import com.icoin.trading.webui.order.SellOrder;
import com.icoin.trading.webui.security.UserServiceFacade;
import com.icoin.trading.webui.trade.facade.TradeServiceFacade;
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

import static com.homhon.mongo.TimeUtils.currentTime;

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

    private TradeServiceFacade tradeServiceFacade;

    private UserServiceFacade userServiceFacade;

    @Autowired
    public void setTradeServiceFacade(TradeServiceFacade tradeServiceFacade) {
        this.tradeServiceFacade = tradeServiceFacade;
    }

    @Autowired
    public void setUserServiceFacade(UserServiceFacade userServiceFacade) {
        this.userServiceFacade = userServiceFacade;
    }

    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String get(Model model) {
        OrderBookEntry orderBookEntry = tradeServiceFacade.loadOrderBookByCurrencyPair(DEFUALT_COIN);
        model.addAttribute("orderBook", orderBookEntry);

        SellOrder sellOrder = tradeServiceFacade.prepareSellOrder(DEFUALT_COIN, orderBookEntry);
        model.addAttribute("sellOrder", sellOrder);

        BuyOrder buyOrder = tradeServiceFacade.prepareBuyOrder(DEFUALT_COIN, orderBookEntry);
        model.addAttribute("buyOrder", buyOrder);

        CoinEntry coin = tradeServiceFacade.loadCoin(DEFUALT_COIN);
        OrderBookEntry bookEntry = tradeServiceFacade.loadOrderBookByCurrencyPair(coin.getPrimaryKey());

        final String userId = SecurityUtil.obtainLoggedinUserIdentifierSafely();
        if (Strings.hasLength(userId)) {
            final PortfolioEntry portfolioEntry = userServiceFacade.obtainPortfolioForUser();
            sellOrder.setBalance(portfolioEntry
                    .obtainAmountOfAvailableItemsFor(DEFUALT_COIN, orderBookEntry.getBaseCurrency())
                    .getAmount());
            buyOrder.setBalance(portfolioEntry.getAmountOfMoney().getAmount());

            final List<OrderEntry> activeOrders = tradeServiceFacade.findUserActiveOrders(portfolioEntry.getPrimaryKey(), bookEntry.getPrimaryKey());
            logger.info("queried active orders for user {} with order book {}: {}", portfolioEntry.getPrimaryKey(), bookEntry.getPrimaryKey(), activeOrders);
            model.addAttribute("activeOrders", activeOrders);
        }

        final List<PriceAggregate> buyOrders =
                tradeServiceFacade.findOrderAggregatedPrice(
                        bookEntry.getPrimaryKey(),
                        OrderType.BUY,
                        currentTime());

        final List<PriceAggregate> sellOrders =
                tradeServiceFacade.findOrderAggregatedPrice(
                        bookEntry.getPrimaryKey(),
                        OrderType.SELL,
                        currentTime());

        List<TradeExecutedEntry> executedTrades = tradeServiceFacade.findByOrderBookIdentifier(bookEntry
                .getPrimaryKey());
        model.addAttribute("coin", coin);
        model.addAttribute("buyOrders", buyOrders);
        model.addAttribute("sellOrders", sellOrders);
        model.addAttribute("executedTrades", executedTrades);

        return "index";
    }

    @RequestMapping(value = "/{coinId}", method = RequestMethod.GET)
    public String details(@PathVariable String coinId, Model model) {
        CoinEntry coin = tradeServiceFacade.loadCoin(coinId);
        OrderBookEntry bookEntry = tradeServiceFacade.loadOrderBookByCurrencyPair(coin.getPrimaryKey());

        final List<OrderEntry> buyOrders =
                tradeServiceFacade.findOrderForOrderBook(
                        bookEntry.getPrimaryKey(),
                        OrderType.BUY,
                        OrderStatus.PENDING);

        final List<OrderEntry> sellOrders =
                tradeServiceFacade.findOrderForOrderBook(
                        bookEntry.getPrimaryKey(),
                        OrderType.SELL,
                        OrderStatus.PENDING);

        List<TradeExecutedEntry> executedTrades = tradeServiceFacade.findByOrderBookIdentifier(bookEntry
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
            OrderBookEntry bookEntry = tradeServiceFacade.loadOrderBookByCurrencyPair(order.getCoinId());
            PortfolioEntry portfolioEntry = userServiceFacade.obtainPortfolioForUser();

            final BigDecimal tradeAmount = order.getTradeAmount();
            final BigDecimal itemPrice = order.getItemPrice();

            final Money price = Money.of(Constants.DEFAULT_CURRENCY_UNIT, itemPrice, RoundingMode.HALF_EVEN);
            final Money btcAmount = Money.of(Constants.CURRENCY_UNIT_BTC, tradeAmount, RoundingMode.HALF_EVEN);

            if (portfolioEntry.obtainAmountOfAvailableItemsFor(bookEntry.getPrimaryKey(), Constants.CURRENCY_UNIT_BTC).isLessThan(btcAmount)) {
                bindingResult.rejectValue("tradeAmount",
                        "error.order.sell.tomanyitems",
                        "Not enough items available to create sell order.");
                BuyOrder buyOrder = new BuyOrder();
                tradeServiceFacade.prepareBuyOrder(coi, );
                model.addAttribute("buyOrder", buyOrder);

                // 
                OrderBookEntry orderBookEntry = tradeServiceFacade.loadOrderBookByCurrencyPair(DEFUALT_COIN);
                model.addAttribute("orderBook", orderBookEntry);
//                addPortfolioItemInfoToModel(order.getCoinId(), model); 
                return "/index";
            }

            logger.info("placing a sell order with price {}, amount {}: {}.", price, btcAmount, order);

            tradeServiceFacade.sellOrder(bookEntry.getPrimaryKey(),portfolioEntry.getPrimaryKey(), btcAmount.toBigMoney(), price.toBigMoney());
            logger.info("Sell order {} dispatched... ", order);

            return "redirect:/index";
        }

//        addPortfolioItemInfoToModel(order.getCoinId(), model); 
        return "/index";
    }

    @RequestMapping(value = "/buy/{coinId}", method = RequestMethod.POST)
    public String buy(@ModelAttribute("buyOrder") @Valid BuyOrder order, BindingResult bindingResult, Model model) {
        if (!bindingResult.hasErrors()) {

            OrderBookEntry bookEntry = tradeServiceFacade.loadOrderBookByCurrencyPair(order.getCoinId());
            PortfolioEntry portfolioEntry = userServiceFacade.obtainPortfolioForUser();

            final BigDecimal tradeAmount = order.getTradeAmount();
            final BigDecimal itemPrice = order.getItemPrice();

            final Money price = Money.of(Constants.DEFAULT_CURRENCY_UNIT, itemPrice, RoundingMode.HALF_EVEN);
            final Money btcAmount = Money.of(Constants.CURRENCY_UNIT_BTC, tradeAmount, RoundingMode.HALF_EVEN);
            final Money totalMoney = btcAmount.convertedTo(price.getCurrencyUnit(), btcAmount.getAmount(), RoundingMode.HALF_EVEN);


            if (portfolioEntry.obtainMoneyToSpend().isLessThan(totalMoney)) {
                bindingResult.rejectValue("tradeAmount",
                        "error.order.buy.notenoughmoney",
                        "Not enough cash to spend to buy the items for the price you want");
                SellOrder sellOrder =  tradeServiceFacade.prepareSellOrder(DEFUALT_COIN, bookEntry);
                model.addAttribute("sellOrder", sellOrder);
                addPortfolioMoneyInfoToModel(portfolioEntry, model);
                return "/index";
            }

            logger.info("placing a buy order with price {}, amount {}, total money {}: {}.", price, btcAmount, totalMoney, order);

            tradeServiceFacade.buyOrder(bookEntry.getPrimaryKey(),portfolioEntry.getPrimaryKey(), btcAmount.toBigMoney(), price.toBigMoney());

            logger.info("Buy order {} dispatched... ", order);
            return "redirect:/index";
        }

        addPortfolioMoneyInfoToModel(model);
        return "/index";
    }

    private void addPortfolioItemInfoToModel(String identifier, Model model) {
        PortfolioEntry portfolioEntry = userServiceFacade.obtainPortfolioForUser();
        OrderBookEntry orderBookEntry = tradeServiceFacade.loadOrderBookByCurrencyPair(identifier);
        addPortfolioItemInfoToModel(portfolioEntry, orderBookEntry.getPrimaryKey(), orderBookEntry.getBaseCurrency(), model);
    }

    private void addPortfolioItemInfoToModel(PortfolioEntry entry, String orderBookIdentifier, CurrencyUnit currencyUnit, Model model) {
        model.addAttribute("itemsInPossession", entry.obtainAmountOfItemsInPossessionFor(orderBookIdentifier, currencyUnit));
        model.addAttribute("itemsReserved", entry.obtainAmountOfReservedItemsFor(orderBookIdentifier, currencyUnit));
    }

    private void addPortfolioMoneyInfoToModel(Model model) {
        PortfolioEntry portfolioEntry = userServiceFacade.obtainPortfolioForUser();
        addPortfolioMoneyInfoToModel(portfolioEntry, model);
    }

    private void addPortfolioMoneyInfoToModel(PortfolioEntry portfolioEntry, Model model) {
        model.addAttribute("moneyInPossession", portfolioEntry.getAmountOfMoney());
        model.addAttribute("moneyReserved", portfolioEntry.getReservedAmountOfMoney());
    }
} 