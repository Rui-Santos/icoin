package com.icoin.trading.fee.cash.interceptor;

import com.icoin.trading.fee.cash.Invocation;
import com.icoin.trading.fee.cash.InvocationContext;
import com.icoin.trading.fee.cash.ValidationCode;
import com.icoin.trading.fee.domain.DueDateService;
import com.icoin.trading.fee.domain.cash.Cash;
import com.icoin.trading.fee.domain.cash.CashRepository;
import org.joda.money.BigMoney;

import java.util.List;

import static com.homhon.util.Collections.isEmpty;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-27
 * Time: AM1:10
 * To change this template use File | Settings | File Templates.
 */
public class MaxAmountPerDayExucutingInterceptor extends ProfilingInterceptor {
    private CashRepository cashRepository;
    private DueDateService service;
    private BigMoney maxAmount = BigMoney.parse("BTC 5");

    @SuppressWarnings("unchecked")
    @Override
    protected ValidationCode doIntercept(Invocation invocation) throws Exception {
        final InvocationContext context = invocation.getInvocationContext();
        final List<Cash> list = cashRepository.findByUserId(context.getUserId(), service.computeDueDate(context.getOccurringTime()));

        if (isEmpty(list)) {
            return null;
        }

        BigMoney amount = null;

        for (Cash cash : list) {
            if (amount == null) {
                amount = cash.getAmount();
                continue;
            }

            amount = amount.plus(cash.getAmount());
        }

        return maxAmount.isLessThan(amount) ? ValidationCode.EXCEEDED_MAX_AMOUNT_PER_DAY : null;
    }

    public void setCashRepository(CashRepository cashRepository) {
        this.cashRepository = cashRepository;
    }

    public void setService(DueDateService service) {
        this.service = service;
    }

    public void setMaxAmount(BigMoney maxAmount) {
        this.maxAmount = maxAmount;
    }
}
