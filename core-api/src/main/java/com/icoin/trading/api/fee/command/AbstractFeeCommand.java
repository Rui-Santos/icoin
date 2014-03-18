package com.icoin.trading.api.fee.command;

import com.homhon.base.domain.model.ValueObjectSupport;
import org.hibernate.validator.constraints.NotEmpty;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-8-24
 * Time: AM10:45
 * To change this template use File | Settings | File Templates.
 */
public class AbstractFeeCommand<T extends AbstractFeeCommand> extends ValueObjectSupport<T> {
    @NotEmpty
    private String transactionId;

    public AbstractFeeCommand(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }
}