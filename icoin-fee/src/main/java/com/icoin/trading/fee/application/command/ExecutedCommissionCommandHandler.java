package com.icoin.trading.fee.application.command;

import com.icoin.trading.api.fee.command.commission.StartBuyCommissionTransactionCommand;
import com.icoin.trading.api.fee.command.commission.StartSellCommissionTransactionCommand;
import com.icoin.trading.api.fee.domain.CommissionType;
import com.icoin.trading.fee.domain.transaction.ExecutedCommissionTransaction;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/17/14
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ExecutedCommissionCommandHandler {
    private Repository<ExecutedCommissionTransaction> transactionRepository;


    @CommandHandler
    public void handleStartToSell(StartSellCommissionTransactionCommand command) {
        ExecutedCommissionTransaction transaction = new ExecutedCommissionTransaction(
                command.getFeeTransactionId(),
                command.getReceivedFeeId(),
                command.getAccountReceivableFeeId(),
                command.getOffsetId(),
                CommissionType.SELL,
                command.getCommissionAmount(),
                command.getOrderId(),
                command.getOrderTransactionId(),
                command.getPortfolioId(),
                command.getTradeTime(),
                command.getDueDate(),
                command.getTradeType(),
                command.getTradedPrice(),
                command.getTradeAmount(),
                command.getExecutedMoney(),
                command.getOrderBookId(),
                command.getCoinId());

        transactionRepository.add(transaction);
    }

    @CommandHandler
    public void handleStartToBuy(StartBuyCommissionTransactionCommand command) {
        ExecutedCommissionTransaction transaction =
                new ExecutedCommissionTransaction(
                        command.getFeeTransactionId(),
                        command.getReceivedFeeId(),
                        command.getAccountReceivableFeeId(),
                        command.getOffsetId(),
                        CommissionType.BUY,
                        command.getCommissionAmount(),
                        command.getOrderId(),
                        command.getOrderTransactionId(),
                        command.getPortfolioId(),
                        command.getTradeTime(),
                        command.getDueDate(),
                        command.getTradeType(),
                        command.getTradedPrice(),
                        command.getTradeAmount(),
                        command.getExecutedMoney(),
                        command.getOrderBookId(),
                        command.getCoinId());

        transactionRepository.add(transaction);
    }

    @Resource(name = "executedCommissionTransactionRepository")
    public void setRepository(Repository<ExecutedCommissionTransaction> repository) {
        this.transactionRepository = repository;
    }
}