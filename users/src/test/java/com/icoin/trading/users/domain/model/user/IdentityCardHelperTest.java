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
        String invalidIdCard18 = "110101201101019253";
        IdentityCardHelper helper = IdentityCardHelper.INSTANCE;

        IdentityCard identity15Card = helper.createIdentityCard(idCard15);
        IdentityCard invalidIdentity15Card = helper.createIdentityCard(invalidIdCard15);
        IdentityCard identity18Card = helper.createIdentityCard(idCard18);
        IdentityCard invalidIdentity18Card = helper.createIdentityCard(invalidIdCard18);
        boolean valid15 = identity15Card.isValid();
        boolean invalid15 = invalidIdentity15Card.isValid();
        boolean valid18 = identity18Card.isValid();
        boolean invalid18 = invalidIdentity18Card.isValid();

        assertThat(valid15, is(true));
        assertThat(invalid15, is(false));
        assertThat(valid18, is(true));
        assertThat(invalid18, is(false));
    }
}
