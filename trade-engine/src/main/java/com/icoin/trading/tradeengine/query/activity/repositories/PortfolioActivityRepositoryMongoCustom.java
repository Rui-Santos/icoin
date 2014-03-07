package com.icoin.trading.tradeengine.query.activity.repositories;

import com.icoin.trading.tradeengine.query.activity.PortfolioActivity;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 1/10/14
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PortfolioActivityRepositoryMongoCustom {
    PortfolioActivity save(PortfolioActivity entity);


    List<PortfolioActivity> save(List<PortfolioActivity> entities);

    PortfolioActivity save(PortfolioActivity entity, Date currentTime);


    List<PortfolioActivity> save(List<PortfolioActivity> entities, Date currentTime);
} 