package com.icoin.trading.fee.application.command;

import com.icoin.trading.api.fee.command.receivable.CancelAccountReceivableFeeCommand;
import com.icoin.trading.api.fee.command.receivable.ConfirmAccountReceivableFeeCommand;
import com.icoin.trading.api.fee.command.receivable.CreateAccountReceivableFeeCommand;
import com.icoin.trading.api.fee.command.receivable.OffsetAccountReceivableFeeCommand;
import com.icoin.trading.fee.domain.payable.AccountPayableFee;
import com.icoin.trading.fee.domain.receivable.AccountReceivableFee;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-19
 * Time: PM8:59
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ReceivablePayableCommandHandler {
    private Repository<AccountReceivableFee> accountReceivableFeeRepository;
    private Repository<AccountPayableFee> accountPayableFeeRepository;

    @CommandHandler
    public void handleCreateAccountReceivable(CreateAccountReceivableFeeCommand command) {
//         receivedEntryRepository.save(toReceivedEntry(command));

        accountReceivableFeeRepository.add(new AccountReceivableFee(
                command.getFeeId(),
                command.getFeeStatus(),
                command.getAmount(),
                command.getFeeType(),
                command.getDueDate(),
                command.getCreatedTime(),
                command.getUserAccountId(),
                command.getBusinessType(),
                command.getBusinessReferenceId()
        ));

    }

    @CommandHandler
    public void handleConfirmAccountReceivable(ConfirmAccountReceivableFeeCommand command) {
        AccountReceivableFee fee = accountReceivableFeeRepository.load(command.getFeeId());

        fee.confirm(command.getConfirmedDate());
    }

    @CommandHandler
    public void handleOffsetAccountReceivable(OffsetAccountReceivableFeeCommand command) {
        AccountReceivableFee fee = accountReceivableFeeRepository.load(command.getFeeId());

        fee.offset(command.getOffsetedDate());
    }


    @CommandHandler
    public void handleCancelAccountReceivable(CancelAccountReceivableFeeCommand command) {
        AccountReceivableFee fee = accountReceivableFeeRepository.load(command.getFeeId());

        fee.cancel(command.getCancelledReason(), command.getCancelledDate());
    }


    @Resource(name = "accountReceivableFeeRepository")
    public void setAccountReceivableFeeRepository(Repository<AccountReceivableFee> accountReceivableFeeRepository) {
        this.accountReceivableFeeRepository = accountReceivableFeeRepository;
    }

    @Resource(name = "accountReceivableFeeRepository")
    public void setAccountPayableFeeRepository(Repository<AccountPayableFee> accountPayableFeeRepository) {
        this.accountPayableFeeRepository = accountPayableFeeRepository;
    }
}
