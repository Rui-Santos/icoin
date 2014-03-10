package com.icoin.trading.tradeengine.query.activity.repositories;

import com.homhon.base.domain.repository.GenericCrudRepository;
import com.icoin.trading.tradeengine.query.activity.ExecutedAlarmActivity;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/27/14
 * Time: 11:00 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ExecutedAlarmActivityQueryRepository
        extends GenericCrudRepository<ExecutedAlarmActivity, String>,
        PagingAndSortingRepository<ExecutedAlarmActivity, String> {
    ExecutedAlarmActivity findBySellUsername(String username);
    ExecutedAlarmActivity findByBuyUsername(String username);

    ExecutedAlarmActivity findBySellPortfolioId(String portfolioId);
    ExecutedAlarmActivity findByBuyPortfolioId(String portfolioId);

}