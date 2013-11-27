package com.icoin.trading.tradeengine.query.coin.repositories;

import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-11-24
 * Time: PM6:13
 * To change this template use File | Settings | File Templates.
 */
public interface CoinQueryRepository extends PagingAndSortingRepository<CoinEntry, String> {
}
