package com.icoin.trading.users.domain.model.user;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 1/20/14
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class IDProvinceCodes {
    private final static Map<String, ProvinceCode> cityCodes;

    static {
        Map<String, ProvinceCode> cityCodeSets = new HashMap<String, ProvinceCode>();

        cityCodeSets.put("11", new ProvinceCode("11", "Beijing"));
        cityCodeSets.put("12", new ProvinceCode("12", "Tianjin"));
        cityCodeSets.put("13", new ProvinceCode("13", "Hebei"));
        cityCodeSets.put("14", new ProvinceCode("14", "Shanxi"));
        cityCodeSets.put("15", new ProvinceCode("15", "Neimenggu"));
        cityCodeSets.put("21", new ProvinceCode("21", "Liaoning"));
        cityCodeSets.put("22", new ProvinceCode("22", "Jilin"));
        cityCodeSets.put("23", new ProvinceCode("23", "Heilongjiang"));
        cityCodeSets.put("31", new ProvinceCode("31", "Shanghai"));
        cityCodeSets.put("32", new ProvinceCode("32", "Jiangsu"));
        cityCodeSets.put("33", new ProvinceCode("33", "Zhejiang"));
        cityCodeSets.put("34", new ProvinceCode("34", "Anhui"));
        cityCodeSets.put("35", new ProvinceCode("35", "Fujian"));
        cityCodeSets.put("36", new ProvinceCode("36", "Jiangxi"));
        cityCodeSets.put("37", new ProvinceCode("37", "Shandong"));
        cityCodeSets.put("41", new ProvinceCode("41", "Henan"));
        cityCodeSets.put("42", new ProvinceCode("42", "Hubei"));
        cityCodeSets.put("43", new ProvinceCode("43", "Hunan"));
        cityCodeSets.put("44", new ProvinceCode("44", "Guangdong"));
        cityCodeSets.put("45", new ProvinceCode("45", "Guangxi"));
        cityCodeSets.put("46", new ProvinceCode("46", "Hainan"));
        cityCodeSets.put("50", new ProvinceCode("50", "Chongqing"));
        cityCodeSets.put("51", new ProvinceCode("51", "Sichuan"));
        cityCodeSets.put("52", new ProvinceCode("52", "Guizhou"));
        cityCodeSets.put("53", new ProvinceCode("53", "Yunnan"));
        cityCodeSets.put("54", new ProvinceCode("54", "Tibet"));
        cityCodeSets.put("61", new ProvinceCode("61", "Shanxi"));
        cityCodeSets.put("62", new ProvinceCode("62", "Gansu"));
        cityCodeSets.put("63", new ProvinceCode("63", "Qinghai"));
        cityCodeSets.put("64", new ProvinceCode("64", "Ningxia"));
        cityCodeSets.put("65", new ProvinceCode("65", "Xinjiang"));
        cityCodeSets.put("71", new ProvinceCode("71", "Taiwan"));
        cityCodeSets.put("81", new ProvinceCode("81", "Hongkong"));
        cityCodeSets.put("82", new ProvinceCode("82", "Macao"));
        cityCodeSets.put("91", new ProvinceCode("91", "Foreign"));

        cityCodes = Collections.unmodifiableMap(cityCodeSets);
    }

    public boolean contains(String provinceCode) {
        return cityCodes.containsKey(provinceCode);
    }

    public ProvinceCode get(String provinceCode) {
        return cityCodes.get(provinceCode);
    }

    private IDProvinceCodes() {
    }

    static IDProvinceCodes INSTANCE = new IDProvinceCodes();
} 