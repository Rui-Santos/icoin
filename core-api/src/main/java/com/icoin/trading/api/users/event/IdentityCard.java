package com.icoin.trading.api.users.event;

import com.homhon.base.domain.model.ValueObjectSupport;
import org.joda.time.LocalDate;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 1/20/14
 * Time: 9:24 PM
 * To change this template use File | Settings | File Templates.
 */
public final class IdentityCard extends ValueObjectSupport<IdentityCard> {
    private final String idNumber;
    private final boolean valid;
    private final ProvinceCode provinceCode;
    private final String cityCode;
    private final String townCode;
    private final String gender;
    private final LocalDate birthday;

    public IdentityCard(final String idNumber,
                        final boolean valid,
                        final ProvinceCode provinceCode,
                        final String cityCode,
                        final String townCode,
                        final String gender,
                        final LocalDate birthday) {
        this.idNumber = idNumber;
        this.valid = valid;
        this.provinceCode = provinceCode;
        this.cityCode = cityCode;
        this.townCode = townCode;
        this.gender = gender;
        this.birthday = birthday;
    }

    public String getProvince() {
        if (!valid) {
            throw new UnsupportedOperationException("This card is invalid! you cannot get province");
        }

        return provinceCode.getProvinceName();
    }

    public int getYear() {
        if (!valid) {
            throw new UnsupportedOperationException("This card is invalid! you cannot get year");
        }
        return birthday.getYearOfEra();
    }

    public int getMonth() {
        if (!valid) {
            throw new UnsupportedOperationException("This card is invalid! you cannot get month");
        }
        return birthday.getMonthOfYear();
    }

    public int getDay() {
        if (!valid) {
            throw new UnsupportedOperationException("This card is invalid! you cannot get day");
        }
        return birthday.getDayOfMonth();
    }

    public String getGender() {
        if (!valid) {
            throw new UnsupportedOperationException("This card is invalid! you cannot get gender");
        }
        return gender;
    }

    public LocalDate getBirthday() {
        if (!valid) {
            throw new UnsupportedOperationException("This card is invalid! you cannot get birthday");
        }
        return birthday;
    }

    public String getProvinceCode() {
        if (!valid) {
            throw new UnsupportedOperationException("This card is invalid! you cannot get provinceCode");
        }
        return provinceCode.getProvinceCode();
    }

    public String getCityCode() {
        if (!valid) {
            throw new UnsupportedOperationException("This card is invalid! you cannot get cityCode");
        }
        return cityCode;
    }

    public String getTownCode() {
        if (!valid) {
            throw new UnsupportedOperationException("This card is invalid! you cannot get townCode");
        }
        return townCode;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public String toString() {
        if (!valid) {
            return "IdentityCard{" +
                    "idNumber='" + idNumber + '\'' +
                    ", valid=" + valid +
                    '}';
        }

        return "IdentityCard{" +
                "idNumber='" + idNumber + '\'' +
                ", valid=" + valid +
                ", provinceCode=" + provinceCode +
                ", cityCode='" + cityCode + '\'' +
                ", townCode='" + townCode + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}