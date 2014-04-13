package com.icoin.trading.users.infrastructure.persistence.mongo;

import com.google.common.collect.Lists;
import com.icoin.trading.users.domain.model.function.UserPasswordReset;
import com.icoin.trading.users.domain.model.function.UserPasswordResetRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static com.homhon.util.TimeUtils.futureMinute;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-15
 * Time: AM8:56
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/spring/users-persistence-mongo.xml")
@ActiveProfiles("dev")
public class UserPasswordResetRepositoryTest {
    private final Date currentTime = new Date();
    private UserPasswordReset reset1;
    private UserPasswordReset reset2;
    private UserPasswordReset reset3;
    private UserPasswordReset reset4;
    private UserPasswordReset reset5;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserPasswordResetRepository repository;

    @Before
    public void setUp() throws Exception {
        repository.deleteAll();

        reset1 = create("12@12.cn", "120.90.98.1", "token1", "name1", futureMinute(currentTime, 30));
        reset2 = create("12@12.cn", "120.90.98.1", "token2", "name1", futureMinute(currentTime, 1));
        reset3 = create("12@12.cn", "120.90.98.1", "token3", "name2", futureMinute(currentTime, 100));
        reset4 = create("234567@12.cn", "120.90.98.1", "token4", "name2", futureMinute(currentTime, 100));
        reset5 = create("12@12.cn", "120.90.98.1", "token5", "name2", futureMinute(currentTime, 89));

        repository.save(Lists.newArrayList(reset1, reset2, reset3, reset4, reset5));

    }

    private UserPasswordReset create(String email,
                                     String ip,
                                     String token,
                                     String username,
                                     Date expirationDate) {

        final UserPasswordReset reset = new UserPasswordReset();

        reset.setEmail(email);
        reset.setIp(ip);
        reset.setToken(token);
        reset.setUsername(username);
        reset.setExpirationDate(expirationDate);

        return reset;
    }

    @Test
    public void testFindNotExpiredByEmail() {
        final List<UserPasswordReset> resets =
                repository.findNotExpiredByEmail(
                        reset1.getEmail(),
                        reset1.getIp(),
                        reset1.getExpirationDate(),
                        futureMinute(reset1.getExpirationDate(), 71));

        assertThat(resets, hasSize(3));

        assertThat(resets, hasItems(reset3, reset5, reset1));
    }

    @Test
    public void testFindByUsername() {
        final List<UserPasswordReset> resets =
                repository.findByUsername("name1");

        assertThat(resets, hasSize(2));
        assertThat(resets, hasItems(reset1, reset2));
    }

    @Test
    public void testFindByToken() {
        final UserPasswordReset reset =
                repository.findByToken("token4");

        assertThat(reset, equalTo(reset4));

        final UserPasswordReset notExisting =
                repository.findByToken("notExistingToken");

        assertThat(notExisting, nullValue());
    }
}
