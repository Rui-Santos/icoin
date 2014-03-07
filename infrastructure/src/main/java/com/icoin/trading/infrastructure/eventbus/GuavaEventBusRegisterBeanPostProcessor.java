package com.icoin.trading.infrastructure.eventbus;

import com.google.common.eventbus.EventBus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/5/14
 * Time: 6:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuavaEventBusRegisterBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {
    private EventBus eventBus;
    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean == null) {
            return bean;
        }

        if (bean.getClass().getCanonicalName().endsWith("GuavaListener")) {
            registerToEventBus(bean);
        }

        return bean;
    }

    private void registerToEventBus(Object bean) {
        eventBus = beanFactory.getBean(EventBus.class);
        this.eventBus.register(bean);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}