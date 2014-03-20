package com.icoin.trading.fee.application.listener;

import com.icoin.trading.api.fee.command.commission.StartSellCommissionTransactionCommand;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.tradeengine.events.trade.TradeExecutedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/17/14
 * Time: 11:27 AM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ExecutedCommissionApplicationListener {
    private final static Logger logger = LoggerFactory.getLogger(ExecutedCommissionApplicationListener.class);
    private DateTimeZone zone = DateTimeZone.forID("Asia/Chongqing");
    private CommandGateway commandGateway;

    @EventHandler
    public void handleSellCommission(TradeExecutedEvent event) {
        notNull(event.getTradeTime());
        logger.debug("About to create a sell commission with executed event {}", event);
        Date dueDate = computeDueDate(event);

        StartSellCommissionTransactionCommand command =
                new StartSellCommissionTransactionCommand(
                        new FeeTransactionId(),
                        new FeeId(),
                        new FeeId(),
                        new OffsetId(),
                        event.getSellCommission(),
                        event.getSellOrderId(),
                        event.getSellTransactionId(),
                        event.getSellPortfolioId(),
                        event.getTradeTime(),
                        dueDate,
                        event.getTradeType(),
                        event.getTradedPrice(),
                        event.getTradeAmount(),
                        event.getExecutedMoney(),
                        event.getOrderBookId(),
                        event.getCoinId());

        commandGateway.send(command);
    }

    private Date computeDueDate(TradeExecutedEvent event) {
        return new DateTime(event.getTradeTime(), zone).toDate();
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

    public void setZone(String zone) {
        this.zone = DateTimeZone.forID(zone);
    }
}

