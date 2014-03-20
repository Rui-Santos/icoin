package com.icoin.trading.fee.application.command;

import com.google.common.collect.ImmutableList;
import com.icoin.trading.api.fee.command.offset.CancelOffsetCommand;
import com.icoin.trading.api.fee.command.offset.CreateOffsetCommand;
import com.icoin.trading.api.fee.command.offset.OffsetFeesCommand;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.offset.CancelledReason;
import com.icoin.trading.api.fee.domain.offset.FeeItem;
import com.icoin.trading.api.fee.domain.offset.FeeItemType;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.domain.offset.OffsetType;
import com.icoin.trading.api.fee.events.offset.OffsetCancelledEvent;
import com.icoin.trading.api.fee.events.offset.OffsetCreatedEvent;
import com.icoin.trading.api.fee.events.offset.OffsetedEvent;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.fee.domain.offset.Offset;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/19/14
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class OffsetCommandHandlerTest {

    private final BigMoney offsetAmount = BigMoney.of(CurrencyUnit.of("BTC"), 10);
    private final BigMoney receivedAmount1 = BigMoney.of(CurrencyUnit.of("BTC"), 7);
    private final BigMoney receivedAmount2 = BigMoney.of(CurrencyUnit.of("BTC"), 3);
    private final Date tradeTime = new Date();
    private final FeeId receivedFeeId = new FeeId();
    private final FeeId accountReceivableFeeId1 = new FeeId();
    private final FeeId accountReceivableFeeId2 = new FeeId();
    private final PortfolioId portfolioId = new PortfolioId();
    private final OffsetId offsetId = new OffsetId();
    private FixtureConfiguration fixture;
    private OffsetCommandHandler commandHandler;

    @Before
    public void setUp() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(Offset.class);
        commandHandler = new OffsetCommandHandler();
        fixture.registerAnnotatedCommandHandler(commandHandler);
    }

    @Test
    public void testHandleCreateOffset() throws Exception {
        CreateOffsetCommand command =
                new CreateOffsetCommand(offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.AR, offsetAmount)),
                        ImmutableList.of(
                                new FeeItem(accountReceivableFeeId1.toString(), FeeItemType.RECEIVED, receivedAmount1),
                                new FeeItem(accountReceivableFeeId2.toString(), FeeItemType.RECEIVED, receivedAmount2)),
                        offsetAmount,
                        tradeTime);

        fixture.given()
                .when(command)
                .expectEvents(new OffsetCreatedEvent(offsetId,
                        OffsetType.RECEIVED_AR,
                        portfolioId.toString(),
                        ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.AR, offsetAmount)),
                        ImmutableList.of(
                                new FeeItem(accountReceivableFeeId1.toString(), FeeItemType.RECEIVED, receivedAmount1),
                                new FeeItem(accountReceivableFeeId2.toString(), FeeItemType.RECEIVED, receivedAmount2)),
                        offsetAmount,
                        tradeTime));

    }

    @Test
    public void testHandleOffsetFees() throws Exception {
        OffsetFeesCommand command = new OffsetFeesCommand(
                offsetId,
                tradeTime);
        fixture.given(new OffsetCreatedEvent(offsetId,
                OffsetType.RECEIVED_AR,
                portfolioId.toString(),
                ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.AR, offsetAmount)),
                ImmutableList.of(
                        new FeeItem(accountReceivableFeeId1.toString(), FeeItemType.RECEIVED, receivedAmount1),
                        new FeeItem(accountReceivableFeeId2.toString(), FeeItemType.RECEIVED, receivedAmount2)),
                offsetAmount,
                tradeTime))
                .when(command)
                .expectEvents(new OffsetedEvent(offsetId, offsetAmount, tradeTime));
    }

    @Test
    public void testHandleCancelOffset() throws Exception {
        CancelOffsetCommand command = new CancelOffsetCommand(
                offsetId,
                CancelledReason.AMOUNT_NOT_MATCHED,
                tradeTime);
        fixture.given(new OffsetCreatedEvent(offsetId,
                OffsetType.RECEIVED_AR,
                portfolioId.toString(),
                ImmutableList.of(new FeeItem(receivedFeeId.toString(), FeeItemType.AR, offsetAmount)),
                ImmutableList.of(
                        new FeeItem(accountReceivableFeeId1.toString(), FeeItemType.RECEIVED, receivedAmount1),
                        new FeeItem(accountReceivableFeeId2.toString(), FeeItemType.RECEIVED, receivedAmount2)),
                offsetAmount,
                tradeTime))
                .when(command)
                .expectEvents(new OffsetCancelledEvent(offsetId, CancelledReason.AMOUNT_NOT_MATCHED, tradeTime));
    }
}