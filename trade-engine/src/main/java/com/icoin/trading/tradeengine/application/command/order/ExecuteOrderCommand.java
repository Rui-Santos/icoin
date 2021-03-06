package com.icoin.trading.tradeengine.application.command.order;

import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-8
 * Time: PM10:13
 * Abstract execute order command.
 */
public abstract class ExecuteOrderCommand<T extends ExecuteOrderCommand> extends AbstractOrderCommand<T> {
    protected ExecuteOrderCommand(OrderId orderId,
                                  PortfolioId portfolioId,
                                  OrderBookId orderBookId,
                                  TransactionId transactionId,
                                  BigMoney tradeCount,
                                  BigMoney itemPrice,
                                  Date placeDate) {
        super(orderId, portfolioId, orderBookId, transactionId, tradeCount, itemPrice, placeDate);
    }
}
