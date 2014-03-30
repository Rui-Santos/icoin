package com.icoin.trading.api.fee.command.commission;

import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import org.joda.money.BigMoney;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/18/14
 * Time: 4:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayTransactionCommand<T extends PayTransactionCommand> extends ExecutionTransactionCommand<T> {
    @NotNull
    protected final FeeId paidFeeId;
    @NotNull
    protected final FeeId accountPayableFeeId;

    public PayTransactionCommand(FeeTransactionId feeTransactionId,
                                 FeeId paidFeeId,
                                 FeeId accountPayableFeeId,
                                 OffsetId offsetId,
                                 String orderId,
                                 TransactionId orderTransactionId,
                                 PortfolioId portfolioId,
                                 Date tradeTime,
                                 Date dueDate,
                                 TradeType tradeType,
                                 BigMoney tradedPrice,
                                 BigMoney tradeAmount,
                                 BigMoney executedMoney,
                                 OrderBookId orderBookId,
                                 CoinId coinId) {
        super(feeTransactionId,
                offsetId,
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
        this.paidFeeId = paidFeeId;
        this.accountPayableFeeId = accountPayableFeeId;
    }

    public FeeId getPaidFeeId() {
        return paidFeeId;
    }

    public FeeId getAccountPayableFeeId() {
        return accountPayableFeeId;
    }
}