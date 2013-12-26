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
    private String userName;
    private BigMoney amountOfMoney = BigMoney.zero(Constants.DEFAULT_CURRENCY_UNIT);
    private BigMoney reservedAmountOfMoney = BigMoney.zero(Constants.DEFAULT_CURRENCY_UNIT);
//    private BigMoney lowestPrice = BigDecimal.valueOf(0.00000001);

    private Map<String, ItemEntry> itemsInPossession = new HashMap<String, ItemEntry>();
    private Map<String, ItemEntry> itemsReserved = new HashMap<String, ItemEntry>();

//    @Value("${trade.lowestPrice}")
//    public void setLowestPrice(BigDecimal lowestPrice) {
//        this.lowestPrice = lowestPrice;
//    }

    /*-------------------------------------------------------------------------------------------*/
    /* utility functions                                                                         */
    /*-------------------------------------------------------------------------------------------*/
    public BigMoney obtainAmountOfAvailableItemsFor(String primaryKey, CurrencyUnit currencyUnit) {
        BigMoney possession = obtainAmountOfItemsInPossessionFor(primaryKey, currencyUnit);
        BigMoney reserved = obtainAmountOfReservedItemsFor(primaryKey, currencyUnit);
        return possession.minus(reserved);
    }

    public BigMoney obtainAmountOfReservedItemsFor(String primaryKey, CurrencyUnit currencyUnit) {
        ItemEntry item = findReservedItemByIdentifier(primaryKey);
        if (null == item) {
            return BigMoney.zero(currencyUnit);
        }
        return item.getAmount();
    }

    public BigMoney obtainAmountOfItemsInPossessionFor(String primaryKey, CurrencyUnit currencyUnit) {
        ItemEntry item = findItemInPossession(primaryKey);
        if (null == item) {
            return BigMoney.zero(currencyUnit);
        }
        return item.getAmount();
    }

    public BigMoney obtainMoneyToSpend() {
        return amountOfMoney.minus(reservedAmountOfMoney);
    }

    public ItemEntry findReservedItemByIdentifier(String primaryKey) {
        return itemsReserved.get(primaryKey);
    }

    public ItemEntry findItemInPossession(String primaryKey) {
        return itemsInPossession.get(primaryKey);
    }

    public void addReservedItem(ItemEntry itemEntry) {
        handleAdd(itemsReserved, itemEntry);
    }

    public void addItemInPossession(ItemEntry itemEntry) {
        handleAdd(itemsInPossession, itemEntry);
    }

    public void removeReservedItem(String itemIdentifier, BigMoney amount) {
        handleRemoveItem(itemsReserved, itemIdentifier, amount);
    }

    public void removeItemsInPossession(String itemIdentifier, BigMoney amount) {
        handleRemoveItem(itemsInPossession, itemIdentifier, amount);
    }

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

    public Map<String, ItemEntry> getItemsInPossession() {
        return itemsInPossession;
    }

    public void setItemsInPossession(Map<String, ItemEntry> itemsInPossession) {
        this.itemsInPossession = itemsInPossession;
    }

    public Map<String, ItemEntry> getItemsReserved() {
        return itemsReserved;
    }

    public void setItemsReserved(Map<String, ItemEntry> itemsReserved) {
        this.itemsReserved = itemsReserved;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /*-------------------------------------------------------------------------------------------*/
    /* Private helper methods                                                                    */
    /*-------------------------------------------------------------------------------------------*/
    private void handleAdd(Map<String, ItemEntry> items, ItemEntry itemEntry) {
        if (items.containsKey(itemEntry.getCoinIdentifier())) {
            ItemEntry foundEntry = items.get(itemEntry.getCoinIdentifier());
            foundEntry.setAmount(foundEntry.getAmount().plus(itemEntry.getAmount()));
        } else {
            items.put(itemEntry.getCoinIdentifier(), itemEntry);
        }
    }

    private void handleRemoveItem(Map<String, ItemEntry> items, String itemIdentifier, BigMoney amount) {
        if (items.containsKey(itemIdentifier)) {
            ItemEntry foundEntry = items.get(itemIdentifier);
            foundEntry.setAmount(foundEntry.getAmount().minus(amount));
            if (foundEntry.getAmount().isNegativeOrZero()) {
                items.remove(foundEntry.getCoinIdentifier());
            }
        }
    }

    @Override
    public String toString() {
        return "PortfolioEntry{" +
                "primaryKey='" + primaryKey + '\'' +
                ", amountOfMoney=" + amountOfMoney +
                ", userIdentifier='" + userIdentifier + '\'' +
                ", userName='" + userName + '\'' +
                ", reservedAmountOfMoney=" + reservedAmountOfMoney +
                ", itemsInPossession=" + itemsInPossession +
                ", itemsReserved=" + itemsReserved +
                '}';
    }
}
