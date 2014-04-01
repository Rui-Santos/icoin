package com.icoin.trading.fee.cash.interceptor;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.icoin.trading.fee.cash.Invocation;
import com.icoin.trading.fee.cash.InvocationContext;
import com.icoin.trading.fee.cash.ValidationCode;
import com.icoin.trading.fee.domain.DueDateService;
import com.icoin.trading.fee.domain.cash.Cash;
import com.icoin.trading.fee.domain.cash.CashRepository;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.homhon.util.Collections.isEmpty;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-27
 * Time: AM1:10
 * To change this template use File | Settings | File Templates.
 */
public class MaxAmountPerDayExecutingInterceptor extends ProfilingInterceptor {
    private CashRepository cashRepository;
    private DueDateService service;
    private final Money maxAmount;

    public MaxAmountPerDayExecutingInterceptor(String currencyUnit, BigDecimal amount) {
        this.maxAmount = BigMoney.of(CurrencyUnit.of(currencyUnit), amount).toMoney(RoundingMode.HALF_EVEN);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ValidationCode doIntercept(Invocation invocation) {
        final InvocationContext context = invocation.getInvocationContext();
        final List<Cash> list = cashRepository.findByUserId(context.getUserId(), service.computeDueDate(context.getOccurringTime()));

        if (isEmpty(list)) {
            return null;
        }

        BigMoney amount = BigMoney.total(Lists.transform(list, new Function<Cash, BigMoney>() {
            @Override
            public BigMoney apply(Cash cash) {
                return cash.getAmount();
            }
        }));

        return maxAmount.isLessThan(amount) ? ValidationCode.EXCEEDED_MAX_AMOUNT_PER_DAY : null;
    }

    public void setCashRepository(CashRepository cashRepository) {
        this.cashRepository = cashRepository;
    }

    public void setService(DueDateService service) {
        this.service = service;
    }
}
