package com.icoin.trading.users.domain.model.user;

import com.homhon.base.domain.model.ValueObjectSupport;

import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-1-5
 * Time: PM10:08
 * To change this template use File | Settings | File Templates.
 */
public class Identifier extends ValueObjectSupport<Identifier> {
    public static enum Type {
        IDENTITY_CARD {
            public boolean isValid(String number) {
                IdentityCard identityCard = IdentityCardHelper.INSTANCE.createIdentityCard(number);
                return identityCard.isValid();
            }
        };

        public boolean isValid(String number) {
            throw new UnsupportedOperationException("ID validation is not supported here");
        }
    }

    private Type type;
    private String number;

    public Identifier(Type type, String number) {
        notNull(type);
        hasLength(number);
        this.type = type;
        this.number = number;
    }

    public boolean isValid() {
        return type.isValid(number);
    }

    @Override
    public String toString() {
        return "Identifier{" +
                "type=" + type +
                ", number='" + number + '\'' +
                '}';
    }
} 
