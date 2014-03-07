package com.icoin.trading.tradeengine.query.activity.repositories;

import com.homhon.base.domain.repository.GenericCrudRepository;
import com.icoin.trading.tradeengine.query.activity.ExecutedExceptionActivity;
import com.icoin.trading.tradeengine.query.activity.PortfolioActivity;
import com.icoin.trading.tradeengine.query.activity.PortfolioActivityType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/27/14
 * Time: 11:00 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ExecutedExceptionActivityQueryRepository
        extends GenericCrudRepository<ExecutedExceptionActivity, String>,
        PagingAndSortingRepository<ExecutedExceptionActivity, String> {
    PortfolioActivity findByUsername(String username, PortfolioActivityType type);

    PortfolioActivity findByPortfolioId(String portfolioId, PortfolioActivityType type);

    PortfolioActivity save(PortfolioActivity entity, Date currentTime);


    List<PortfolioActivity> save(List<PortfolioActivity> entities, Date currentTime);
} 