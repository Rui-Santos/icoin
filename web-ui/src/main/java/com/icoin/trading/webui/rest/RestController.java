/*
 * Copyright (c) 2012. Axon Framework
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

package com.icoin.trading.webui.rest;

import com.icoin.trading.tradeengine.infrastructure.persistence.mongo.converters.JodaMoneyConverter;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import com.thoughtworks.xstream.XStream;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.commandhandling.StructuralCommandValidationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

/**
 * Very generic controller supporting the sending of commands in an XStream serialized format. This controller also
 * contains a few methods to obtain data in XStream format.
 *
 * @author Jettro Coenradie
 */
@Controller
@RequestMapping("/rest")
public class RestController {

    private static final Logger logger = LoggerFactory.getLogger(RestController.class);
    private CommandBus commandBus;
    private PortfolioQueryRepository portfolioQueryRepository;
    private OrderBookQueryRepository orderBookQueryRepository;

    private XStream xStream;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public RestController(CommandBus commandBus, PortfolioQueryRepository portfolioQueryRepository,
                          OrderBookQueryRepository orderBookQueryRepository) {
        this.portfolioQueryRepository = portfolioQueryRepository;
        this.orderBookQueryRepository = orderBookQueryRepository;
        this.xStream = new XStream();
        this.xStream.registerConverter(new JodaMoneyConverter());
        this.commandBus = commandBus;
    }

    @RequestMapping(value = "/command", method = RequestMethod.POST)
    public
    @ResponseBody
    String mappedCommand(String command, HttpServletResponse response) throws IOException {
        try {
            Object actualCommand = xStream.fromXML(command);
            commandBus.dispatch(new GenericCommandMessage<Object>(actualCommand));
        } catch (StructuralCommandValidationFailedException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "This is an invalid request.");
        } catch (Exception e) {
            logger.error("Problem whils deserializing an xml: {}", command, e);
            return "ERROR - " + e.getMessage();
        }

        return "OK";
    }

    @RequestMapping("/portfolio")
    public
    @ResponseBody
    String obtainPortfolios() {
        Iterable<PortfolioEntry> all = portfolioQueryRepository.findAll();
        List<PortfolioEntry> portfolioEntries = new ArrayList<PortfolioEntry>();
        for (PortfolioEntry entry : all) {
            portfolioEntries.add(entry);
        }

        return xStream.toXML(portfolioEntries);
    }

    @RequestMapping("/portfolio/{identifier}")
    public
    @ResponseBody
    String obtainPortfolio(@PathVariable String identifier) {
        PortfolioEntry entry = portfolioQueryRepository.findOne(identifier);

        return xStream.toXML(entry);
    }

    @RequestMapping("/orderbook")
    public
    @ResponseBody
    String obtainOrderBooks() {
        Iterable<OrderBookEntry> all = orderBookQueryRepository.findAll();
        List<OrderBookEntry> orderBookEntries = new ArrayList<OrderBookEntry>();
        for (OrderBookEntry entry : all) {
            orderBookEntries.add(entry);
        }

        return xStream.toXML(orderBookEntries);
    }
}
