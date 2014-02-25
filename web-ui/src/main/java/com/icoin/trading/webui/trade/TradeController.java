package com.icoin.trading.webui.trade;

import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.OrderEntry;
import com.icoin.trading.tradeengine.query.order.OrderType;
import com.icoin.trading.tradeengine.query.order.PriceAggregate;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.tradeexecuted.TradeExecutedEntry;
import com.icoin.trading.webui.order.BuyOrder;
import com.icoin.trading.webui.order.SellOrder;
import com.icoin.trading.webui.user.facade.UserServiceFacade;
import com.icoin.trading.webui.trade.facade.TradeServiceFacade;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
import java.util.Collections;
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
    static final String DEFUALT_COIN = "BTC";
    static final CurrencyPair DEFAULT_CCY_PAIR = CurrencyPair.BTC_CNY;
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

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String get(Model model) {
        OrderBookEntry orderBookEntry = tradeServiceFacade.loadOrderBookByCurrencyPair(DEFAULT_CCY_PAIR);
        model.addAttribute("orderBook", orderBookEntry);

        PortfolioEntry portfolioEntry = userServiceFacade.obtainPortfolioForUser();

        SellOrder sellOrder = tradeServiceFacade.prepareSellOrder(DEFUALT_COIN, DEFAULT_CCY_PAIR, orderBookEntry, portfolioEntry);
        model.addAttribute("sellOrder", sellOrder);

        BuyOrder buyOrder = tradeServiceFacade.prepareBuyOrder(DEFUALT_COIN, DEFAULT_CCY_PAIR, orderBookEntry, portfolioEntry);
        model.addAttribute("buyOrder", buyOrder);

        initPage(DEFUALT_COIN, orderBookEntry, portfolioEntry, model);
        return "index";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "refresh", method = RequestMethod.GET)
    public String refresh() {
        tradeServiceFacade.refreshOrderBookPrice();
        return "redirect:/";
    }

    @RequestMapping(value = "/coin/{coinId}", method = RequestMethod.GET)
    public String details(@PathVariable String coinId, Model model) {
        CurrencyPair currencyPair = new CurrencyPair(coinId);

        CoinEntry coin = tradeServiceFacade.loadCoin(coinId);
        OrderBookEntry bookEntry = tradeServiceFacade.loadOrderBookByCurrencyPair(currencyPair);

        final List<OrderEntry> buyOrders =
                tradeServiceFacade.findOrderForOrderBook(
                        bookEntry.getPrimaryKey(),
                        OrderType.BUY,
                        OrderStatus.PENDING,
                        new PageRequest(0,50));

        final List<OrderEntry> sellOrders =
                tradeServiceFacade.findOrderForOrderBook(
                        bookEntry.getPrimaryKey(),
                        OrderType.SELL,
                        OrderStatus.PENDING,
                        new PageRequest(0,50));

        List<TradeExecutedEntry> executedTrades = tradeServiceFacade.findExecutedTrades(bookEntry.getPrimaryKey());
        model.addAttribute("coin", coin);
        model.addAttribute("sellOrders", sellOrders);
        model.addAttribute("buyOrders", buyOrders);
        model.addAttribute("executedTrades", executedTrades);
        return "/index";
    }

    @RequestMapping(value = "/sell/{coinId}", method = RequestMethod.POST)
    public String sell(@PathVariable String coinId,
                       @ModelAttribute("sellOrder") @Valid SellOrder order,
                       BindingResult bindingResult, Model model) {
        CurrencyPair currencyPair = new CurrencyPair(coinId);
        CurrencyUnit priceCcy = currencyPair.getCounterCurrencyUnit();
        CurrencyUnit coinCcy = currencyPair.getBaseCurrencyUnit();

        OrderBookEntry orderBookEntry = tradeServiceFacade.loadOrderBookByCurrencyPair(currencyPair);
        model.addAttribute("orderBook", orderBookEntry);

        PortfolioEntry portfolioEntry = userServiceFacade.obtainPortfolioForUser();
        if (!bindingResult.hasErrors()) {
            final BigDecimal tradeAmount = order.getTradeAmount();
            final BigDecimal itemPrice = order.getItemPrice();

            final Money price = Money.of(priceCcy, itemPrice, RoundingMode.HALF_EVEN);
            final Money btcAmount = Money.of(coinCcy, tradeAmount, RoundingMode.HALF_EVEN);

            final BigMoney totalMoney = tradeServiceFacade.calculateSellOrderEffectiveAmount(order);

            if (portfolioEntry.obtainAmountOfAvailableItemFor(coinId, coinCcy).isLessThan(totalMoney)) {
                bindingResult.rejectValue("tradeAmount", "error.order.sell.tomanyitems", "Not enough items available to create sell order.");
                BuyOrder buyOrder = tradeServiceFacade.prepareBuyOrder(coinId, currencyPair, orderBookEntry, portfolioEntry);
                model.addAttribute("buyOrder", buyOrder);
                logger.info("rejected a sell order with price {}, amount {}, total money {}: {}.", price, btcAmount, totalMoney, order);
                initPage(coinId, orderBookEntry, portfolioEntry, model);
                return "/index";
            }
//
            final TransactionId transactionId = new TransactionId();
            logger.info("placing a sell transaction {} with price {}, amount {}: {}.", transactionId, price, btcAmount, order);
            tradeServiceFacade.sellOrder(transactionId, coinId, currencyPair, orderBookEntry.getPrimaryKey(), portfolioEntry.getPrimaryKey(), btcAmount.toBigMoney(), price.toBigMoney());
            logger.info("Sell order {} dispatched... ", order);

//            initPage(coinId, orderBookEntry, portfolioEntry, model);
            return "redirect:/";
        }

//        initPage(coinId, orderBookEntry, portfolioEntry, model);
        return "/index";
    }

    @RequestMapping(value = "/buy/{coinId}", method = RequestMethod.POST)
    public String buy(@PathVariable String coinId,
                      @ModelAttribute("buyOrder") @Valid BuyOrder order,
                      BindingResult bindingResult, Model model) {
        CurrencyPair currencyPair = new CurrencyPair(coinId);

        OrderBookEntry orderBookEntry = tradeServiceFacade.loadOrderBookByCurrencyPair(currencyPair);
        model.addAttribute("orderBook", orderBookEntry);
        PortfolioEntry portfolioEntry = userServiceFacade.obtainPortfolioForUser();

        if (!bindingResult.hasErrors()) {
            //todo
//            if(amountCcy){
//                bindingResult.rejectValue("amountCcy", "error.order.buy.amountCcy", "Amount ccy is not supported");
//            }


            final BigDecimal tradeAmount = order.getTradeAmount();
            final BigDecimal itemPrice = order.getItemPrice();

            CurrencyUnit priceCcy = currencyPair.getCounterCurrencyUnit();
            CurrencyUnit coinCcy = currencyPair.getBaseCurrencyUnit();

            final Money price = Money.of(priceCcy, itemPrice, RoundingMode.HALF_EVEN);
            final Money btcAmount = Money.of(coinCcy, tradeAmount, RoundingMode.HALF_EVEN);
//            final Money totalMoney = btcAmount.convertedTo(price.getCurrencyUnit(), price.getAmount(), RoundingMode.HALF_EVEN);

            final BigMoney totalMoney = tradeServiceFacade.calculateBuyOrderEffectiveAmount(order);

            if (portfolioEntry.obtainMoneyToSpend().isLessThan(totalMoney)) {
                bindingResult.rejectValue("tradeAmount", "error.order.buy.notenoughmoney", "Not enough cash to spend to buy the items for the price you want");
                SellOrder sellOrder = tradeServiceFacade.prepareSellOrder(coinId, currencyPair, orderBookEntry, portfolioEntry);
                model.addAttribute("sellOrder", sellOrder);
                logger.info("rejected a buy order with price {}, amount {}, total money {}: {}.", price, btcAmount, totalMoney, order);
                initPage(coinId, orderBookEntry, portfolioEntry, model);
                return "/index";
            }

            final TransactionId transactionId = new TransactionId();
            logger.info("placing a buy transaction {} with price {}, amount {}, total money {}: {}.", transactionId, price, btcAmount, totalMoney, order);
            tradeServiceFacade.buyOrder(transactionId, coinId, currencyPair, orderBookEntry.getPrimaryKey(), portfolioEntry.getPrimaryKey(), btcAmount.toBigMoney(), price.toBigMoney());
            logger.info("Buy order {} dispatched... ", order);

//            initPage(coinId, orderBookEntry, portfolioEntry, model);
            return "redirect:/";
        }

//        initPage(coinId, orderBookEntry, portfolioEntry, model);
        return "/index";
    }


    private void initPage(String coinId,
                          OrderBookEntry orderBookEntry,
                          PortfolioEntry portfolioEntry,
                          Model model) {
        CoinEntry coin = tradeServiceFacade.loadCoin(coinId);
        model.addAttribute("coin", coin);

        List<OrderEntry> activeOrders = Collections.emptyList();
        List<PriceAggregate> buyOrders = Collections.emptyList();
        List<PriceAggregate> sellOrders = Collections.emptyList();
        List<TradeExecutedEntry> executedTrades = Collections.emptyList();

        if (orderBookEntry != null) {
            buyOrders = tradeServiceFacade.findOrderAggregatedPrice(
                    orderBookEntry.getPrimaryKey(),
                    OrderType.BUY,
                    currentTime());

            sellOrders = tradeServiceFacade.findOrderAggregatedPrice(
                    orderBookEntry.getPrimaryKey(),
                    OrderType.SELL,
                    currentTime());

            executedTrades = tradeServiceFacade.findExecutedTrades(orderBookEntry.getPrimaryKey());

            if (portfolioEntry != null) {
                activeOrders = tradeServiceFacade.findUserActiveOrders(portfolioEntry.getPrimaryKey(), orderBookEntry.getPrimaryKey());
                logger.info("queried active orders for user {} with order book {}: {}", portfolioEntry.getPrimaryKey(), orderBookEntry.getPrimaryKey(), activeOrders);
            }
        }

        model.addAttribute("activeOrders", activeOrders);
        model.addAttribute("buyOrders", buyOrders);
        model.addAttribute("sellOrders", sellOrders);
        model.addAttribute("executedTrades", executedTrades);
    }
}