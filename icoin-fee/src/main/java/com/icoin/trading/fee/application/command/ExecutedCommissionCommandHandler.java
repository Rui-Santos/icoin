package com.icoin.trading.fee.application.command;

import com.icoin.trading.api.fee.command.commission.PayBuyCommissionTransactionCommand;
import com.icoin.trading.api.fee.command.commission.PaySellCommissionTransactionCommand;
import com.icoin.trading.api.fee.domain.ExecutedFeeType;
import com.icoin.trading.fee.domain.transaction.ExecutedFeeTransaction;
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
    private Repository<ExecutedFeeTransaction> transactionRepository;


    @CommandHandler
    public void handleStartToSell(PaySellCommissionTransactionCommand command) {
        ExecutedFeeTransaction transaction = new ExecutedFeeTransaction(
                command.getFeeTransactionId(),
                command.getReceivedFeeId(),
                command.getAccountReceivableFeeId(),
                command.getOffsetId(),
                ExecutedFeeType.SELL_COMMISSION,
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
    public void handleStartToBuy(PayBuyCommissionTransactionCommand command) {
        ExecutedFeeTransaction transaction =
                new ExecutedFeeTransaction(
                        command.getFeeTransactionId(),
                        command.getReceivedFeeId(),
                        command.getAccountReceivableFeeId(),
                        command.getOffsetId(),
                        ExecutedFeeType.BUY_COMMISSION,
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
    public void setRepository(Repository<ExecutedFeeTransaction> repository) {
        this.transactionRepository = repository;
    }
}