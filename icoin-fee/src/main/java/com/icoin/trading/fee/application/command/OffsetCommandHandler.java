package com.icoin.trading.fee.application.command;

import com.icoin.trading.api.fee.command.offset.CancelOffsetCommand;
import com.icoin.trading.api.fee.command.offset.CreateOffsetCommand;
import com.icoin.trading.api.fee.command.offset.OffsetFeesCommand;
import com.icoin.trading.fee.domain.offset.Offset;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:10
 * To change this template use File | Settings | File Templates.
 */
@Component
public class OffsetCommandHandler {
    private Repository<Offset> offsetRepository;

    @CommandHandler
    public void handleCreateOffset(CreateOffsetCommand command) {
        offsetRepository.add(
                new Offset(command.getOffsetId(),
                        command.getOffsetType(),
                        command.getAccountId(),
                        command.getArapList(),
                        command.getReceivedPaidList(),
                        command.getOffsetAmount(),
                        command.getStartedDate()));
    }

    @CommandHandler
    public void handleOffsetFees(OffsetFeesCommand command) {
        Offset offset = offsetRepository.load(command.getOffsetId());

        offset.offset(command.getOffsetedDate());
    }


    @CommandHandler
    public void handleCancelOffset(CancelOffsetCommand command) {
        Offset offset = offsetRepository.load(command.getOffsetId());

        offset.cancel(command.getCancelledReason(), command.getCancelledDate());
    }

    @Resource(name = "offsetRepository")
    public void setOffsetRepository(Repository<Offset> offsetRepository) {
        this.offsetRepository = offsetRepository;
    }
}