/*
 * Copyright (c) 2010-2012. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icoin.trading.tradeengine.query.portfolio;

import com.homhon.mongo.domainsupport.modelsupport.entity.AuditAwareEntitySupport;
import com.icoin.trading.tradeengine.Constants;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jettro Coenradie
 */
public class PortfolioEntry extends AuditAwareEntitySupport<PortfolioEntry, String, Long> {

    private String userIdentifier;
    private String fullName;
    private String userName;
    private BigMoney amountOfMoney = BigMoney.zero(Constants.DEFAULT_CURRENCY_UNIT);
    private BigMoney reservedAmountOfMoney = BigMoney.zero(Constants.DEFAULT_CURRENCY_UNIT);
//    private BigMoney lowestPrice = BigDecimal.valueOf(0.00000001);

    private Map<String, ItemEntry> items = new HashMap<String, ItemEntry>();

//    @Value("${trade.lowestPrice}")
//    public void setLowestPrice(BigDecimal lowestPrice) {
//        this.lowestPrice = lowestPrice;
//    }

    /*-------------------------------------------------------------------------------------------*/
    /* utility functions                                                                         */
    /*-------------------------------------------------------------------------------------------*/
    public BigMoney obtainAmountOfAvailableItemFor(String primaryKey, CurrencyUnit currencyUnit) {
        ItemEntry item = findItemByIdentifier(primaryKey);
        if (null == item) {
            return BigMoney.zero(currencyUnit);
        }
        return item.getAvailableAmount();
    }

    public BigMoney obtainAmountOfReservedItemFor(String primaryKey, CurrencyUnit currencyUnit) {
        ItemEntry item = findItemByIdentifier(primaryKey);
        if (null == item) {
            return BigMoney.zero(currencyUnit);
        }
        return item.getReservedAmount();
    }

    public BigMoney obtainAmountOfItemInPossessionFor(String primaryKey, CurrencyUnit currencyUnit) {
        ItemEntry item = findItemByIdentifier(primaryKey);
        if (null == item) {
            return BigMoney.zero(currencyUnit);
        }
        return item.getAmountInPossession();
    }

    public void confirmReserved(String coinIdentifier, BigMoney amount) {
        if (!hasItem(coinIdentifier)) {
            throw new IllegalArgumentException("cannot find item with " + coinIdentifier + ", please add it first if necessary");
        }

        ItemEntry foundEntry = findItemByIdentifier(coinIdentifier);
        foundEntry.confirmReserved(amount);
    }

    public BigMoney obtainMoneyToSpend() {
        return amountOfMoney.minus(reservedAmountOfMoney);
    }

    public ItemEntry findItemByIdentifier(String primaryKey) {
        return items.get(primaryKey);
    }

//    public ItemEntry findItemInPossession(String primaryKey) {
//        return items.get(primaryKey);
//    }

    public void addReserved(String coinIdentifier, BigMoney reserved) {
        if (!hasItem(coinIdentifier)) {
            throw new IllegalArgumentException("cannot find item with " + coinIdentifier + ", please add it first if necessary");
        }
        handleAddReserved(coinIdentifier, reserved);
    }

    public void addItemInPossession(String coinIdentifier, BigMoney amount) {
        if (!hasItem(coinIdentifier)) {
            throw new IllegalArgumentException("cannot find item with " + coinIdentifier + ", please add it first if necessary");
        }
        handleAddPossession(coinIdentifier, amount);
    }

    public boolean hasItem(String coinIdentifier) {
        return items.containsKey(coinIdentifier);
    }

    private void handleAddPossession(String coinIdentifier, BigMoney amount) {
        ItemEntry foundEntry = findItemByIdentifier(coinIdentifier);
        foundEntry.addAmountInPossession(amount);
    }

    private void handleAddReserved(String coinIdentifier, BigMoney amount) {
        ItemEntry foundEntry = findItemByIdentifier(coinIdentifier);
        foundEntry.addReservedAmount(amount);
    }

    public ItemEntry createItem(String coinIdentifier, String coinName) {
        if (hasItem(coinIdentifier)) {
            return findItemByIdentifier(coinIdentifier);
        }
        ItemEntry itemEntry = new ItemEntry(coinIdentifier);
        itemEntry.setCoinName(coinName);
        items.put(coinIdentifier, itemEntry);
        return itemEntry;
    }

    public void cancelReserved(String coinIdentifier, BigMoney amount) {
        if (!hasItem(coinIdentifier)) {
            throw new IllegalArgumentException("cannot find item with " + coinIdentifier + ", please add it first if necessary");
        }
        ItemEntry foundEntry = findItemByIdentifier(coinIdentifier);
        foundEntry.cancelReserved(amount);
    }


//    public void removeReservedItem(String coinIdentifier, BigMoney amount) {
//        handleRemoveItem(itemsReserved, coinIdentifier, amount);
//    }

//    public void removeItemInPossession(String coinIdentifier, BigMoney amount) {
//        handleRemoveItem(items, itemIdentifier, amount);
//    }

    /*-------------------------------------------------------------------------------------------*/
    /* Getters and setters                                                                       */
    /*-------------------------------------------------------------------------------------------*/
    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public BigMoney getAmountOfMoney() {
        return amountOfMoney;
    }

    public void setAmountOfMoney(BigMoney amountOfMoney) {
        this.amountOfMoney = amountOfMoney;
    }

    public String getIdentifier() {
        return primaryKey;
    }

    public void setIdentifier(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public BigMoney getReservedAmountOfMoney() {
        return reservedAmountOfMoney;
    }

    public void setReservedAmountOfMoney(BigMoney reservedAmountOfMoney) {
        this.reservedAmountOfMoney = reservedAmountOfMoney;
    }

    public Map<String, ItemEntry> getItems() {
        return items;
    }

    public void setItems(Map<String, ItemEntry> iterms) {
        this.items = iterms;
    }

//    public Map<String, ItemEntry> getItemsReserved() {
//        return items;
//    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

//    /*-------------------------------------------------------------------------------------------*/
//    /* Private helper methods                                                                    */
//    /*-------------------------------------------------------------------------------------------*/
//    private void handleAdd(Map<String, ItemEntry> items, ItemEntry itemEntry) {
//        if (items.containsKey(itemEntry.getCoinIdentifier())) {
//            ItemEntry foundEntry = findItemByIdentifier(itemEntry.getCoinIdentifier());
//            foundEntry.setAmountInPossession(foundEntry.getAmountInPossession().plus(itemEntry.getAmountInPossession()));
//        } else {
//            items.put(itemEntry.getCoinIdentifier(), itemEntry);
//        }
//    }
//
//    private void handleRemoveItem(Map<String, ItemEntry> items, String itemIdentifier, BigMoney amount) {
//        if (items.containsKey(itemIdentifier)) {
//            ItemEntry foundEntry = findItemByIdentifier(itemIdentifier);
//            foundEntry.setAmountInPossession(foundEntry.getAmountInPossession().minus(amount));
//            if (foundEntry.getAmountInPossession().isNegativeOrZero()) {
//                items.remove(foundEntry.getCoinIdentifier());
//            }
//        }
//    }

    @Override
    public String toString() {
        return "PortfolioEntry{" +
                "primaryKey='" + primaryKey + '\'' +
                ", amountOfMoney=" + amountOfMoney +
                ", userIdentifier='" + userIdentifier + '\'' +
                ", userName='" + userName + '\'' +
                ", reservedAmountOfMoney=" + reservedAmountOfMoney +
                ", items=" + items +
                '}';
    }
}
