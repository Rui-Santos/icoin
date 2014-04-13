package com.icoin.trading.fee.cash.interceptor;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.icoin.trading.fee.cash.Invocation;
import com.icoin.trading.fee.cash.ValidationCode;
import com.icoin.trading.fee.query.fee.payable.AccountPayableFeeEntry;
import com.icoin.trading.fee.query.fee.receivable.AccountReceivableFeeEntry;
import com.icoin.trading.fee.query.fee.payable.AccountPayableFeeEntryQueryRepository;
import com.icoin.trading.fee.query.fee.receivable.AccountReceivableFeeEntryQueryRepository;
import org.joda.money.BigMoney;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-7
 * Time: PM1:29
 * To change this template use File | Settings | File Templates.
 */
public class WithdrawReceivablePayableInterceptor extends ProfilingInterceptor {
    private AccountPayableFeeEntryQueryRepository payableFeeRepository;
    private AccountReceivableFeeEntryQueryRepository receivableFeeRepository;

    @Override
    protected ValidationCode doIntercept(Invocation invocation) {
        List<AccountReceivableFeeEntry> receivableFeeEntries = receivableFeeRepository.findConfirmedByUserAccountId(invocation.getInvocationContext().getPortfolioId());
        List<AccountPayableFeeEntry> payableFeeEntries = payableFeeRepository.findConfirmedByUserAccountId(invocation.getInvocationContext().getPortfolioId());

        BigMoney receivable = BigMoney.total(Lists.transform(receivableFeeEntries, new Function<AccountReceivableFeeEntry, BigMoney>() {
            @Override
            public BigMoney apply(AccountReceivableFeeEntry input) {
                return input.getAmount();
            }
        }));

        BigMoney payable = BigMoney.total(Lists.transform(receivableFeeEntries, new Function<AccountReceivableFeeEntry, BigMoney>() {
            @Override
            public BigMoney apply(AccountReceivableFeeEntry input) {
                return input.getAmount();
            }
        }));

        if (receivable.isLessThan(payable)) {
            return ValidationCode.INSUFFICIENT_COIN;
        }
        return null;
    }
}
