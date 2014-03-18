package com.icoin.trading.fee.saga;

import com.icoin.trading.api.fee.command.sell.CreateSellCommissionFeeCommand;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.tradeengine.events.trade.TradeExecutedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-17
 * Time: PM10:13
 * To change this template use File | Settings | File Templates.
 */
public class CommissionManagerSaga extends AbstractAnnotatedSaga {
    private transient CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handleSellCommission(ReceiveTransactionStartedEvent event) {
        notNull(event.getTradeTime());
        logger.debug("About to create a sell commission with executed event {}", event);
        Date dueDate = new DateTime(event.getTradeTime(), zone).toDate();

        CreateSellCommissionFeeCommand command =
                new CreateSellCommissionFeeCommand(
                        new FeeTransactionId(),
                        event.getSellTransactionId().toString(),
                        event.getSellCommission(),
                        event.getTradeTime(),
                        dueDate,
                        event.getSellPortfolioId().toString(),
                        event.getOrderBookIdentifier().toString(),
                        event.getCoinId().toString(),
                        event.getTradeType(),
                        event.getTradedPrice(),
                        event.getTradeAmount(),
                        event.getExecutedMoney()
                );
        commandGateway.send(command);
    }
}
