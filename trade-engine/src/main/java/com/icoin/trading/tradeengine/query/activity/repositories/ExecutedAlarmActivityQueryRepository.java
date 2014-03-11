package com.icoin.trading.tradeengine.query.activity.repositories;

import com.homhon.base.domain.repository.GenericCrudRepository;
import com.icoin.trading.tradeengine.query.activity.ExecutedAlarmActivity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

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

    @Query(value = "{ 'tradeTime' : { '$gte' : ?0, '$lt' : ?1}")
    ExecutedAlarmActivity findByTradeTime(Date start, Date end, Pageable pageable);

    ExecutedAlarmActivity findBySellUsername(String username, Pageable pageable);

    ExecutedAlarmActivity findByBuyUsername(String username, Pageable pageable);

    ExecutedAlarmActivity findBySellPortfolioId(String portfolioId, Pageable pageable);

    ExecutedAlarmActivity findByBuyPortfolioId(String portfolioId, Pageable pageable);

}