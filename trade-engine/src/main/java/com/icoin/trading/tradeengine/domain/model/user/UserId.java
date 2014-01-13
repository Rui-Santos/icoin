package com.icoin.trading.tradeengine.domain.model.user;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-11-25
 * Time: PM11:39
 * To change this template use File | Settings | File Templates.
 */

import org.axonframework.domain.IdentifierFactory;

import java.io.Serializable;

import static com.homhon.util.Asserts.notNull;

/**
 * @author Jettro Coenradie
 */
@Deprecated
public class UserId implements Serializable {
    private static final long serialVersionUID = -4860092244272266543L;

    private String identifier;

    public UserId() {
        this.identifier = IdentifierFactory.getInstance().generateIdentifier();
    }

    public UserId(String identifier) {
        notNull(identifier, "Identifier may not be null");
        this.identifier = identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserId userId = (UserId) o;

        return identifier.equals(userId.identifier);

    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public String toString() {
        return identifier;
    }
}