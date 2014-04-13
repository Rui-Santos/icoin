package com.icoin.trading.tradeengine.application;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-1-3
 * Time: PM9:42
 * To change this template use File | Settings | File Templates.
 */
public class SynchronizedOnIdentifierHandlerTest {
    @Test
    public void testPerform() throws Exception {
        final SynchronizedOnIdentifierHandler handler = new SynchronizedOnIdentifierHandler();


        final Boolean performed = handler.perform(new Callback<Boolean>() {
            @Override
            public String getIdentifier() {
                return "1";
            }

            @Override
            public Boolean execute() throws Exception {
                return true;
            }
        });

        assertThat(performed, is(true));
    }

    @Test(expected = InvocationException.class)
    public void testPerformException() throws Exception {
        final SynchronizedOnIdentifierHandler handler = new SynchronizedOnIdentifierHandler();


        handler.perform(new Callback<Void>() {
            @Override
            public String getIdentifier() {
                return "1";
            }

            @Override
            public Void execute() throws Exception {
                throw new Exception();
            }
        });
    }
}
