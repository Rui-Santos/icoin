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

package com.icoin.trading.webui.dashboard;

import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import com.icoin.trading.tradeengine.query.transaction.TransactionEntry;
import com.icoin.trading.tradeengine.query.transaction.repositories.TransactionQueryRepository;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import com.icoin.trading.webui.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author Jettro Coenradie
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final static Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private PortfolioQueryRepository portfolioRepository;
    private TransactionQueryRepository transactionRepository;
    private UserQueryRepository userRepository;

    @RequestMapping(method = RequestMethod.GET)
    public String show(Model model) {
        String identifier = SecurityUtil.obtainLoggedinUserIdentifier();
        logger.debug("Requested to obtain the portfolio for the user: {}", identifier);

        PortfolioEntry portfolio = portfolioRepository.findByUserIdentifier(identifier);
        if (portfolio == null) {
            throw new RuntimeException("You most certainly changed the id of the current logged in user " +
                    "and the user did not logout.");
        }
        model.addAttribute("portfolio", portfolio);

        List<TransactionEntry> transactions = transactionRepository.findByPortfolioIdentifier(
                portfolio.getIdentifier(),
                new PageRequest(0, 10));
        model.addAttribute("transactions", transactions);

//        PagedListHolder pagedListHolder = new PagedListHolder(); // set total count, if < paged query, no,
// if>= paged query, search more;


//        List<OrderEntry> orders = orderRepository.findAllUserOrders(identifier, 0, 10);
//        model.addAttribute("orders", orders);

        final UserEntry userInfo = userRepository.findOne(identifier);
        model.addAttribute("userInfo", userInfo);
        return "dashboard/index";
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setUserRepository(UserQueryRepository userRepository) {
        this.userRepository = userRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setPortfolioRepository(PortfolioQueryRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setTransactionRepository(TransactionQueryRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
}
