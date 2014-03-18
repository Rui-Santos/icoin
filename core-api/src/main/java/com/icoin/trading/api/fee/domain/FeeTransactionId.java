package com.icoin.trading.api.fee.domain;

import org.axonframework.domain.IdentifierFactory;

import java.io.Serializable;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-17
 * Time: PM9:35
 * To change this template use File | Settings | File Templates.
 */
public class FeeTransactionId implements Serializable {
    private static final long serialVersionUID = 4619605978908896567L;

    private final String identifier;
    private final int hashCode;

    public FeeTransactionId() {
        this.identifier = IdentifierFactory.getInstance().generateIdentifier();
        this.hashCode = identifier.hashCode();
    }

    public FeeTransactionId(String identifier) {
        notNull(identifier, "Identifier may not be null");
        this.identifier = identifier;
        this.hashCode = identifier.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeeTransactionId coinId = (FeeTransactionId) o;

        return identifier.equals(coinId.identifier);

    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return identifier;
    }
}
