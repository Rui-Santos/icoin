package com.icoin.trading.fee.application.listener;

import com.icoin.trading.api.fee.command.sell.CreateSellCommissionFeeCommand;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.tradeengine.events.trade.TradeExecutedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM8:19
 * To change this template use File | Settings | File Templates.
 */
public class ExecutedCommissionListener {
    private final static Logger logger = LoggerFactory.getLogger(ExecutedCommissionListener.class);
    private DateTimeZone zone = DateTimeZone.forID("Asia/Chongqing");
    private CommandGateway commandGateway;

    @EventHandler
    public void handleSellCommission(TradeExecutedEvent event) {
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

    @EventHandler
    public void handleBuyCommission(TradeExecutedEvent event) {
        logger.debug("About to create a buy commission with executed event {}", event);
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Resource(name = "fee.commandGateway") //todo check if it's needed to use Autowired
    public void setCommandGateway(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    public void setZone(DateTimeZone zone) {
        this.zone = zone;
    }
}
