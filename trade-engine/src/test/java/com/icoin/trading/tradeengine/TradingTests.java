package com.icoin.trading.tradeengine;

import com.icoin.trading.tradeengine.application.command.coin.CreateCoinCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.DepositCashCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.AddAmountToPortfolioCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.StartBuyTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.StartSellTransactionCommand;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.coin.repositories.CoinQueryRepository;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import com.icoin.trading.users.application.command.CreateUserCommand;
import com.icoin.trading.users.domain.model.user.Identifier;
import com.icoin.trading.users.domain.model.user.UserId;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 1/20/14
 * Time: 12:02 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class TradingTests {
    static void createBTCCoin(CoinId coinId, String coinName, CommandGateway commandGateway) {
        CreateCoinCommand createCoinCommand = new CreateCoinCommand(
                coinId,
                coinName,
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.ZERO),
                BigMoney.of(Constants.CURRENCY_UNIT_BTC, BigDecimal.ZERO));
        commandGateway.send(createCoinCommand);
    }

    static UserId createUser(String userName, String firstName, String lastName, CommandGateway commandGateway) {
        UserId userId = new UserId();
        CreateUserCommand createUser =
                new CreateUserCommand(userId,
                        userName,
                        firstName,
                        lastName,
                        new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252"),
                        userName + "@163.com",
                        userName,
                        userName);
        commandGateway.send(createUser);
        return userId;
    }

    static void addMoney(UserId buyer1, BigDecimal amount, CommandGateway commandGateway, PortfolioQueryRepository portfolioRepository) {
        PortfolioEntry portfolioEntry = portfolioRepository.findByUserIdentifier(buyer1.toString());
        depositMoneyToPortfolio(portfolioEntry.getIdentifier(), amount, commandGateway);
    }

    static void depositMoneyToPortfolio(String portfolioIdentifier, BigDecimal amountOfMoney, CommandGateway commandGateway) {
        DepositCashCommand command =
                new DepositCashCommand(new PortfolioId(portfolioIdentifier), BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, amountOfMoney));
        commandGateway.send(command);
    }

    static void addItems(UserId user, String coinId, BigDecimal amount, CommandGateway commandGateway, PortfolioQueryRepository portfolioRepository) {
        PortfolioEntry portfolioEntry = portfolioRepository.findByUserIdentifier(user.toString());
        AddAmountToPortfolioCommand command = new AddAmountToPortfolioCommand(
                new PortfolioId(portfolioEntry.getIdentifier()),
                new CoinId(coinId),
                BigMoney.of(CurrencyUnit.of(coinId), amount));
        commandGateway.send(command);
    }

    static OrderBookEntry obtainOrderBookByCoinName(String coinId, CoinQueryRepository coinRepository, OrderBookQueryRepository orderBookRepository) {
        Iterable<CoinEntry> coinEntries = coinRepository.findAll();
        for (CoinEntry entry : coinEntries) {
            if (entry.getPrimaryKey().equals(coinId)) {
                List<OrderBookEntry> orderBookEntries = orderBookRepository.findByCoinIdentifier(entry.getPrimaryKey());
                return orderBookEntries.get(0);
            }
        }
        throw new RuntimeException(String.format("Problem initializing, could not find coin with required name %s.", coinId));
    }


    static void placeSellOrder(CoinId coinId, CurrencyPair currencyPair, OrderBookEntry orderBookEntry, PortfolioEntry portfolioEntry, BigMoney tradeAmount, BigMoney price, TransactionId sellTransactionId, CommandGateway commandGateway) {
        StartSellTransactionCommand command = new StartSellTransactionCommand(sellTransactionId,
                coinId,
                currencyPair,
                new OrderBookId(orderBookEntry.getPrimaryKey()),
                new PortfolioId(portfolioEntry.getPrimaryKey()),
                tradeAmount,
                price);
        commandGateway.send(command);
    }

    static void placeBuyOrder(CoinId coinId, CurrencyPair currencyPair, OrderBookEntry orderBookEntry, PortfolioEntry portfolioEntry, BigMoney tradeAmount, BigMoney price, TransactionId buyTransactionId, CommandGateway commandGateway) {
        StartBuyTransactionCommand command = new StartBuyTransactionCommand(buyTransactionId,
                coinId,
                currencyPair,
                new OrderBookId(orderBookEntry.getPrimaryKey()),
                new PortfolioId(portfolioEntry.getPrimaryKey()),
                tradeAmount,
                price);
        commandGateway.send(command);
    }
} 

