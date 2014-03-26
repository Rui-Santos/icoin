package com.icoin.trading.fee.domain.address;

import com.homhon.mongo.domainsupport.modelsupport.entity.VersionedEntitySupport;
import org.springframework.data.mongodb.core.index.Indexed;

import static com.homhon.util.Strings.hasText;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM9:16
 * To change this template use File | Settings | File Templates.
 */
public class Address extends VersionedEntitySupport<Address, String, Integer> {
    private boolean picked;
    @Indexed
    private String address;
    private String account;

    public boolean isPicked() {
        return picked;
    }

    private void setPicked(boolean picked) {
        this.picked = picked;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void pickUp(){
        picked = true;
    }

    public void release(){
        picked = false;
    }

    public boolean isValid() {
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
