package com.icoin.trading.api.fee.command.commission;


import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import com.icoin.trading.api.users.domain.UserId;
import org.joda.money.BigMoney;

import javax.validation.constraints.NotNull;
import java.util.Date;

import static com.homhon.util.Asserts.isTrue;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM10:05
 * To change this template use File | Settings | File Templates.
 */
public class PaySellCommissionTransactionCommand extends PayTransactionCommand<PaySellCommissionTransactionCommand> {
    @NotNull
    private BigMoney commission;

    public PaySellCommissionTransactionCommand(FeeTransactionId feeTransactionId,
                                               FeeId paidFeeId,
                                               FeeId accountPayableFeeId,
                                               OffsetId offsetId,
                                               BigMoney commission,
                                               String orderId,
                                               TransactionId orderTransactionId,
                                               PortfolioId portfolioId,
                                               UserId userId,
                                               Date tradeTime,
                                               Date dueDate,
                                               TradeType tradeType,
                                               BigMoney tradedPrice,
                                               BigMoney tradeAmount,
                                               BigMoney executedMoney,
                                               OrderBookId orderBookId,
                                               CoinId coinId) {
        super(feeTransactionId,
                paidFeeId,
                accountPayableFeeId,
                offsetId,
                orderId,
                orderTransactionId,
                portfolioId,
                userId,
                tradeTime,
                dueDate,
                tradeType,
                tradedPrice,
                tradeAmount,
                executedMoney,
                orderBookId,
                coinId);
        isTrue(commission.isPositive());
        this.commission = commission;
    }

    public BigMoney getCommission() {
        return commission;
    }
}