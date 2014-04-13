package com.icoin.trading.infrastructure.mail;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-15
 * Time: PM8:57
 * To change this template use File | Settings | File Templates.
 */
public class SimpleSpringVelocityEmailSenderIT {
    @Test
    public void test() throws Exception {
        //VelocityEngine
        VelocityEngineFactoryBean factoryBean = new VelocityEngineFactoryBean();
        factoryBean.setResourceLoaderPath("classpath:/com/icoin/trading/infrastructure/mail");
        factoryBean.setPreferFileSystemAccess(false);
        factoryBean.afterPropertiesSet();
        VelocityEngine ve = factoryBean.getObject();

        //JavaMailSender
        final String from = "testplus9@163.com";
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.163.com");
        mailSender.setUsername(from);
        mailSender.setPassword("testpassword");

        SimpleSpringVelocityEmailSender sender = new SimpleSpringVelocityEmailSender();
        sender.setMailSender(mailSender);
        sender.setVelocityEngine(ve);

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", "iCoin");

        sender.sendEmail("Test",
                "testplus8@163.com",
                from,
                "forgot-password.vm",
                "utf-8",
                model,
                true);
    }
}
