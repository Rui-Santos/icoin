package com.icoin.trading.api.users.event;

import com.homhon.base.domain.model.ValueObjectSupport;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 1/20/14
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
class ProvinceCode extends ValueObjectSupport<ProvinceCode> {
    private final String provinceCode;
    private final String provinceName;
    private final int hashCode;

    public ProvinceCode(String provinceCode, String provinceName) {
        this.provinceCode = provinceCode;
        this.provinceName = provinceName;
        hashCode = Integer.valueOf(provinceCode);
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProvinceCode provinceCode = (ProvinceCode) o;

        if (!this.provinceCode.equals(provinceCode.provinceCode)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return "ProvinceCode{" +
                "provinceCode='" + provinceCode + '\'' +
                ", provinceName='" + provinceName + '\'' +
                '}';
    }
} 