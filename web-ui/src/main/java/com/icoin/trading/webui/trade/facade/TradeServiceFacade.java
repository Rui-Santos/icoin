package com.icoin.trading.webui.trade.facade;

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
import org.joda.money.BigMoney;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/17/13
 * Time: 5:51 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TradeServiceFacade {
    OrderBookEntry loadOrderBookByCurrencyPair(CurrencyPair currencyPair);

    void refreshOrderBookPrice();

    CoinEntry loadCoin(String coinId);

    BigMoney calculateSellOrderEffectiveAmount(SellOrder order);

    BigMoney calculateBuyOrderEffectiveAmount(BuyOrder order);

    List<TradeExecutedEntry> findExecutedTradesByOrderBookIdentifier(String orderBookIdentifier);

    List<OrderEntry> findUserActiveOrders(String userId, String orderBookId);

    List<PriceAggregate> findOrderAggregatedPrice(String orderBookIdentifier, OrderType type, Date toDate);

    List<OrderEntry> findOrderForOrderBook(String orderBookIdentifier,
                                           OrderType type,
                                           OrderStatus orderStatus);

    BuyOrder prepareBuyOrder(String coinId, CurrencyPair currencyPair, OrderBookEntry orderBookEntry, PortfolioEntry portfolioEntry);

    SellOrder prepareSellOrder(String coinId, CurrencyPair currencyPair, OrderBookEntry orderBookEntry, PortfolioEntry portfolioEntry);

    void sellOrder(TransactionId transactionId, String orderBookId, String portfolioId, BigMoney tradeAmount, BigMoney price);


    void buyOrder(TransactionId transactionId, String orderBookId, String portfolioId, BigMoney tradeAmount, BigMoney price);
}