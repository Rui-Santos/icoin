package com.icoin.trading.tradeengine.query.activity.repositories;

import com.google.common.collect.Lists;
import com.icoin.trading.tradeengine.query.activity.ExecutedAlarmActivity;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static com.homhon.util.TimeUtils.currentTime;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/10/14
 * Time: 6:54 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration({"classpath:com/icoin/trading/tradeengine/infrastructure/persistence/mongo/tradeengine-persistence-mongo.xml"})
public class ExecutedAlarmActivityQueryRepositoryIT {
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private ExecutedAlarmActivityQueryRepository repository;

    private ExecutedAlarmActivity activity1, activity2, activity3, activity4;

    @Before
    public void setUp() throws Exception {
        repository.deleteAll();

        final Date tradeTime = currentTime();

        activity1 = create("buyUsername", "sellUsername", "buyPortfolioId", "sellPortfolioId", tradeTime);

        repository.save(Lists.newArrayList(activity1, activity2, activity3, activity4));
    }

    private ExecutedAlarmActivity create(String buyUsername, String sellUsername, String buyPortfolioId, String sellPortfolioId, Date tradeTime) {
        ExecutedAlarmActivity activity = new ExecutedAlarmActivity();
        activity.setBuyUsername(buyUsername);
        activity.setSellUsername(sellUsername);
        activity.setBuyPortfolioId(buyPortfolioId);
        activity.setSellPortfolioId(sellPortfolioId);
        activity.setTradeTime(tradeTime);


        return activity;
    }
}
