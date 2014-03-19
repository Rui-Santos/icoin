package com.icoin.trading.api.fee.events.commission;

import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/18/14
 * Time: 2:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class BuyExecutedCommissionTransactionStartedEvent extends ExecutedCommissionTransactionStartedEvent<BuyExecutedCommissionTransactionStartedEvent> {
    public BuyExecutedCommissionTransactionStartedEvent(FeeTransactionId feeTransactionId,
                                                        BigMoney commissionAmount,
                                                        String orderId,
                                                        String orderTransactionId,
                                                        String portfolioId,
                                                        Date tradeTime,
                                                        Date dueDate,
                                                        TradeType tradeType,
                                                        BigMoney tradedPrice,
                                                        BigMoney tradeAmount,
                                                        BigMoney executedMoney,
                                                        String orderBookId,
                                                        String coinId) {
        super(feeTransactionId,
                receivedFeeId, accountReceivableFeeId, offsetId, commissionAmount,
                orderId,
                orderTransactionId,
                portfolioId,
                tradeTime,
                dueDate,
                tradeType,
                tradedPrice,
                tradeAmount,
                executedMoney,
                orderBookId,
                coinId);
    }
}