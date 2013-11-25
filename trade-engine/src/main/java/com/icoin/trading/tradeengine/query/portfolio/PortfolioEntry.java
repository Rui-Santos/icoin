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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jettro Coenradie
 */
public class PortfolioEntry {

    @Id
    private String identifier;
    private String userIdentifier;
    private String userName;
    private BigDecimal amountOfMoney;
    private BigDecimal reservedAmountOfMoney;
    private BigDecimal lowestPrice = BigDecimal.valueOf(0.00000001);

    private Map<String, ItemEntry> itemsInPossession = new HashMap<String, ItemEntry>();
    private Map<String, ItemEntry> itemsReserved = new HashMap<String, ItemEntry>();

    @Value("${trade.lowestPrice}")
    public void setLowestPrice(BigDecimal lowestPrice) {
        this.lowestPrice = lowestPrice;
    }
    /*-------------------------------------------------------------------------------------------*/
    /* utility functions                                                                         */
    /*-------------------------------------------------------------------------------------------*/
    public BigDecimal obtainAmountOfAvailableItemsFor(String identifier) {
        BigDecimal possession = obtainAmountOfItemsInPossessionFor(identifier);
        BigDecimal reserved = obtainAmountOfReservedItemsFor(identifier);
        return possession.subtract(reserved);
    }

    public BigDecimal obtainAmountOfReservedItemsFor(String identifier) {
        ItemEntry item = findReservedItemByIdentifier(identifier);
        if (null == item) {
            return BigDecimal.ZERO;
        }
        return item.getAmount();
    }

    public BigDecimal obtainAmountOfItemsInPossessionFor(String identifier) {
        ItemEntry item = findItemInPossession(identifier);
        if (null == item) {
            return BigDecimal.ZERO;
        }
        return item.getAmount();
    }

    public BigDecimal obtainMoneyToSpend() {
        return amountOfMoney.subtract(reservedAmountOfMoney);
    }

    public ItemEntry findReservedItemByIdentifier(String identifier) {
        return itemsReserved.get(identifier);
    }

    public ItemEntry findItemInPossession(String identifier) {
        return itemsInPossession.get(identifier);
    }

    public void addReservedItem(ItemEntry itemEntry) {
        handleAdd(itemsReserved, itemEntry);
    }

    public void addItemInPossession(ItemEntry itemEntry) {
        handleAdd(itemsInPossession, itemEntry);
    }

    public void removeReservedItem(String itemIdentifier, BigDecimal amount) {
        handleRemoveItem(itemsReserved, itemIdentifier, amount);
    }

    public void removeItemsInPossession(String itemIdentifier, BigDecimal amount) {
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

    public BigDecimal getAmountOfMoney() {
        return amountOfMoney;
    }

    public void setAmountOfMoney(BigDecimal amountOfMoney) {
        this.amountOfMoney = amountOfMoney;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public BigDecimal getReservedAmountOfMoney() {
        return reservedAmountOfMoney;
    }

    public void setReservedAmountOfMoney(BigDecimal reservedAmountOfMoney) {
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
        if (items.containsKey(itemEntry.getIdentifier())) {
            ItemEntry foundEntry = items.get(itemEntry.getIdentifier());
            foundEntry.setAmount(foundEntry.getAmount().add(itemEntry.getAmount()));
        } else {
            items.put(itemEntry.getIdentifier(), itemEntry);
        }
    }

    private void handleRemoveItem(Map<String, ItemEntry> items, String itemIdentifier, BigDecimal amount) {
        if (items.containsKey(itemIdentifier)) {
            ItemEntry foundEntry = items.get(itemIdentifier);
            foundEntry.setAmount(foundEntry.getAmount().subtract(amount));
            if (foundEntry.getAmount().compareTo(lowestPrice)<0) {
                items.remove(foundEntry.getIdentifier());
            }
        }
    }

    @Override
    public String toString() {
        return "PortfolioEntry{" +
                "amountOfMoney=" + amountOfMoney +
                ", identifier='" + identifier + '\'' +
                ", userIdentifier='" + userIdentifier + '\'' +
                ", userName='" + userName + '\'' +
                ", reservedAmountOfMoney=" + reservedAmountOfMoney +
                ", itemsInPossession=" + itemsInPossession +
                ", itemsReserved=" + itemsReserved +
                '}';
    }
}
