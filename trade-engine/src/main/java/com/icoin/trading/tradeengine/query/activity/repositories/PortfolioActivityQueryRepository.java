package com.icoin.trading.tradeengine.query.activity.repositories;

import com.homhon.base.domain.repository.GenericCrudRepository;
import com.icoin.trading.tradeengine.query.activity.PortfolioActivity;
import com.icoin.trading.tradeengine.query.activity.PortfolioActivityType;
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
public interface PortfolioActivityQueryRepository
        extends GenericCrudRepository<PortfolioActivity, String>,
        PortfolioActivityRepositoryMongoCustom,
        PagingAndSortingRepository<PortfolioActivity, String> {

    @Query(value = "{ 'username' : ?0, 'type' : ?1}")
    PortfolioActivity findByUsername(String username, PortfolioActivityType type);

    @Query(value = "{ 'portfolioId' : ?0, 'type' : ?1}")
    PortfolioActivity findByPortfolioId(String portfolioId, PortfolioActivityType type);

    PortfolioActivity save(PortfolioActivity entity, Date currentTime);


    List<PortfolioActivity> save(List<PortfolioActivity> entities, Date currentTime);
}
