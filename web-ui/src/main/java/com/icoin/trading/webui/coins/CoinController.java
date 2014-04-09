/*
 * Copyright (c) 2010-2012. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icoin.trading.webui.coins;

import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.fee.application.TransferCoinService;
import com.icoin.trading.fee.domain.address.Address;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import com.icoin.trading.users.domain.model.user.UserAccount;
import com.icoin.trading.webui.user.facade.UserServiceFacade;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;

import static com.homhon.util.TimeUtils.currentTime;

/**
 * @author Jettro Coenradie
 */
@Controller
@RequestMapping("/coin")
public class CoinController {
    private static Logger logger = LoggerFactory.getLogger(CoinController.class);
    private TransferCoinService transferCoinService;
    private UserServiceFacade userService;
    private PortfolioQueryRepository portfolioRepository;

    @RequestMapping(value = "/add/{coin}", method = RequestMethod.GET)
    public String add(@PathVariable("coin") String coin, BindingResult bindingResult, Model model) {
        if (!"BTC".equalsIgnoreCase(coin)) {
            bindingResult.reject("error.currency.notSupported", "Currency not supported!");
            return "transferCoin/failed";
        }
        UserAccount user = userService.currentUser();
        if (user == null) {
            bindingResult.reject("error.user.notloggedon", "User not logged on, please log on first!");
            return "transferCoin/failed";
        }

        Address address = transferCoinService.getAddress(user);

        model.addAttribute("address", address.getAddress());
        return "transferCoin/add/" + coin.toLowerCase();
    }

    @RequestMapping(value = "/withdraw/{coin}", method = RequestMethod.GET)
    public String withdraw(@PathVariable("coin") String coin, BindingResult bindingResult) {
        if (!"BTC".equalsIgnoreCase(coin)) {
            bindingResult.reject("error.currency.notSupported", "Currency not supported!");
            return "transferCoin/failed";
        }
        UserAccount user = userService.currentUser();
        if (user == null) {
            bindingResult.reject("error.user.notloggedon", "User not logged on, please log on first!");
            return "transferCoin/failed";
        }

        return "transferCoin/withdraw/" + coin.toLowerCase();
    }


    @RequestMapping(value = "/withdraw/{coin}", method = RequestMethod.POST)
    public String withdraw(@PathVariable("coin") String coin, BindingResult bindingResult, @ModelAttribute BigDecimal amount) {
        if (!"BTC".equalsIgnoreCase(coin)) {
            bindingResult.reject("error.currency.notSupported", "Currency not supported!");
            return "transferCoin/failed";
        }

        UserAccount user = userService.currentUser();
        if (user == null) {
            bindingResult.reject("error.user.notloggedon", "User not logged on, please log on first!");
            return "transferCoin/failed";
        }

        if (amount == null || amount.compareTo(BigDecimal.valueOf(0.01)) <= 0) {
            bindingResult.reject("error.withraw.amount.incorrect", "Amount should be greater than 0.01!");
            return "transferCoin/failed";
        }

        PortfolioEntry portfolio = getPortfolio( user);
        if (portfolio == null) {
            logger.error("Cannot find portfolio via user account {}", user.getPrimaryKey());
            return null;
        }

        transferCoinService.withdrawCoin(BigMoney.of(CurrencyUnit.of(coin.toUpperCase()), amount), user,
                new PortfolioId(portfolio.getPrimaryKey()), currentTime());

        return "transferCoin/withdraw/wait";
    }

    private PortfolioEntry getPortfolio(UserAccount user){
        return portfolioRepository.findByUserIdentifier(user.getPrimaryKey());
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setTransferCoinService(TransferCoinService transferCoinService) {
        this.transferCoinService = transferCoinService;
    }

    @Autowired
    public void setUserService(UserServiceFacade userService) {
        this.userService = userService;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setPortfolioRepository(PortfolioQueryRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }
}
