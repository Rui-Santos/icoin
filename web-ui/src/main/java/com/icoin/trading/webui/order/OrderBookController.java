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

package com.icoin.trading.webui.order;

import com.icoin.trading.tradeengine.query.orderbook.OrderBookEntry;
import com.icoin.trading.tradeengine.query.orderbook.repositories.OrderBookQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Jettro Coenradie
 */
@Controller
@RequestMapping("/orderbook")
public class OrderBookController {

    private OrderBookQueryRepository repository;
    private String externalServerUrl;

    @Autowired
    public OrderBookController(OrderBookQueryRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String get(Model model) {
        model.addAttribute("items", repository.findAll());
        return "orderbook/list";
    }

    @RequestMapping(value = "socket", method = RequestMethod.GET)
    public String getSocket(ModelMap modelMap) {
        modelMap.addAttribute("externalServerurl", externalServerUrl);
        return "orderbook/socket";
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET)
    public String getOrders(@PathVariable String identifier, Model model) {
        OrderBookEntry orderBook = repository.findOne(identifier);
        model.addAttribute("orderBook", orderBook);
        return "orderbook/orders";
    }

    @Value("#{external.serverUrlEventBus}")
    public void setExternalServerUrl(String externalServerUrl) {
        this.externalServerUrl = externalServerUrl;
    }
}
