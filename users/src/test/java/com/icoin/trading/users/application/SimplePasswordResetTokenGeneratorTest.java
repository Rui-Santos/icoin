package com.icoin.trading.users.application;

import org.junit.Test;

import java.util.Date;

import static com.homhon.util.TimeUtils.currentTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/18/14
 * Time: 4:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimplePasswordResetTokenGeneratorTest {
    @Test(timeout = 10000)
    public void testGenerate() throws Exception {
        SimplePasswordResetTokenGenerator generator = new SimplePasswordResetTokenGenerator();

        Date date = currentTime();
        String generated = generator.generate("userName", "ip", date);
        assertThat(generated, not(isEmptyOrNullString()));

        long time = date.getTime();
        int conflicted = 0;
        for (int i = 0; i < 50000; i++) {
            Date newDate = new Date(time);
            String token = generator.generate("userName", "ip", newDate);
            assertThat(token, not(isEmptyOrNullString()));
            if(generated.equalsIgnoreCase(token)){
                conflicted++;
            }
        }

        assertThat(conflicted, lessThan(10));
    }
}