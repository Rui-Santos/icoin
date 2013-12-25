package com.icoin.trading.tradeengine.application.command.order.handler;

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.commission.Commission;
import com.icoin.trading.tradeengine.domain.model.commission.CommissionPolicy;
import com.icoin.trading.tradeengine.domain.model.commission.CommissionPolicyFactory;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrderRepository;
import com.icoin.trading.tradeengine.domain.model.order.OrderBook;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import com.icoin.trading.tradeengine.domain.model.order.SellOrderRepository;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-8
 * Time: PM10:10
 * Execute helper for order execution.
 * todo, if have time, change it to singleton with repo's set
 */
@Component
public class OrderExecutorHelper {
    private SellOrderRepository sellOrderRepository;
    private BuyOrderRepository buyOrderRepository;
    private CommissionPolicyFactory commissionPolicyFactory;

    private static Logger logger = LoggerFactory.getLogger(OrderExecutorHelper.class);

    public List<SellOrder> findAscPendingOrdersByPriceTime(Date toTime,
                                                           BigMoney price,
                                                           OrderBookId orderBookId,
                                                           int size) {
        notNull(toTime);
        notNull(price);
        isTrue(price.isPositive(), "Price should be greater than 0!");
        isTrue(size > 0, "Size should be greater than 0!");


        if (logger.isDebugEnabled()) {
            logger.debug("To find asc sell pending orders with toTime{}, price {}, orderBookId {}, size {} ",
                    toTime, price, orderBookId, size);
        }

        List<SellOrder> list = sellOrderRepository.findAscPendingOrdersByPriceTime(toTime, price, orderBookId, size);

        if (logger.isDebugEnabled()) {
            logger.debug("Found asc sell pending orders with toTime{}, price {}, orderBookId {}, size {}: ",
                    toTime, price, orderBookId, size, list);
        }
        return list;
    }


    public List<BuyOrder> findDescPendingOrdersByPriceTime(Date toTime,
                                                           BigMoney price,
                                                           OrderBookId orderBookId,
                                                           int size) {
        notNull(toTime);
        notNull(price);
        isTrue(price.isPositive(), "Price should be greater than 0!");
        isTrue(size > 0, "Size should be greater than 0!");


        if (logger.isDebugEnabled()) {
            logger.debug("To find desc buy pending orders with toTime{}, price {}, orderBookId {}, size {} ",
                    toTime, price, orderBookId, size);
        }

        List<BuyOrder> list = buyOrderRepository.findDescPendingOrdersByPriceTime(toTime, price, orderBookId, size);

        if (logger.isDebugEnabled()) {
            logger.debug("Found desc buy pending orders with toTime{}, price {}, orderBookId {}, size {}: ",
                    toTime, price, orderBookId, size, list);
        }
        return list;
    }

    public SellOrder findSellOrder(OrderId orderId) {
        notNull(orderId);
        final SellOrder sellOrder = sellOrderRepository.findOne(orderId.toString());

        if (logger.isDebugEnabled()) {
            logger.debug("sellOrder {} found: {}", orderId, sellOrder);
        }

        return sellOrder;
    }

    public BuyOrder findBuyOrder(OrderId orderId) {
        notNull(orderId);
        final BuyOrder buyOrder = buyOrderRepository.findOne(orderId.toString());

        if (logger.isDebugEnabled()) {
            logger.debug("buyOrder {} found: {}", orderId, buyOrder);
        }

        return buyOrder;
    }

    public void refresh(OrderBook orderBook) {
        notNull(orderBook);
        notNull(orderBook.getCurrencyPair());
        notNull(orderBook.getOrderBookId());

        String lowestSellOrderId = null;
        String highestBuyOrderId = null;

        OrderBookId orderBookId = orderBook.getOrderBookId();
        CurrencyPair currencyPair = orderBook.getCurrencyPair();
        BigMoney lowestSellPrice = BigMoney.of(CurrencyUnit.of(currencyPair.getCounterCurrency()), Constants.INIT_SELL_PRICE);
        BigMoney highestBuyPrice = BigMoney.zero(CurrencyUnit.of(currencyPair.getCounterCurrency()));


        final SellOrder lowestSell = sellOrderRepository.findLowestPricePendingOrder(orderBookId);
        final BuyOrder highestBuy = buyOrderRepository.findHighestPricePendingOrder(orderBookId);

        if (lowestSell != null) {
            lowestSellOrderId = lowestSell.getPrimaryKey();
            lowestSellPrice = lowestSell.getItemPrice();
        }
        if (highestBuy != null) {
            highestBuyOrderId = highestBuy.getPrimaryKey();
            highestBuyPrice = highestBuy.getItemPrice();
        }

        logger.info("Refreshing with lowest sell order {} and price {}, Refreshing with highest buy order {} and price {}",
                lowestSellOrderId, lowestSellPrice, highestBuyOrderId, highestBuyPrice);
        orderBook.resetLowestSellPrice(lowestSellOrderId, lowestSellPrice);
        orderBook.resetHighestBuyPrice(highestBuyOrderId, highestBuyPrice);
    }

    public void recordTraded(BuyOrder buyOrder,
                             SellOrder sellOrder,
                             BigMoney buyCommission,
                             BigMoney sellCommission,
                             BigMoney matchedTradeAmount,
                             BigMoney matchedTradePrice,
                             Date tradedDate) {
        notNull(buyOrder);
        notNull(sellOrder);


        buyOrder.recordTraded(matchedTradeAmount, buyCommission, tradedDate);
        sellOrder.recordTraded(matchedTradeAmount, sellCommission, tradedDate);
        logger.info("for this trade, buy order {} and sell order {} have commission {} and {}.",
                buyOrder, sellOrder, buyCommission, sellCommission);

        buyOrderRepository.save(buyOrder);
        sellOrderRepository.save(sellOrder);
    }

    public BigMoney calcExecutedSellCommission(SellOrder sellOrder, BigMoney matchedTradePrice, BigMoney matchedTradeAmount) {
        CommissionPolicy commissionPolicy = commissionPolicyFactory.createCommissionPolicy(sellOrder);
        Commission commission = commissionPolicy.calculateSellCommission(sellOrder, matchedTradeAmount, matchedTradePrice);
        return commission.getBigMoneyCommission();
    }

    public BigMoney calcExecutedBuyCommission(BuyOrder buyOrder, BigMoney matchedTradePrice, BigMoney matchedTradeAmount) {
        CommissionPolicy commissionPolicy = commissionPolicyFactory.createCommissionPolicy(buyOrder);
        Commission commission = commissionPolicy.calculateBuyCommission(buyOrder, matchedTradeAmount, matchedTradePrice);
        return commission.getBigMoneyCommission();
    }


    @Autowired
    public void setBuyOrderRepository(BuyOrderRepository buyOrderRepository) {
        this.buyOrderRepository = buyOrderRepository;
    }

    @Autowired
    public void setSellOrderRepository(SellOrderRepository sellOrderRepository) {
        this.sellOrderRepository = sellOrderRepository;
    }

    @Autowired
    public void setCommissionPolicyFactory(CommissionPolicyFactory commissionPolicyFactory) {
        this.commissionPolicyFactory = commissionPolicyFactory;
    }
}
