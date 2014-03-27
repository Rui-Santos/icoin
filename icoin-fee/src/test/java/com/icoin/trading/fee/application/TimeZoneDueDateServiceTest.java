package com.icoin.trading.fee.application;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.util.Date;
import java.util.TimeZone;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-27
 * Time: AM1:34
 * To change this template use File | Settings | File Templates.
 */
public class TimeZoneDueDateServiceTest {
    @Test
    public void testComputeDueDate() throws Exception {
        final String zone = "America/New_York";
        DateTime dateTime = DateTime.now(DateTimeZone.forTimeZone(TimeZone.getTimeZone(zone)));

        final TimeZoneDueDateService service = new TimeZoneDueDateService();
        service.setZone(zone);

        final Date date = service.computeDueDate(dateTime.toDate());

        assertThat(date, equalTo(dateTime.toLocalDate().toDate()));
    }
}
