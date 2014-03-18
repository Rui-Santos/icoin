package com.icoin.trading.fee.application.command;

import com.icoin.trading.api.fee.command.offset.CreateOffsetCommand;
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
    public void handleCommission(CreateOffsetCommand command) {
        offsetRepository.add(
                new Offset(command.getOffsetId(),
                        command.getOffsetType(),
                        command.getAccountId(),
                        command.getArapList(),
                        command.getReceivedPaidList(),
                        command.getOffsetAmount(),
                        command.getStartedDate()));

    }


    @Resource(name = "offsetRepository")
    public void setOffsetRepository(Repository<Offset> offsetRepository) {
        this.offsetRepository = offsetRepository;
    }
}