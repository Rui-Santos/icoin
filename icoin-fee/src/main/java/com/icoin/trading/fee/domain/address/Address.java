package com.icoin.trading.fee.domain.address;

import com.homhon.mongo.domainsupport.modelsupport.entity.VersionedEntitySupport;
import org.joda.money.BigMoney;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Strings.hasText;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM9:16
 * To change this template use File | Settings | File Templates.
 */
public class Address extends VersionedEntitySupport<Address, String, Integer> {
    @Indexed
    private String address;
    @Indexed
    private String walletHostname;//wallet server 1/2/3
    @Indexed
    private String userId;
    private String account;//same as userid now
    //listreceivedbyaddress
    //getnewaddress
    private int confirmations;
    private BigMoney amount;
    private BigMoney confirmedAmount;
    private boolean complete;
    private Date created;


    public String getTransactionId() {
        return getPrimaryKey();
    }

    public Address(String address) {
        isTrue(simpleCheck(), "the address is invalid");
        this.address = address;
    }

    public void validate() {

    }

    private boolean simpleCheck() {
        if (!hasText(address)) {
            return false;
        }

//        27-34 alphanumeric characters, beginning with the number 1 or 3
        int length = address.length();

        if (length <= 26 || length >= 35) {
            return false;
        }

        if (!address.startsWith("1") || !address.startsWith("3")) {
            return false;
        }

        // 52 + 10 - 4 = 58, base58 encoded
        if (address.contains("0") || address.contains("I") || address.contains("O") || address.contains("l")) {
            return false;
        }

        return true;
    }
}
