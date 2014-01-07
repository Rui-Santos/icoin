package com.icoin.trading.users.domain.model.contact;

import com.homhon.base.domain.model.VerifiedAwareValueObject;
import org.springframework.data.annotation.PersistenceConstructor;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-6-24
 * Time: PM8:49
 * To change this template use File | Settings | File Templates.
 */
public class Phone extends VerifiedAwareValueObject<Phone> {
    private PhoneType phoneType;
    private String phoneNumber;
    private boolean verified;

    @PersistenceConstructor
    private Phone(PhoneType phoneType, String phoneNumber) {
        this.phoneType = phoneType;
        this.phoneNumber = phoneNumber;
    }

    public PhoneType getPhoneType() {
        return phoneType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Phone phone = (Phone) o;

        if (!phoneNumber.equals(phone.phoneNumber)) return false;
        if (phoneType != phone.phoneType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = phoneType.hashCode();
        result = 31 * result + phoneNumber.hashCode();
        return result;
    }

    public static Phone creatCellphone(String number) {
        return new Phone(PhoneType.CELL, number);
    }

    public static Phone creatLandline(String number) {
        return new Phone(PhoneType.LANDLINE, number);
    }
}