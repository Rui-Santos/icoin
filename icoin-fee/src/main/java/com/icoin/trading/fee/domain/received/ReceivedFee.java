package com.icoin.trading.fee.domain.received;

import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.fee.domain.fee.FeeEntity;
import com.icoin.trading.fee.domain.fee.ReceiveFee;
import org.joda.money.BigMoney;
import org.springframework.data.annotation.TypeAlias;

import java.util.Date;

import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 4/10/13
 * Time: 3:14 PM
 */

@TypeAlias("fee.ReceivedFee")
public class ReceivedFee extends FeeEntity<ReceivedFee> {

    public ReceivedFee(ReceiveFee fee) {
        super(fee);
    }

    public static ReceivedFee createNewReceivedFee(FeeType feeType,
                                                   String accountId,
                                                   BigMoney money,
                                                   Date dueDate,
                                                   Date businessCreationTime) {
        hasLength(accountId);
        notNull(money);
        isTrue(money.isPositive());

        ReceiveFee receiveFee =
                ReceiveFee.createNewReceiveFee(
                        money,
                        feeType,
                        accountId,
                        businessCreationTime,
                        dueDate);

        ReceivedFee receivedFee = new ReceivedFee(receiveFee);
        return receivedFee;
    }
}