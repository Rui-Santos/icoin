package com.icoin.trading.users.infrastructure.email;

import com.icoin.trading.infrastructure.mail.VelocityEmailSender;
import com.icoin.trading.users.domain.model.function.UserPasswordReset;
import com.icoin.trading.users.domain.model.function.UserPasswordResetRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/users-persistence-mongo.xml",
        "classpath:META-INF/spring/users-external.xml"
})
@ActiveProfiles("dev")
public class VelocityForgetPasswordEmailSenderIT {
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private VelocityEmailSender sender;
//    @Test
//    public void test() throws Exception {
//        final String token = "dijfid82348lkdfisofsudkldf92390;la0dvmva987fal";
////        VelocityEngine ve = new VelocityEngine();
//
////        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
////        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
//
//        VelocityEngineFactoryBean factoryBean = new VelocityEngineFactoryBean();
//        factoryBean.setResourceLoaderPath("classpath:/com/icoin/trading/users/infrastructure/email");
//        factoryBean.setPreferFileSystemAccess(false);
//
//        /*
//        * <bean id="velocityEngine" class="org.springframework.ui.velocity.VelocityEngineFactoryBean"
//          p:resourceLoaderPath="classpath:/org/springbyexample/email"
//          p:preferFileSystemAccess="false"/>
//          */
//
//        factoryBean.afterPropertiesSet();
//        VelocityEngine ve = factoryBean.getObject();
//
//
//        final UserPasswordReset reset = new UserPasswordReset();
//        reset.setEmail("testplus8@163.com");
//        reset.setUsername("test user");
//        UserPasswordResetRepository userPasswordResetRepository = mock(UserPasswordResetRepository.class);
//        when(userPasswordResetRepository.findByToken(eq(token))).thenReturn(reset);
//
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost("smtp.163.com");
//        mailSender.setUsername("testplus9@163.com");
//        mailSender.setPassword("testpassword");
//
//        VelocityForgetPasswordEmailSender sender = new VelocityForgetPasswordEmailSender();
//        sender.setVelocityEngine(ve);
//        sender.setUserPasswordResetRepository(userPasswordResetRepository);
//
////        <bean id="templateMessage" class="org.springframework.mail.SimpleMailMessage"
////        p:from="dwinterfeldt@springbyexample.org"
////        p:to="${mail.recipient}"
////        p:subject="Greetings from Spring by Example" />
//
//        sender.setMailSender(mailSender);
//        sender.setFrom("testplus9@163.com");
//        sender.setSubject("test");
//        sender.setTemplateLocation("forgot-password.vm");
//
//        UserEntry userAccount = new UserEntry();
//        userAccount.setUsername("jihual");
//
//        sender.sendEmail(token);
//    }


    //todo add unit test
    @Ignore
    @Test
    public void test() throws Exception {
        final String token = "dijfid82348lkdfisofsudkldf92390;la0dvmva987fal";
        final UserPasswordReset reset = new UserPasswordReset();
        reset.setEmail("testplus8@163.com");
        reset.setUsername("test user");

        UserPasswordResetRepository userPasswordResetRepository = mock(UserPasswordResetRepository.class);
        when(userPasswordResetRepository.findByToken(eq(token))).thenReturn(reset);

        final VelocityForgetPasswordEmailSender emailSender = new VelocityForgetPasswordEmailSender();

        emailSender.setUserPasswordResetRepository(userPasswordResetRepository);
        emailSender.setTemplateLocation("email/forgot-password.vm");
        emailSender.setSubject("forgot password");
        emailSender.setFrom("testplus9@163.com");
        emailSender.setDomainUrl("http://localhost:8080");
        emailSender.setSender(sender);

        emailSender.sendEmail(token);
    }
} 