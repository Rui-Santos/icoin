package com.icoin.trading.fee.cash.interceptor;

import com.icoin.trading.fee.cash.Invocation;
import com.icoin.trading.fee.cash.InvocationContext;
import com.icoin.trading.fee.cash.ValidationCode;
import com.icoin.trading.fee.domain.DueDateService;
import com.icoin.trading.fee.domain.cash.Cash;
import com.icoin.trading.fee.domain.cash.CashRepository;

import java.util.List;

import static com.homhon.util.Collections.isEmpty;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-27
 * Time: AM1:10
 * To change this template use File | Settings | File Templates.
 */
public class MaxTimesPerDayInterceptor extends ProfilingInterceptor {
    private CashRepository cashRepository;
    private DueDateService service;
    private int maxTime = 3;

    @SuppressWarnings("unchecked")
    @Override
    protected ValidationCode doIntercept(Invocation invocation) {
        final InvocationContext context = invocation.getInvocationContext();
        final List<Cash> list = cashRepository.findByUserId(context.getUserId(), service.computeDueDate(context.getOccurringTime()));

        if (isEmpty(list) || list.size() <= maxTime) {
            return null;
        }


        return ValidationCode.EXCEEDED_MAX_TIMES_PER_DAY;
    }

    public void setCashRepository(CashRepository cashRepository) {
        this.cashRepository = cashRepository;
    }

    public void setService(DueDateService service) {
        this.service = service;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }
}
