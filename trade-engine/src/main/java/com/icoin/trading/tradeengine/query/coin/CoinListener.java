package com.icoin.trading.tradeengine.query.coin;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-11-24
 * Time: PM6:11
 * To change this template use File | Settings | File Templates.
 */

import com.icoin.trading.tradeengine.domain.events.coin.CoinCreatedEvent;
import com.icoin.trading.tradeengine.query.coin.repositories.CoinQueryRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Jettro Coenradie
 */
@Component
public class CoinListener {

    private CoinQueryRepository coinRepository;

    @EventHandler
    public void handleCoinCreatedEvent(CoinCreatedEvent event) {
        CoinEntry coinEntry = new CoinEntry();
        coinEntry.setPrimaryKey(event.getCoinIdentifier().toString());
        coinEntry.setCoinPrice(event.getCoinInitialPrice());
        coinEntry.setCoinAmount(event.getCoinInitialAmount());
        coinEntry.setTradeStarted(true);
        coinEntry.setName(event.getCoinName());

        coinRepository.save(coinEntry);
    }

    @Autowired
    public void setCoinRepository(CoinQueryRepository coinRepository) {
        this.coinRepository = coinRepository;
    }
}