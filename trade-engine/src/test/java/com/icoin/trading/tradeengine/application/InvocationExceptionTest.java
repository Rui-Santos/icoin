package com.icoin.trading.tradeengine.application;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-1-3
 * Time: PM9:52
 * To change this template use File | Settings | File Templates.
 */
public class InvocationExceptionTest {
    @Test
    public void testException() throws Exception {
        boolean invoked = false;
        final Exception root = new Exception();
        try {
            throw new InvocationException("test", root);
        } catch (InvocationException e) {
            invoked = true;
            assertThat(e.getMessage(), equalTo("test"));
            assertThat(e.getCause(), equalTo(root));
            assertThat(e.getTargetException(), equalTo(root));
        }

        assertThat(invoked, is(true));
    }
}
