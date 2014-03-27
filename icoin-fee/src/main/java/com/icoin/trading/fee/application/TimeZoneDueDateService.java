package com.icoin.trading.fee.application;

import com.icoin.trading.fee.domain.DueDateService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Component;

import java.util.Date;
import static com.homhon.util.Asserts.*;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/26/14
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class TimeZoneDueDateService implements DueDateService {
    private DateTimeZone zone = DateTimeZone.forID("Asia/Chongqing");

    @Override
    public Date computeDueDate(Date occurringTime) {
        notNull(occurringTime);
        return new DateTime(occurringTime, zone).toLocalDate().toDate();
    }

    public void setZone(String zone) {
        this.zone = DateTimeZone.forID(zone);
    }
}