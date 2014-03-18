package com.icoin.trading.api.fee.domain.offset;

import org.axonframework.domain.IdentifierFactory;

import java.io.Serializable;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM7:30
 * To change this template use File | Settings | File Templates.
 */
public class OffsetId implements Serializable {
    private static final long serialVersionUID = 8861161787763752022L;

    private final String identifier;
    private final int hashCode;

    public OffsetId() {
        this.identifier = IdentifierFactory.getInstance().generateIdentifier();
        this.hashCode = identifier.hashCode();
    }

    public OffsetId(String identifier) {
        notNull(identifier, "Identifier may not be null");
        this.identifier = identifier;
        this.hashCode = identifier.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OffsetId coinId = (OffsetId) o;

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
