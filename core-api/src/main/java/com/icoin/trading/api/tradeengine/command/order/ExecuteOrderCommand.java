package com.icoin.trading.api.tradeengine.command.order;

import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.OrderId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
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
