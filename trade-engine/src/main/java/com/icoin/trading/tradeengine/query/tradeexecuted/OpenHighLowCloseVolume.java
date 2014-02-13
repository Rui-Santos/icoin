package com.icoin.trading.tradeengine.query.tradeexecuted;

import com.homhon.base.domain.model.ValueObjectSupport;
import com.icoin.trading.tradeengine.MoneyUtils;
import org.joda.money.BigMoney;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-5
 * Time: AM10:47
 * To change this template use File | Settings | File Templates.
 */
public class OpenHighLowCloseVolume extends ValueObjectSupport<OpenHighLowCloseVolume> {
    private String priceCurrency;
    private String amountCurrency;
    private Date date;
    private String year;
    private Long open;
    private Long high;
    private Long low;
    private Long close;
    private Long volume;

    //    TypedAggregation<TradeExecutedEntry> aggregation = newAggregation(TradeExecutedEntry.class,
//            match(where("orderBookIdentifier").is(orderBookIdentifier)
//                    .and("tradeTime").gte(start)
//                    .and("tradeTime").lt(end)),
//            project("tradeTime", "tradedPrice", "tradedAmount")
//                    .and("tradeTime").project("year").as("year")
//                    .and("tradeTime").project("month").as("month")
//                    .and("tradeTime").project("dayOfMonth").as("day")
//                    .and("tradeTime").project("hour").as("hour")
//            ,
//            sort(DESC, "tradeTime", "tradedPrice.amount"),
//            group(Fields.from(Fields.field("price", "tradedPrice.amount"))
//                    .and(Fields.field("priceCurrency", "itemPrice.currency"))
//                    .and(Fields.field("amountCurrency", "tradedAmount.currency")))
//                    .first("tradedPrice").as("open")
//                    .max("tradedPrice").as("high")
//                    .min("tradedPrice").as("low")
//                    .last("tradedPrice").as("close")
//                    .sum("tradedAmount").as("volume"),
//            skip(pageable.getOffset()),
//            limit(pageable.getPageSize())

    public OpenHighLowCloseVolume(String priceCurrency, String amountCurrency,
                                  Date date,
                                  Long open,
                                  Long high,
                                  Long low,
                                  Long close,
                                  Long volume) {
        this.priceCurrency = priceCurrency;
        this.amountCurrency = amountCurrency;
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

//    public OpenHighLowCloseVolume(Date date,
//                                  BigMoney open,
//                                  BigMoney high,
//                                  BigMoney low,
//                                  BigMoney close,
//                                  BigMoney volume) {
//        this.date = date;
//        this.open = open;
//        this.high = high;
//        this.low = low;
//        this.close = close;
//        this.volume = volume;
//    }


    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Date getDate() {
        return date;
    }

    public BigMoney getOpen() {
        return MoneyUtils.convertToBigMoney(priceCurrency, open);
    }

    public BigMoney getHigh() {
        return MoneyUtils.convertToBigMoney(priceCurrency, high);
    }

    public BigMoney getLow() {
        return MoneyUtils.convertToBigMoney(priceCurrency, low);
    }

    public BigMoney getClose() {
        return MoneyUtils.convertToBigMoney(priceCurrency, close);
    }

    public BigMoney getVolume() {
        return MoneyUtils.convertToBigMoney(amountCurrency, volume);
    }
}
