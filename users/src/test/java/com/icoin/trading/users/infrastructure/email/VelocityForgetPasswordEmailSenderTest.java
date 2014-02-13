package com.icoin.trading.users.infrastructure.email;

import com.icoin.trading.users.domain.model.function.UserPasswordReset;
import com.icoin.trading.users.domain.model.function.UserPasswordResetRepository;
import com.icoin.trading.users.query.UserEntry;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/13/14
 * Time: 11:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class VelocityForgetPasswordEmailSenderTest {
    @Test
    public void test() throws Exception {
        final String token = "dijfid82348lkdfisofsudkldf92390;la0dvmva987fal";
//        VelocityEngine ve = new VelocityEngine();

//        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
//        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        VelocityEngineFactoryBean factoryBean = new VelocityEngineFactoryBean();
        factoryBean.setResourceLoaderPath("classpath:/com/icoin/trading/users/infrastructure/email");
        factoryBean.setPreferFileSystemAccess(false);

        /*
        * <bean id="velocityEngine" class="org.springframework.ui.velocity.VelocityEngineFactoryBean"
          p:resourceLoaderPath="classpath:/org/springbyexample/email"
          p:preferFileSystemAccess="false"/>
          */

        factoryBean.afterPropertiesSet();
        VelocityEngine ve = factoryBean.getObject();


        final UserPasswordReset reset = new UserPasswordReset();
        reset.setEmail("jihual@ms.com");
        reset.setUsername("liougehooa1");
        UserPasswordResetRepository userPasswordResetRepository = mock(UserPasswordResetRepository.class);
        when(userPasswordResetRepository.findByToken(eq(token))).thenReturn(reset);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.163.com");
        mailSender.setUsername("liougehooa1@163.com");
        mailSender.setPassword("");

        VelocityForgetPasswordEmailSender sender = new VelocityForgetPasswordEmailSender();
        sender.setVelocityEngine(ve);
        sender.setUserPasswordResetRepository(userPasswordResetRepository);

//        <bean id="templateMessage" class="org.springframework.mail.SimpleMailMessage"
//        p:from="dwinterfeldt@springbyexample.org"
//        p:to="${mail.recipient}"
//        p:subject="Greetings from Spring by Example" />

        sender.setMailSender(mailSender);
        sender.setFrom("liougehooa1@163.com");
        sender.setSubject("test");
        sender.setTemplateLocation("forgot-password.vm");

        UserEntry userAccount = new UserEntry();
        userAccount.setUsername("jihual");

        sender.sendEmail(token);
    }
} 