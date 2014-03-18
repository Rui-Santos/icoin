package com.icoin.trading.api.fee.domain.fee;

import org.axonframework.domain.IdentifierFactory;

import java.io.Serializable;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-17
 * Time: PM9:33
 * To change this template use File | Settings | File Templates.
 */
public class FeeId implements Serializable {
    private static final long serialVersionUID = 4619605978908896567L;

    private final String identifier;
    private final int hashCode;

    public FeeId() {
        this.identifier = IdentifierFactory.getInstance().generateIdentifier();
        this.hashCode = identifier.hashCode();
    }

    public FeeId(String identifier) {
        notNull(identifier, "Identifier may not be null");
        this.identifier = identifier;
        this.hashCode = identifier.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeeId coinId = (FeeId) o;

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
