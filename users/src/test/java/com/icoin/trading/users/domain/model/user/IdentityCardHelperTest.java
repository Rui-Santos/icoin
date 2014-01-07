package com.icoin.trading.users.domain.model.user;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-1-5
 * Time: PM10:55
 * To change this template use File | Settings | File Templates.
 */
public class IdentityCardHelperTest {
    @Test
    public void testValidation() throws Exception {
        String idCard15 = "320311770706002";
        String invalidIdCard15 = "320311770732002";
        String idCard18 = "110101201101019252";
        String invalidIdcard18 = "110101201101019253";
        IdentityCardHelper helper = new IdentityCardHelper();

        boolean valid15 = helper.isValidate15Idcard(idCard15);
        boolean invalid15 = helper.isValidate15Idcard(invalidIdCard15);
        boolean valid18 = helper.isValidate18Idcard(idCard18);
        boolean invalid18 = helper.isValidate18Idcard(invalidIdcard18);
        boolean invalid18All = helper.isValidatedAllIdcard(invalidIdcard18);
        boolean valid15All = helper.isValidatedAllIdcard(idCard15);

        assertThat(valid15, is(true));
        assertThat(invalid15, is(false));
        assertThat(valid18, is(true));
        assertThat(invalid18, is(false));
        assertThat(invalid18All, is(false));
        assertThat(valid15All, is(true));
    }
}
