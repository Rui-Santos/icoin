package com.icoin.trading.fee.saga;

import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.events.commission.SellExecutedCommissionTransactionStartedEvent;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import org.axonframework.test.saga.AnnotatedSagaTestFixture;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static com.homhon.util.TimeUtils.currentTime;
import static org.axonframework.test.matchers.Matchers.exactSequenceOf;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-19
 * Time: AM9:04
 * To change this template use File | Settings | File Templates.
 */
public class SellExecutedCommissionManagerSagaTest {
    private final FeeTransactionId feeTransactionId = new FeeTransactionId();
    private final String orderId = "orderId";
    private final CoinId coinId = new CoinId("BTC");
    private final TransactionId orderTransactionId = new TransactionId();
    private final PortfolioId portfolioId = new PortfolioId();
    private final Date tradeTime = currentTime();
    private final Date dueDate = new Date();
    private final OrderBookId orderBookId = new OrderBookId();
    private final FeeId receivedFeeId = new FeeId();
    private final FeeId accountReceivableFeeId = new FeeId();
    private final OffsetId offsetId = new OffsetId();
    private AnnotatedSagaTestFixture fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new AnnotatedSagaTestFixture(SellExecutedCommissionManagerSaga.class);

    }

    @Test
    public void testStarted() throws Exception {
        fixture.givenAggregate(feeTransactionId).published()
                .whenAggregate(feeTransactionId).publishes(
                new SellExecutedCommissionTransactionStartedEvent(
                        feeTransactionId,
                        receivedFeeId,
                        accountReceivableFeeId,
                        offsetId,
                        BigMoney.of(CurrencyUnit.of("BTC"),10),
                        orderId,
                        orderTransactionId,
                        portfolioId,
                        tradeTime,
                        dueDate,
                        TradeType.BUY,
                        BigMoney.of(CurrencyUnit.EUR, 1009),
                        BigMoney.of(CurrencyUnit.of("BTC"), 120.23),
                        BigMoney.of(CurrencyUnit.EUR, 109),
                        orderBookId,
                        coinId))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(
                        exactSequenceOf(new ReserveMoneyFromPortfolioCommandMatcher(
                                buyPortfolioId,
                                transactionIdentifier,
                                TOTAL_MONEY,
                                TOTAL_COMMISSION)));

    }
}
