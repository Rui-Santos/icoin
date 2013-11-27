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

    private CoinQueryRepository companyRepository;

    @EventHandler
    public void handleCoinCreatedEvent(CoinCreatedEvent event) {
        CoinEntry companyEntry = new CoinEntry();
        companyEntry.setIdentifier(event.getCoinIdentifier().toString());
        companyEntry.setCoinInitialPrice(event.getCoinInitialPrice());
        companyEntry.setCoinInitialAmount(event.getCoinInitialAmount());
        companyEntry.setTradeStarted(true);
        companyEntry.setName(event.getCoinName());

        companyRepository.save(companyEntry);
    }

    @Autowired
    public void setCoinRepository(CoinQueryRepository companyRepository) {
        this.companyRepository = companyRepository;
    }
}