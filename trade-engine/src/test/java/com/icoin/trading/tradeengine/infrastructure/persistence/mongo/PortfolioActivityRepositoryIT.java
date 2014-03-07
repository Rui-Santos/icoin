package com.icoin.trading.tradeengine.infrastructure.persistence.mongo;

import com.google.common.collect.ImmutableList;
import com.icoin.trading.tradeengine.query.activity.Activity;
import com.icoin.trading.tradeengine.query.activity.ActivityItem;
import com.icoin.trading.tradeengine.query.activity.PortfolioActivity;
import com.icoin.trading.tradeengine.query.activity.PortfolioActivityRepository;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.homhon.util.TimeUtils.currentTime;
import static com.homhon.util.TimeUtils.futureMinute;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/27/14
 * Time: 1:13 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration({"classpath:com/icoin/trading/tradeengine/infrastructure/persistence/mongo/tradeengine-persistence-mongo.xml"})
@SuppressWarnings("SpringJavaAutowiringInspection")
public class PortfolioActivityRepositoryIT {
    private PortfolioActivity activity1;
    private PortfolioActivity activity2;

    @Autowired
    private PortfolioActivityRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        repository.deleteAll();

        activity1 = create();

        repository.save(activity1);
    }

    private PortfolioActivity create() {
        PortfolioActivity activity = new PortfolioActivity();

        String userId = "userId";
        BigMoney amount = BigMoney.of(CurrencyUnit.EUR, 10);

        ImmutableList<ActivityItem> activityItems =
                ImmutableList.of(new ActivityItem(futureMinute(currentTime(), -2), userId, "45.23.98.243", "entity", "key1", amount),
                        new ActivityItem(futureMinute(currentTime(), -1), userId, "45.23.98.243", "entity", "key2", amount),
                        new ActivityItem(currentTime(), userId, "45.23.98.243", "entity", "key3", amount));

        activity.setWithdrawCoin(new Activity("withdrawCoins", 10, activityItems));
        activity.setAddCoin(new Activity("addCoins", 10, activityItems));

        return activity;
    }

    @Test
    public void testArraySave() throws Exception {
        BigMoney amount = BigMoney.of(CurrencyUnit.AUD, 10);
        PortfolioActivity one = repository.findOne(activity1.getPrimaryKey());

        Activity withdrawCoin = one.getWithdrawCoin();

        List<ActivityItem> activityItems = withdrawCoin.getActivityItems();
        assertThat(activityItems, hasSize(3));

        withdrawCoin.addItem(new ActivityItem(currentTime(), "userId", "45.23.98.243", "entity", "key2", amount));

        repository.save(one);

        one = repository.findOne(activity1.getPrimaryKey());

        withdrawCoin = one.getWithdrawCoin();

        activityItems = withdrawCoin.getActivityItems();
        assertThat(activityItems, hasSize(4));
    }

    @Test
    public void testArrayUpdate() throws Exception {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(activity1.getPrimaryKey()));

        query.fields().include("_id")
                .include("withdrawCoin");

        PortfolioActivity one = mongoTemplate.findOne(query, PortfolioActivity.class);

        Activity withdrawCoin = one.getWithdrawCoin();

        BigMoney amount = BigMoney.of(CurrencyUnit.AUD, 10);

        withdrawCoin.addItem(new ActivityItem(futureMinute(currentTime(), 1), "userId", "45.23.98.243", "entity", "key2",amount));
        withdrawCoin.addItem(new ActivityItem(futureMinute(currentTime(), 2), "userId", "45.23.98.243", "entity", "key2",amount));
        withdrawCoin.addItem(new ActivityItem(futureMinute(currentTime(), 3), "userId", "45.23.98.243", "entity", "key2",amount));
        withdrawCoin.addItem(new ActivityItem(futureMinute(currentTime(), 4), "userId", "45.23.98.243", "entity", "key2",amount));
        withdrawCoin.addItem(new ActivityItem(futureMinute(currentTime(), 5), "userId", "45.23.98.243", "entity", "key2",amount));
        withdrawCoin.addItem(new ActivityItem(futureMinute(currentTime(), 6), "userId", "45.23.98.243", "entity", "key2",amount));
        withdrawCoin.addItem(new ActivityItem(futureMinute(currentTime(), 7), "userId", "45.23.98.243", "entity", "key2",amount));
        withdrawCoin.addItem(new ActivityItem(futureMinute(currentTime(), 8), "userId", "45.23.98.243", "entity", "key2",amount));
        withdrawCoin.addItem(new ActivityItem(futureMinute(currentTime(), 9), "userId", "45.23.98.243", "entity", "key2",amount));
        withdrawCoin.addItem(new ActivityItem(futureMinute(currentTime(), 10), "userId", "45.23.98.243", "entity", "key2",amount));

        Update update = new Update();
        update.set("withdrawCoin", withdrawCoin);

        mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(activity1.getPrimaryKey())), update,PortfolioActivity.class);

        one = repository.findOne(activity1.getPrimaryKey());

        withdrawCoin = one.getWithdrawCoin();
    }
} 