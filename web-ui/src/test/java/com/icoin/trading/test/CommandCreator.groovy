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

import com.icoin.trading.api.tradeengine.command.transaction.StartBuyTransactionCommand
import com.icoin.trading.api.tradeengine.command.transaction.StartSellTransactionCommand
import com.icoin.trading.api.tradeengine.domain.OrderBookId
import com.icoin.trading.api.tradeengine.domain.PortfolioId
import com.icoin.trading.api.tradeengine.domain.TransactionId
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry
import org.joda.money.BigMoney
import org.joda.money.CurrencyUnit

/**
 * Class used to create an order based on the provided Profile. If the profile has cash we place buy orders, if
 * the profile is almost out of cash we start selling stuff.
 *
 * @author Jettro Coenradie
 */
class CommandCreator {
    Random randomFactory = new Random()
    def orderBookEntries
    def coinNames
    def coinToOrderBooks
    def command

    def CommandCreator(orderBookEntries, coinNames, coinToOrderBooks) {
        this.orderBookEntries = orderBookEntries
        this.coinNames = coinNames
        this.coinToOrderBooks = coinToOrderBooks
    }

    def createCommand(PortfolioEntry portfolio) {
        if (portfolio.amountOfMoney.isGreaterThan(portfolio.reservedAmountOfMoney.plus(100))) {
            def orderBookId = obtainRandomOrderBook()
            command = new StartBuyTransactionCommand(
                    new TransactionId(),
                    new OrderBookId(orderBookId),
                    new PortfolioId(portfolio.identifier),
                    BigMoney.of(CurrencyUnit.of(coinNames[orderBookId]), randomFactory.nextInt(50) + 1),
                    BigMoney.of(portfolio.amountOfMoney.getCurrencyUnit(), randomFactory.nextInt(10) + 1))
        } else {
            def availableOrderBook = obtainAvailableOrderBook(portfolio)
            if (availableOrderBook) {
                command = new StartSellTransactionCommand(
                        new TransactionId(),
                        new OrderBookId(availableOrderBook[0]),
                        new PortfolioId(portfolio.identifier),
                        availableOrderBook[1],
                        BigMoney.of(portfolio.amountOfMoney.getCurrencyUnit(), randomFactory.nextInt(10) + 1))
            }
        }

        return command
    }

    private def obtainRandomOrderBook() {
        return orderBookEntries[randomFactory.nextInt(orderBookEntries.size())]
    }

    private def obtainAvailableOrderBook(PortfolioEntry portfolioEntry) {
        def amountOfOrderBooks = portfolioEntry.itemsInPossession.size()
        def counterOrderBook = 0
        while (counterOrderBook < amountOfOrderBooks) {
            def identifier = portfolioEntry.itemsInPossession.keySet().toArray()[counterOrderBook]
            def amountAvailable = portfolioEntry.itemsInPossession[identifier].amountInPossession
            def reserved = (portfolioEntry.itemsReserved[identifier]) ? portfolioEntry.itemsReserved[identifier].amountInPossession : BigMoney.zero(amountAvailable.getCurrencyUnit())
            if (amountAvailable.isGreaterThan(reserved)) {
                def amountToSell = amountAvailable.isGreaterThan(reserved.plus(50)) ? BigMoney.of(amountAvailable.getCurrencyUnit(), randomFactory.nextInt(50) + 1) : amountAvailable.minus(reserved)
                return [coinToOrderBooks[identifier], amountToSell]
            }
            counterOrderBook++
        }
        return null
    }

}
