package com.icoin.trading.fee.application.command;

import com.icoin.trading.api.fee.command.commission.GainBuyCoinTransactionCommand;
import com.icoin.trading.api.fee.command.commission.GainSoldMoneyTransactionCommand;
import com.icoin.trading.api.fee.command.commission.PayBuyCommissionTransactionCommand;
import com.icoin.trading.api.fee.command.commission.PayBuyMoneyTransactionCommand;
import com.icoin.trading.api.fee.command.commission.PaySellCommissionTransactionCommand;
import com.icoin.trading.api.fee.command.commission.PaySoldCoinTransactionCommand;
import com.icoin.trading.api.fee.domain.ExecutedFeeType;
import com.icoin.trading.fee.domain.transaction.ExecutedCoinTransaction;
import com.icoin.trading.fee.domain.transaction.ExecutedCommissionTransaction;
import com.icoin.trading.fee.domain.transaction.ExecutedMoneyTransaction;
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
public class ExecutionCommandHandler {
    private Repository<ExecutedCommissionTransaction> commissionRepository;
    private Repository<ExecutedCoinTransaction> coinRepository;
    private Repository<ExecutedMoneyTransaction> moneyRepository;


    @CommandHandler
    public void handleStartToSell(PaySellCommissionTransactionCommand command) {
        ExecutedCommissionTransaction transaction = new ExecutedCommissionTransaction(
                command.getFeeTransactionId(),
                command.getPaidFeeId(),
                command.getAccountPayableFeeId(),
                command.getOffsetId(),
                ExecutedFeeType.SELL_COMMISSION,
                command.getCommission(),
                command.getOrderId(),
                command.getOrderTransactionId(),
                command.getPortfolioId(),
                command.getUserId(),
                command.getTradeTime(),
                command.getDueDate(),
                command.getTradeType(),
                command.getTradedPrice(),
                command.getTradeAmount(),
                command.getExecutedMoney(),
                command.getOrderBookId(),
                command.getCoinId());

        commissionRepository.add(transaction);
    }

    @CommandHandler
    public void handleStartToBuy(PayBuyCommissionTransactionCommand command) {
        ExecutedCommissionTransaction transaction =
                new ExecutedCommissionTransaction(
                        command.getFeeTransactionId(),
                        command.getPaidFeeId(),
                        command.getAccountPayableFeeId(),
                        command.getOffsetId(),
                        ExecutedFeeType.BUY_COMMISSION,
                        command.getCommission(),
                        command.getOrderId(),
                        command.getOrderTransactionId(),
                        command.getPortfolioId(),
                        command.getUserId(),
                        command.getTradeTime(),
                        command.getDueDate(),
                        command.getTradeType(),
                        command.getTradedPrice(),
                        command.getTradeAmount(),
                        command.getExecutedMoney(),
                        command.getOrderBookId(),
                        command.getCoinId());

        commissionRepository.add(transaction);
    }

    @CommandHandler
    public void handleStartToPaySoldCoin(PaySoldCoinTransactionCommand command) {
        ExecutedCoinTransaction transaction =
                new ExecutedCoinTransaction(
                        command.getFeeTransactionId(),
                        command.getPaidFeeId(),
                        command.getAccountPayableFeeId(),
                        command.getOffsetId(),
                        ExecutedFeeType.PAY,
                        command.getOrderId(),
                        command.getOrderTransactionId(),
                        command.getPortfolioId(),
                        command.getUserId(),
                        command.getTradeTime(),
                        command.getDueDate(),
                        command.getTradeType(),
                        command.getTradedPrice(),
                        command.getTradeAmount(),
                        command.getExecutedMoney(),
                        command.getOrderBookId(),
                        command.getCoinId());

        coinRepository.add(transaction);
    }


    @CommandHandler
    public void handleStartToReceiveMoneyFromSale(GainSoldMoneyTransactionCommand command) {
        ExecutedMoneyTransaction transaction =
                new ExecutedMoneyTransaction(
                        command.getFeeTransactionId(),
                        command.getReceivedFeeId(),
                        command.getAccountReceivableFeeId(),
                        command.getOffsetId(),
                        ExecutedFeeType.RECEIVE,
                        command.getOrderId(),
                        command.getOrderTransactionId(),
                        command.getPortfolioId(),
                        command.getUserId(),
                        command.getTradeTime(),
                        command.getDueDate(),
                        command.getTradeType(),
                        command.getTradedPrice(),
                        command.getTradeAmount(),
                        command.getExecutedMoney(),
                        command.getOrderBookId(),
                        command.getCoinId());

        moneyRepository.add(transaction);
    }

    @CommandHandler
    public void handleStartToPayForCoins(PayBuyMoneyTransactionCommand command) {
        ExecutedMoneyTransaction transaction =
                new ExecutedMoneyTransaction(
                        command.getFeeTransactionId(),
                        command.getPaidFeeId(),
                        command.getAccountPayableFeeId(),
                        command.getOffsetId(),
                        ExecutedFeeType.PAY,
                        command.getOrderId(),
                        command.getOrderTransactionId(),
                        command.getPortfolioId(),
                        command.getUserId(),
                        command.getTradeTime(),
                        command.getDueDate(),
                        command.getTradeType(),
                        command.getTradedPrice(),
                        command.getTradeAmount(),
                        command.getExecutedMoney(),
                        command.getOrderBookId(),
                        command.getCoinId());

        moneyRepository.add(transaction);
    }

    @CommandHandler
    public void handleStartToReceiveCoinsFromSale(GainBuyCoinTransactionCommand command) {
        ExecutedCoinTransaction transaction =
                new ExecutedCoinTransaction(
                        command.getFeeTransactionId(),
                        command.getReceivedFeeId(),
                        command.getAccountReceivableFeeId(),
                        command.getOffsetId(),
                        ExecutedFeeType.RECEIVE,
                        command.getOrderId(),
                        command.getOrderTransactionId(),
                        command.getPortfolioId(),
                        command.getUserId(),
                        command.getTradeTime(),
                        command.getDueDate(),
                        command.getTradeType(),
                        command.getTradedPrice(),
                        command.getTradeAmount(),
                        command.getExecutedMoney(),
                        command.getOrderBookId(),
                        command.getCoinId());

        coinRepository.add(transaction);
    }

    @Resource(name = "executedCommissionTransactionRepository")
    public void setCommissionRepository(Repository<ExecutedCommissionTransaction> commissionRepository) {
        this.commissionRepository = commissionRepository;
    }

    @Resource(name = "executedCoinTransactionRepository")
    public void setCoinRepository(Repository<ExecutedCoinTransaction> coinRepository) {
        this.coinRepository = coinRepository;
    }

    @Resource(name = "executedMoneyTransactionRepository")
    public void setMoneyRepository(Repository<ExecutedMoneyTransaction> moneyRepository) {
        this.moneyRepository = moneyRepository;
    }
}