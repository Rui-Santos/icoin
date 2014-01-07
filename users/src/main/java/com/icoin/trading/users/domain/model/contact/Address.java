package com.icoin.trading.users.domain.model.contact;

import com.homhon.base.domain.model.ValueObjectSupport;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-6-24
 * Time: PM8:43
 * To change this template use File | Settings | File Templates.
 */
@TypeAlias("user.Address")
public class Address extends ValueObjectSupport<Address> {
    private String street;
    private String zipCode;
    private String city;
    private String country;

    /**
     * @param street
     * @param zipCode
     * @param city
     * @param country
     */
    @PersistenceConstructor
    public Address(String street, String zipCode, String city, String country) {
        this.street = street;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
    }

    /**
     * @return the street
     */
    public String getStreet() {
        return street;
    }

    /**
     * @param street the street to set
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * @return the zipCode
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * @param zipCode the zipCode to set
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }
}