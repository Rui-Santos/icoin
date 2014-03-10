package com.icoin.trading.tradeengine.query.activity.repositories;

import com.homhon.base.domain.repository.GenericCrudRepository;
import com.icoin.trading.tradeengine.query.activity.PortfolioActivity;
import com.icoin.trading.tradeengine.query.activity.PortfolioActivityType;
import com.icoin.trading.tradeengine.query.activity.PortfolioAlarmActivity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 1/10/14
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PortfolioAlarmQueryRepository
        extends GenericCrudRepository<PortfolioAlarmActivity, String>,
        PagingAndSortingRepository<PortfolioAlarmActivity, String> {

    @Query(value = "{ 'username' : ?0, 'type' : ?1}")
    PortfolioActivity findByUsername(String username, PortfolioActivityType type);

    @Query(value = "{ 'portfolioId' : ?0, 'type' : ?1}")
    PortfolioActivity findByPortfolioId(String portfolioId, PortfolioActivityType type);
}
