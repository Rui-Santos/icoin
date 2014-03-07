package com.icoin.trading.tradeengine.query.activity.repositories;

import com.icoin.trading.tradeengine.query.activity.Activity;
import com.icoin.trading.tradeengine.query.activity.PortfolioActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.homhon.util.Asserts.notNull;
import static com.homhon.util.TimeUtils.currentTime;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 1/10/14
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class PortfolioActivityRepositoryMongoImpl implements PortfolioActivityRepositoryMongoCustom {
    private static Logger logger = LoggerFactory.getLogger(PortfolioActivityRepositoryMongoImpl.class);

    private MongoTemplate mongoTemplate;

    @Resource(name = "trade.mongoTemplate")
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public PortfolioActivity save(PortfolioActivity entity) {
        return save(entity, currentTime());
    }

    @Override
    public List<PortfolioActivity> save(List<PortfolioActivity> entities) {
        return save(entities, currentTime());
    }

    @Override
    public PortfolioActivity save(PortfolioActivity entity, Date currentTime) {
        notNull(entity, "The given entity cannot not be null!");
        notNull(currentTime, "The given currentTime cannot be null!");

        Activity activity = entity.getActivity();
        if (activity != null) {
            activity.archiveIfNecessary(currentTime);
        }

        mongoTemplate.save(entity);
        return entity;
    }

    @Override
    public List<PortfolioActivity> save(List<PortfolioActivity> entities, Date currentTime) {
        notNull(entities, "The given entities cannot not be null!");
        notNull(currentTime, "The given currentTime cannot be null!");

        List<PortfolioActivity> result = new ArrayList<PortfolioActivity>();

        for (PortfolioActivity entity : entities) {
            save(entity, currentTime);
            result.add(entity);
        }

        return result;
    }
} 