package com.icoin.trading.tradeengine;

import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.coin.domain.CurrencyPair;
import com.icoin.trading.api.tradeengine.command.coin.CreateCoinCommand;
import com.icoin.trading.api.tradeengine.command.portfolio.cash.DepositCashCommand;
import com.icoin.trading.api.tradeengine.command.portfolio.coin.AddAmountToPortfolioCommand;
import com.icoin.trading.api.tradeengine.command.transaction.StartBuyTransactionCommand;
import com.icoin.trading.api.tradeengine.command.transaction.StartSellTransactionCommand;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import com.icoin.trading.api.users.command.CreateUserCommand;
import com.icoin.trading.api.users.domain.Identifier;
import com.icoin.trading.api.users.domain.UserId;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.coin.repositories.CoinQueryRepository;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.homhon.util.TimeUtils.currentTime;

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
        Date time = currentTime();
        UserId userId = new UserId();
        CreateUserCommand createUser =
                new CreateUserCommand(userId,
                        userName,
                        firstName,
                        lastName,
                        new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252"),
                        userName + "@163.com",
                        userName,
                        userName,
                        Constants.DEFAULT_ROLES, time);
        commandGateway.send(createUser);
        return userId;
    }

    static void addMoney(UserId buyer1, BigDecimal amount, CommandGateway commandGateway, PortfolioQueryRepository portfolioRepository) {
        PortfolioEntry portfolioEntry = portfolioRepository.findByUserIdentifier(buyer1.toString());
        depositMoneyToPortfolio(portfolioEntry.getIdentifier(), amount, commandGateway);
    }

    static void depositMoneyToPortfolio(String portfolioIdentifier, BigDecimal amountOfMoney, CommandGateway commandGateway) {
        Date time = currentTime();
        DepositCashCommand command =
                new DepositCashCommand(new PortfolioId(portfolioIdentifier), BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, amountOfMoney), time);
        commandGateway.send(command);
    }

    static void addItems(UserId user, String coinId, BigDecimal amount, CommandGateway commandGateway, PortfolioQueryRepository portfolioRepository) {
        Date time = currentTime();
        PortfolioEntry portfolioEntry = portfolioRepository.findByUserIdentifier(user.toString());
        AddAmountToPortfolioCommand command = new AddAmountToPortfolioCommand(
                new PortfolioId(portfolioEntry.getIdentifier()),
                new CoinId(coinId),
                BigMoney.of(CurrencyUnit.of(coinId), amount), time);
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
        Date time = currentTime();
        StartSellTransactionCommand command = new StartSellTransactionCommand(sellTransactionId,
                coinId,
                currencyPair,
                new OrderBookId(orderBookEntry.getPrimaryKey()),
                new PortfolioId(portfolioEntry.getPrimaryKey()),
                tradeAmount,
                price, time);
        commandGateway.send(command);
    }

    static void placeBuyOrder(CoinId coinId, CurrencyPair currencyPair, OrderBookEntry orderBookEntry, PortfolioEntry portfolioEntry, BigMoney tradeAmount, BigMoney price, TransactionId buyTransactionId, CommandGateway commandGateway) {
        Date time = currentTime();
        StartBuyTransactionCommand command = new StartBuyTransactionCommand(buyTransactionId,
                coinId,
                currencyPair,
                new OrderBookId(orderBookEntry.getPrimaryKey()),
                new PortfolioId(portfolioEntry.getPrimaryKey()),
                tradeAmount,
                price, time);
        commandGateway.send(command);
    }
} 

