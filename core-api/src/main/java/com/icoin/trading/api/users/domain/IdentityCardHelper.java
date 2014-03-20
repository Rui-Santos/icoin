package com.icoin.trading.api.users.domain;

import com.homhon.util.Strings;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;

import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-1-5
 * Time: PM10:28
 * To change this template use File | Settings | File Templates.
 */
public class IdentityCardHelper {
    public static final String TWENTIETH_CENTURY = "19";

    // power 
    private int power[] = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    // 18th digit, verification code 
    private String verificationCodes[] = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};

    private IdentityCardHelper() {
    }

    public IdentityCard createIdentityCard(final String idNumber) {
        boolean valid = isIdCardValid(idNumber);
        if (!valid) {
            return new IdentityCard(idNumber, false, null, null, null, null, null);
        }

        String idCardNo = idNumber;

        if (idNumber.length() == 15) {
            idCardNo = convertIdCarTo18Digits(idNumber);
        }

        final ProvinceCode provinceCode = IDProvinceCodes.INSTANCE.get(idCardNo.substring(0, 2));
        final String gender = (idCardNo.charAt(16) % 2) == 0 ? "Female" : "Male";
        final String cityCode = idCardNo.substring(2, 4);
        final String townCode = idCardNo.substring(4, 6);
        final LocalDate birthDate = LocalDate.parse(idCardNo.substring(6, 14), ISODateTimeFormat.basicDate());

        return new IdentityCard(idNumber, true, provinceCode, cityCode, townCode, gender, birthDate);
    }

    /**
     * @param idCardNo id number
     * @return
     */
    private boolean isIdCardValid(final String idCardNo) {
        if (!Strings.hasLength(idCardNo)) {
            return false;
        }

        String idCard18Digits = idCardNo;

        if (idCardNo.length() == 15) {
            idCard18Digits = convertIdCarTo18Digits(idCardNo);
        }
        return isValidate18Idcard(idCard18Digits);
    }

    /**
     * @param idCard
     * @return
     */
    private boolean isValidate18Idcard(final String idCard) {
        if (idCard.length() != 18) {
            return false;
        }

        // extract the first 17 chars 
        final String idcard17 = idCard.substring(0, 17);

        // digit check 
        if (!isDigits(idcard17)) {
            return false;
        }

        // get province code 
        String provinceCode = idCard.substring(0, 2);
        ProvinceCode province = IDProvinceCodes.INSTANCE.get(provinceCode);
        if (province == null) {
            return false;
        }

        String lastChar = idCard.substring(17, 18);

        // calc the check sum code 
        final char[] chars = idcard17.toCharArray();
        final int[] bit = convertCharsToInt(chars);
        final int sum17 = getPowerSum(bit);
        final String checkCode = getCheckCodeBySum(sum17);

        // to check if last char is equal to the check code 
        if (!lastChar.equalsIgnoreCase(checkCode)) {
            return false;
        }

        return true;
    }

    /**
     * convert old 15-digit card number to 18-digit number
     *
     * @param idCard 15 digits
     * @return
     */
    private String convertIdCarTo18Digits(final String idCard) {
        if (idCard.length() != 15) {
            return "";
        }

        if (!isDigits(idCard)) {
            return "";
        }

        // get birthday 
        String birthday = idCard.substring(6, 12);
        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(TWENTIETH_CENTURY + birthday, ISODateTimeFormat.basicDate());
        } catch (Exception e) {
            return "";
        }

        final String year = String.valueOf(birthDate.getYearOfEra());

        final String idCard17 = idCard.substring(0, 6) + year + idCard.substring(8);

        final char c[] = idCard17.toCharArray();
        final int bit[] = convertCharsToInt(c);
        final int sum17 = getPowerSum(bit);

        // get check code 
        String checkCode = getCheckCodeBySum(sum17);

        //return idCard17 + checkCode 
        return idCard17 + checkCode;
    }

    /**
     * digit check
     *
     * @param idCard
     * @return
     */
    public boolean isIdcard(final String idCard) {
        return idCard == null || "".equals(idCard) ? false : Pattern.matches(
                "(^\\d{15}$)|(\\d{17}(?:\\d|x|X)$)", idCard);
    }

    /**
     * 15 digit card number check
     *
     * @param idCard
     * @return
     */
    public boolean is15Idcard(final String idCard) {
        return idCard == null || "".equals(idCard) ? false : Pattern.matches(
                "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$",
                idCard);
    }

    /**
     * 18 digit card number check
     *
     * @param idCard
     * @return
     */
    public boolean is18Idcard(final String idCard) {
        return Pattern
                .matches(
                        "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([\\d|x|X]{1})$",
                        idCard);
    }

    private static boolean isDigits(final String str) {
        if (!Strings.hasLength(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    /**
     * get verification int
     *
     * @param bit
     * @return
     */
    private int getPowerSum(final int[] bit) {

        int sum = 0;

        if (power.length != bit.length) {
            return sum;
        }

        for (int i = 0; i < bit.length; i++) {
            for (int j = 0; j < power.length; j++) {
                if (i == j) {
                    sum = sum + bit[i] * power[j];
                }
            }
        }
        return sum;
    }

    /**
     * sum%11
     *
     * @param sum17
     * @return checkCode
     */
    private String getCheckCodeBySum(final int sum17) {
        return verificationCodes[sum17 % 11];
    }

    /**
     * @param chars
     * @return
     * @throws NumberFormatException
     */
    private int[] convertCharsToInt(final char[] chars) throws NumberFormatException {
        int[] a = new int[chars.length];
        int k = 0;
        for (char temp : chars) {
            a[k++] = temp - '0';
        }
        return a;
    }

    public static IdentityCardHelper INSTANCE = new IdentityCardHelper();
} 