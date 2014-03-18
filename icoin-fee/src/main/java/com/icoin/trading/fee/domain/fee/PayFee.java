package com.icoin.trading.fee.domain.fee;

import com.homhon.fee.domain.model.amount.Amount;
import com.homhon.mongo.domainsupport.modelsupport.money.Money;

import java.util.Date;

import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-8-16
 * Time: AM9:33
 * To change this template use File | Settings | File Templates.
 */
public class PayFee extends AbstractFee<PayFee> {

    public PayFee(FeeType feeType) {
        super(feeType);
        validate();
    }

    private void validate() {
        isTrue(FeeMovingDirection.MOVING_OUT == getFeeType().getMovingDirection());
    }

    public static PayFee createNewPayFee(Money money,
                                         FeeType feeType,
                                         String accountId,
                                         Date businessCreationTime,
                                         Date dueDate) {
        hasLength(accountId);
        notNull(money);
        isTrue(Money.greaterThanZero(money));

        Amount<Money> amount = Amount.createMoneyAmount(money);

        PayFee payFee = new PayFee(feeType);
        initializeFee(payFee, amount, accountId, businessCreationTime, dueDate);

        return payFee;
    }
}