package com.icoin.trading.fee.cash.scheduler;

import com.icoin.trading.api.fee.domain.fee.CancelledReason;
import com.icoin.trading.bitcoin.client.BitcoinRpcOperations;
import com.icoin.trading.bitcoin.client.response.StringResponse;
import com.icoin.trading.bitcoin.client.response.ValidateAddressResponse;
import com.icoin.trading.fee.cash.CashValidator;
import com.icoin.trading.fee.cash.PayScheduler;
import com.icoin.trading.fee.cash.ValidationCode;
import com.icoin.trading.fee.domain.DueDateService;
import com.icoin.trading.fee.domain.address.Address;
import com.icoin.trading.fee.domain.cash.CoinPayCash;
import com.icoin.trading.fee.domain.paid.PaidFee;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import org.axonframework.repository.Repository;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Date;

import static com.homhon.util.Strings.hasText;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-10
 * Time: AM12:25
 * To change this template use File | Settings | File Templates.
 */
public class PayCoinScheduler extends PayScheduler<CoinPayCash> {
    private static Logger logger = LoggerFactory.getLogger(PayCoinScheduler.class);
    private final String commentTo = "from iCoin";
    private BitcoinRpcOperations operations;
    private int minConfirmations = 3;
    private CashValidator cashValidator;
    private UserQueryRepository userQueryRepository;
    private BigMoney minAmount = BigMoney.of(CurrencyUnit.of("BTC"), 0.01);

//    @Override
//    protected BigDecimal getReceivedAmount(CoinCash entity, Date occurringTime) {
//        if (entity.getAddress() == null || !hasText(entity.getAddress().getAddress())) {
//            return null;
//        }
//        BigDecimalResponse response = operations.getReceivedByAddress(entity.getAddress().getAddress(), minConfirmations);
//        return response == null ? null : response.getResult();
//    }

    @Override
    protected String pay(CoinPayCash entity, Date occurringTime) {
        if (entity.getAmount() == null || entity.getAmount().isLessThan(minAmount)) {
            return null;
        }

        Address address = entity.getAddress();

        if (address == null || !hasText(address.getAddress())) {
            return null;
        }



        PaidFee paidFee = paidFeeRepository.load(entity.getPrimaryKey());

        ValidateAddressResponse response = operations.validateAddress(address.getAddress());

        if(response == null){
            logger.error("Server unavailable when doing payment for {}", entity.describe());
            return null;
        }

        if(response.getResult() == null){
            logger.error("Response result is null {}", entity.describe());
            return null;
        }

        if (response.getResult().getValid() != Boolean.FALSE) {
            logger.warn("address {} is incorrect", address.getAddress());
            paidFee.cancel(CancelledReason.INVALID_ADDRESS,occurringTime);
            return null;
        }

        UserEntry user = userQueryRepository.findOne(entity.getUserId());
        ValidationCode validationCode = cashValidator.canCreate(user, entity.getPortfolioId(), entity.getAmount(), occurringTime);

        if (ValidationCode.breakDown(validationCode)) {
            logger.warn("Validation failed for entity {} with error: {}", entity, validationCode, entity.describe());
            return null;
        }

//        operations.getRawTransaction();

        StringResponse tx = operations.sendToAddress(address.getAddress(), entity.getAmount().getAmount(), "pay for " + user.getUsername(), commentTo);

        if (tx == null || hasText(tx.getResult())) {
            return null;
        }

        return tx.getResult();
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setOperations(BitcoinRpcOperations operations) {
        this.operations = operations;
    }

    public void setMinConfirmations(int minConfirmations) {
        this.minConfirmations = minConfirmations;
    }


    public void setUserQueryRepository(UserQueryRepository userQueryRepository) {
        this.userQueryRepository = userQueryRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Resource(name = "coinTransferringInCashValidator")
    public void setCashValidator(CashValidator cashValidator) {
        this.cashValidator = cashValidator;
    }
}