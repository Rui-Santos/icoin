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

package com.icoin.trading.test

import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry

/**
 * For some reason groovy finds the log4j.xml from the httpbuilder jar. Therefore we configure the logging to come
 * from a log4j.properties file on the classpath.
 *
 * -Dlog4j.debug=true -Dlog4j.configuration=log4j.properties
 *
 * @author Jettro Coenradie
 */


def commandSender = new com.icoin.trading.test.CommandSender()

def portfolios = []
commandSender.obtainPortfolios().each() {
    portfolios.add it.primaryKey
}

def coinNames = [:]
def coinToOrderBooks = [:]
def orderBooks = []
commandSender.obtainOrderBooks().each() {
    orderBooks.add it.primaryKey
    coinNames.put(it.primaryKey, it.coinIdentifier)
    coinToOrderBooks.put(it.coinIdentifier, it.primaryKey)
}
def commandCreator = new com.icoin.trading.test.CommandCreator(orderBooks,coinNames,coinToOrderBooks)

def numUsers = portfolios.size()
def numUser = 1;

def time = System.currentTimeMillis();

for (int i = 0; i < 1000; i++) {
    def portfolioIdentifier = portfolios[numUser - 1]
    PortfolioEntry portfolio = commandSender.obtainPortfolio(portfolioIdentifier)
    def command = commandCreator.createCommand(portfolio)

    println "${portfolio.userName} # ${command.tradeAmount} \$ ${command.itemPrice} ${coinNames[command.orderbookIdentifier.toString()]}"

    commandSender.sendCommand(command)

    if (numUser < numUsers) {
        numUser++
    } else {
        numUser = 1
    }

    // Take a breath
//    Thread.sleep(100)
}

time = System.currentTimeMillis() - time ;
println "time is ${time}"