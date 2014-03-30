package com.icoin.trading.fee.cash.interceptor;

import com.icoin.trading.fee.domain.cash.CashAdmin;
import com.icoin.trading.fee.domain.cash.CashAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-29
 * Time: AM9:18
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractAdminControlInterceptor extends ProfilingInterceptor {
    protected CashAdminRepository repository;

    protected CashAdmin retrieve() {
        final Iterable<CashAdmin> all = repository.findAll();

        if (all == null || !all.iterator().hasNext()) {
            return null;
        }

        return all.iterator().next();
    }

    @Autowired
    public void setRepository(CashAdminRepository repository) {
        this.repository = repository;
    }
}
