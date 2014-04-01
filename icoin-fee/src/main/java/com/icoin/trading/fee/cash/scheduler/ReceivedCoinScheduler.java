package com.icoin.trading.fee.cash.scheduler;

import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.domain.received.ReceivedSource;
import com.icoin.trading.api.fee.domain.received.ReceivedSourceType;
import com.icoin.trading.api.fee.domain.transfer.TransferTransactionType;
import com.icoin.trading.api.fee.domain.transfer.TransferType;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.bitcoin.client.BitcoinRpcOperations;
import com.icoin.trading.bitcoin.client.response.BigDecimalResponse;
import com.icoin.trading.fee.cash.CashValidator;
import com.icoin.trading.fee.cash.Scheduler;
import com.icoin.trading.fee.cash.ValidationCode;
import com.icoin.trading.fee.domain.DueDateService;
import com.icoin.trading.fee.domain.address.Address;
import com.icoin.trading.fee.domain.cash.CoinCash;
import com.icoin.trading.fee.domain.transaction.CoinTransferringInTransaction;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import org.axonframework.repository.Repository;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-1
 * Time: PM9:26
 * To change this template use File | Settings | File Templates.
 */
public class ReceivedCoinScheduler extends Scheduler<CoinCash> {
    private static Logger logger = LoggerFactory.getLogger(ReceivedCoinScheduler.class);
    private BitcoinRpcOperations operations;
    private int minConfirmations = 6;
    private Repository<CoinTransferringInTransaction> repository;
    private CashValidator cashValidator;
    private UserQueryRepository userQueryRepository;
    private DueDateService dueDateService;

    @Override
    protected BigDecimal getReceivedAmount(CoinCash entity, Date occurringTime) {
        BigDecimalResponse response = operations.getReceivedByAddress(entity.getAddress().getAddress(), minConfirmations);
        return response == null ? null : response.getResult();
    }

    @Override
    protected void complete(CoinCash entity, BigDecimal received, Date occurringTime) {
        final BigMoney amount = BigMoney.of(CurrencyUnit.of("BTC"), received);

        UserEntry user = userQueryRepository.findOne(entity.getUserId());
        ValidationCode validationCode = cashValidator.canCreate(user, amount, occurringTime);

        if (ValidationCode.breakDown(validationCode)) {
            logger.error("validation failed  for entity {} with error: {}", entity, validationCode, entity.describe());
            return;
        }


        entity.confirmed(amount.toMoney(RoundingMode.HALF_EVEN).toBigMoney(), occurringTime);

        pendingCashRepository.save(entity);

        final Address address = entity.getAddress();
        repository.add(new CoinTransferringInTransaction(
                new FeeTransactionId(),
                new OffsetId(),
                new PortfolioId(),
                occurringTime,
                dueDateService.computeDueDate(occurringTime),
                new FeeId(),
                new FeeId(),
                amount,
                new CoinId("BTC"),
                TransferTransactionType.COIN,
                TransferType.IN,
                new ReceivedSource(ReceivedSourceType.COIN_ADDRESS, address == null ? null : address.getAddress()),
                entity.getPrimaryKey()));
    }

    @Autowired
    public void setOperations(BitcoinRpcOperations operations) {
        this.operations = operations;
    }

    public void setMinConfirmations(int minConfirmations) {
        this.minConfirmations = minConfirmations;
    }

    @Resource(name = "coinTransferringInTransactionRepository")
    public void setRepository(Repository<CoinTransferringInTransaction> repository) {
        this.repository = repository;
    }

    @Resource(name = "coinTransferringInCashValidator")
    public void setCashValidator(CashValidator cashValidator) {
        this.cashValidator = cashValidator;
    }

    @Autowired
    public void setDueDateService(DueDateService dueDateService) {
        this.dueDateService = dueDateService;
    }
}
