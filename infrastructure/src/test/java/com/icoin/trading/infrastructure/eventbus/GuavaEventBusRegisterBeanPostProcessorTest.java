package com.icoin.trading.infrastructure.eventbus;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-7
 * Time: AM1:02
 * To change this template use File | Settings | File Templates.
 */
public class GuavaEventBusRegisterBeanPostProcessorTest {
    @Test
    public void test() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GuavaEventBusRegisterBeanPostProcessor guavaEventBusRegisterBeanPostProcessor = new GuavaEventBusRegisterBeanPostProcessor();
        guavaEventBusRegisterBeanPostProcessor.setBeanFactory(bf);
        bf.addBeanPostProcessor(guavaEventBusRegisterBeanPostProcessor);

        bf.registerBeanDefinition("guavaListener", new RootBeanDefinition(TestGuavaListener.class));
        bf.registerBeanDefinition("eventBus", new RootBeanDefinition(EventBus.class));


        EventBus eventBus = (EventBus) bf.getBean("eventBus");
        TestGuavaListener listener = (TestGuavaListener) bf.getBean("guavaListener");

        final int event = 120320;
        eventBus.post(event);

        assertThat(listener.getNumber(), is(event));
    }

    private static class TestGuavaListener {
        private int number;

        @Subscribe
        public void handle(Integer event) {
            number = event;
        }

        private int getNumber() {
            return number;
        }
    }
}
