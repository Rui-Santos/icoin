/**
 * Copyright (C) 2013, Claus Nielsen, cn@cn-consult.dk
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA. */
package com.icoin.trading.bitcoin.client.response;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.icoin.trading.bitcoin.client.BitcoinRpcOperations;
import com.icoin.trading.bitcoin.client.ValueObject;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data returned by BitcoindClient's listAddressGroupings method.
 * <p/>
 * BitcoindClient.listAddressGroupings returns an array with one of these
 * for each group.
 *
 * @author Claus Nielsen
 */

public class ListAddressGroupingsResult extends ValueObject {

    /**
     * Array of array with address, amount, account (optional).
     */
    private Object[] objects;

    /**
     * Balance and account per addresses in the group.
     */
    @JsonIgnore
    private Map<String, BalanceAndAccount> balanceAndAmountPerAddress;


    @SuppressWarnings({"unchecked", "rawtypes"})
    @JsonCreator
    private ListAddressGroupingsResult(Object[] objects) {
        this.objects = objects;
        Map<String, BalanceAndAccount> map = new HashMap<String, BalanceAndAccount>();
        for (Object object : objects) {
            List oa = (List) object;
            String address = (String) oa.get(0);
            BigDecimal amount = BigDecimal.valueOf((Double) oa.get(1)).setScale(BitcoinRpcOperations.SCALE);
            oa.set(1, amount); // Replace the Double with the BigDecimal to make Jackson serialize with right number of decimal places.
            String account = ((oa.size() > 2) ? (String) oa.get(2) : null);
            map.put(address, new BalanceAndAccount(amount, account));
        }
        balanceAndAmountPerAddress = Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("unused") // Used by Jackson for json serialization
    @JsonValue
    private Object[] getObjects() {
        return objects;
    }

}
