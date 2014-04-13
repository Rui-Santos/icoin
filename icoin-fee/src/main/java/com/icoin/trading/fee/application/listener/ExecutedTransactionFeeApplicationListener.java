package com.icoin.trading.fee.application.listener;

import com.icoin.trading.api.fee.command.commission.GainBuyCoinTransactionCommand;
import com.icoin.trading.api.fee.command.commission.GainSoldMoneyTransactionCommand;
import com.icoin.trading.api.fee.command.commission.PayBuyCommissionTransactionCommand;
import com.icoin.trading.api.fee.command.commission.PayBuyMoneyTransactionCommand;
import com.icoin.trading.api.fee.command.commission.PaySellCommissionTransactionCommand;
import com.icoin.trading.api.fee.command.commission.PaySoldCoinTransactionCommand;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.tradeengine.events.trade.TradeExecutedEvent;
import com.icoin.trading.fee.domain.DueDateService;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ExecutedTransactionFeeApplicationListener {
    private final static Logger logger = LoggerFactory.getLogger(ExecutedTransactionFeeApplicationListener.class);
    //    private DateTimeZone zone = DateTimeZone.forID("Asia/Chongqing");
    private DueDateService dueDateService;
    private CommandGateway commandGateway;

    @EventHandler
    public void handlePaySellCommission(TradeExecutedEvent event) {
        notNull(event.getTradeTime());
        logger.debug("About to pay sell commission with executed event {}", event);
        Date dueDate = dueDateService.computeDueDate(event.getTradeTime());

        PaySellCommissionTransactionCommand command =
                new PaySellCommissionTransactionCommand(
                        new FeeTransactionId(),
                        new FeeId(),
                        new FeeId(),
                        new OffsetId(),
                        event.getSellCommission(),
                        event.getSellOrderId(),
                        event.getSellTransactionId(),
                        event.getSellPortfolioId(),
                        event.getSellUserId(),
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

    @EventHandler
    public void handlePayBuyCommission(TradeExecutedEvent event) {
        notNull(event.getTradeTime());
        logger.debug("About to pay buy commission with executed event {}", event);
        Date dueDate = dueDateService.computeDueDate(event.getTradeTime());

        PayBuyCommissionTransactionCommand command =
                new PayBuyCommissionTransactionCommand(
                        new FeeTransactionId(),
                        new FeeId(),
                        new FeeId(),
                        new OffsetId(),
                        event.getSellCommission(),
                        event.getSellOrderId(),
                        event.getSellTransactionId(),
                        event.getSellPortfolioId(),
                        event.getSellUserId(),
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

    //pay sold coins
    @EventHandler
    public void handlePaySoldCoin(TradeExecutedEvent event) {
        notNull(event.getTradeTime());
        logger.debug("About to pay sold coins with executed event {}", event);
        Date dueDate = dueDateService.computeDueDate(event.getTradeTime());

        PaySoldCoinTransactionCommand command =
                new PaySoldCoinTransactionCommand(
                        new FeeTransactionId(),
                        new FeeId(),
                        new FeeId(),
                        new OffsetId(),
                        event.getSellOrderId(),
                        event.getSellTransactionId(),
                        event.getSellPortfolioId(),
                        event.getSellUserId(),
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

    //receive money from sale
    @EventHandler
    public void handleReceiveMoneyFromSale(TradeExecutedEvent event) {
        notNull(event.getTradeTime());
        logger.debug("About to receive money from sale with executed event {}", event);
        Date dueDate = dueDateService.computeDueDate(event.getTradeTime());

        GainSoldMoneyTransactionCommand command =
                new GainSoldMoneyTransactionCommand(
                        new FeeTransactionId(),
                        new FeeId(),
                        new FeeId(),
                        new OffsetId(),
                        event.getSellOrderId(),
                        event.getSellTransactionId(),
                        event.getSellPortfolioId(),
                        event.getSellUserId(),
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

    //pay for buying coins
    @EventHandler
    public void handlePayForCoins(TradeExecutedEvent event) {
        notNull(event.getTradeTime());
        logger.debug("About to pay for buying coins with executed event {}", event);
        Date dueDate = dueDateService.computeDueDate(event.getTradeTime());

        PayBuyMoneyTransactionCommand command =
                new PayBuyMoneyTransactionCommand(
                        new FeeTransactionId(),
                        new FeeId(),
                        new FeeId(),
                        new OffsetId(),
                        event.getBuyOrderId(),
                        event.getBuyTransactionId(),
                        event.getBuyPortfolioId(),
                        event.getBuyUserId(),
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

    //receive coins from sale
    @EventHandler
    public void handleReceiveCoinsFromSale(TradeExecutedEvent event) {
        notNull(event.getTradeTime());
        logger.debug("About to receive coins from sale with executed event {}", event);
        Date dueDate = dueDateService.computeDueDate(event.getTradeTime());

        GainBuyCoinTransactionCommand command =
                new GainBuyCoinTransactionCommand(
                        new FeeTransactionId(),
                        new FeeId(),
                        new FeeId(),
                        new OffsetId(),
                        event.getBuyOrderId(),
                        event.getBuyTransactionId(),
                        event.getBuyPortfolioId(),
                        event.getBuyUserId(),
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

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Resource(name = "fee.commandGateway") //todo check if it's needed to use Autowired
    public void setCommandGateway(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Autowired
    public void setDueDateService(DueDateService dueDateService) {
        this.dueDateService = dueDateService;
    }
}

