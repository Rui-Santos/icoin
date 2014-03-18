package com.icoin.trading.fee.query.executed;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM8:26
 * To change this template use File | Settings | File Templates.
 */
public class ExecutedCommissionEntry {
    private String transactionId;
    private String userAccountId;
    private BusinessType businessType;
    private String businessReferenceId;
    private Date executedTime;
    private Date dueDate;
    private ReceivedSource receivedSource;
    private String orderBookIdentifier;

    private String coinId;

    private TradeType tradeType;

    private BigMoney amount;
    private FeeType feeType;

    private String receivedId;
    private String receivableId;
}
