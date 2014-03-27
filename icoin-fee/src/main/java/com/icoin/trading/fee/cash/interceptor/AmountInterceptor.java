package com.icoin.trading.fee.cash.interceptor;

import com.icoin.trading.fee.cash.Invocation;
import com.icoin.trading.fee.cash.InvocationContext;
import com.icoin.trading.fee.cash.ValidationCode;
import org.joda.money.BigMoney;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-27
 * Time: AM1:10
 * To change this template use File | Settings | File Templates.
 */
public class AmountInterceptor extends ProfilingInterceptor {
    private BigMoney maxAmount = BigMoney.parse("BTC 5");
    private BigMoney minAmount = BigMoney.parse("BTC 0.01");

    @SuppressWarnings("unchecked")
    @Override
    protected ValidationCode doIntercept(Invocation invocation) throws Exception {
        final InvocationContext context = invocation.getInvocationContext();

        ValidationCode validationCode = maxAmount.isLessThan(context.getAmount()) ? ValidationCode.EXCEEDED_MAX_AMOUNT_PER_TIME : null;
        if (validationCode != null) {
            return validationCode;
        }

        validationCode = minAmount.isGreaterThan(context.getAmount()) ? ValidationCode.EXCEEDED_MIN_AMOUNT_PER_TIME : null;
        return validationCode;
    }


    public void setMaxAmount(BigMoney maxAmount) {
        this.maxAmount = maxAmount;
    }
}
