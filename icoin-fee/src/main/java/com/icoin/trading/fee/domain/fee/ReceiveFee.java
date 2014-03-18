package com.icoin.trading.fee.domain.fee;

import com.icoin.trading.api.fee.domain.fee.FeeMovingDirection;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import org.joda.money.BigMoney;

import java.util.Date;

import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-8-16
 * Time: AM9:34
 * To change this template use File | Settings | File Templates.
 */
public class ReceiveFee extends AbstractFee<ReceiveFee> {

    public ReceiveFee(FeeType feeType) {
        super(feeType);
        validate();
    }

    private void validate() {
        isTrue(FeeMovingDirection.MOVING_IN == getFeeType().getMovingDirection());
    }

    public static ReceiveFee createNewReceiveFee(BigMoney money,
                                                 FeeType feeType,
                                                 String accountId,
                                                 Date businessCreationTime,
                                                 Date dueDate) {
        hasLength(accountId);
        notNull(money);
        isTrue(money.isPositive());

        ReceiveFee receiveFee = new ReceiveFee(feeType);

        initializeFee(receiveFee, money, accountId, businessCreationTime, dueDate);
        return receiveFee;
    }
}