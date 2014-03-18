package com.icoin.trading.fee.domain.received;

import com.homhon.mongo.domainsupport.modelsupport.entity.VersionedEntitySupport;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.api.fee.domain.received.ReceivedSource;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 9/10/13
 * Time: 1:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReceivedEntry extends VersionedEntitySupport<ReceivedEntry, String, Integer> {
    private FeeId feeId;

    private ReceivedSource receivedSource;

    private String orderBookIdentifier;

    private String coinId;

    private TradeType tradeType;

    private BigMoney tradedPrice;

    private BigMoney tradeAmount;

    private BigMoney executedMoney;

    private FeeStatus feeStatus;
    private BigMoney amount;
    private FeeType feeType;
    private BusinessType businessType;

    private Date createdTime;
    private Date dueDate;
    private String userAccountId;
    private String businessReferenceId;


}