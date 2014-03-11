package com.icoin.trading.tradeengine.query.activity.repositories;

import com.icoin.trading.tradeengine.query.activity.PortfolioActivity;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 1/10/14
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PortfolioActivityRepositoryMongoCustom {
//    <S extends PortfolioActivity> S save(S entity);


//    <S extends PortfolioActivity> Iterable<S> save(Iterable<S> entities);

    <S extends PortfolioActivity> S save(S entity, Date currentTime);


    <S extends PortfolioActivity> Iterable<S> save(Iterable<S> entities, Date currentTime);
} 