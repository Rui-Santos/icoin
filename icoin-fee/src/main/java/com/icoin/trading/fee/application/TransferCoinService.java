package com.icoin.trading.fee.application;

import com.icoin.trading.api.tradeengine.command.portfolio.cash.WithdrawCashCommand;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.fee.domain.AddressService;
import com.icoin.trading.fee.domain.address.Address;
import com.icoin.trading.fee.domain.address.AddressRepository;
import com.icoin.trading.users.domain.model.user.UserAccount;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.money.BigMoney;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;
import static com.homhon.util.Strings.hasText;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-7
 * Time: PM1:20
 * To change this template use File | Settings | File Templates.
 */
@Service
public class TransferCoinService {
    private static Logger logger = LoggerFactory.getLogger(TransferCoinService.class);
    private AddressService service;
    private AddressRepository addressRepository;
    private CommandGateway commandGateway;
    private int generationBatchSize = 50;

    //address still maybe null
    public Address getAddress(UserAccount account) {
        notNull(account);
        Address address = addressRepository.findOneUnpicked();

        if (address == null) {
            generate(generationBatchSize);
        }

        address = addressRepository.findOneUnpicked();
        address.use(account.getId());
        service.changeAccount(address.getAddress(), account.getPrimaryKey());
        addressRepository.save(address);
        return address;
    }

    public void withdrawCoin(BigMoney amount, UserAccount userAccount, PortfolioId portfolioId, Date withdrawTime) {
        notNull(amount);
        notNull(userAccount);
        notNull(portfolioId);
        notNull(withdrawTime);
        isTrue(amount.isPositive(), "Amount" + amount + " should be positive!");


        commandGateway.send(new WithdrawCashCommand(portfolioId, amount, withdrawTime));

    }


    private void generate(int size) {
        if (!service.isServerAvailable()) {
            return;
        }

        for (int i = 0; i < size; i++) {
            String addressString = service.generate("");
            if (!hasText(addressString)) {
                logger.error("Cannot generate address");
                return;
            }
            final Address address = new Address(addressString);
            address.validate();
            addressRepository.save(address);
        }

    }

    public void setService(AddressService service) {
        this.service = service;
    }
}
