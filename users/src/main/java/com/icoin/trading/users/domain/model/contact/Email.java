package com.icoin.trading.users.domain.model.contact;

import com.homhon.base.domain.model.VerifiedAwareValueObject;
import org.springframework.data.annotation.PersistenceConstructor;

import static com.homhon.util.Strings.hasLength;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-6-24
 * Time: PM8:48
 * To change this template use File | Settings | File Templates.
 */
public class Email extends VerifiedAwareValueObject<Email> {
    //email name format like ddd@xxx.com
    private String name;

    @PersistenceConstructor
    public Email(String name) {
        hasLength(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCompanySuffix() {
        if (!hasLength(name)) {
            return "";
        }

        int index = name.lastIndexOf("@");
        if (index == -1) {
            return "";
        }
        return name.substring(index);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Email email = (Email) o;

        return name.equals(email.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}