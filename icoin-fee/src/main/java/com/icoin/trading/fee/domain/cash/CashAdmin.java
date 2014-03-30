package com.icoin.trading.fee.domain.cash;

import com.homhon.mongo.domainsupport.modelsupport.entity.VersionedEntitySupport;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-29
 * Time: AM12:18
 * To change this template use File | Settings | File Templates.
 */
public class CashAdmin extends VersionedEntitySupport<CashAdmin, String, Integer> {
    private boolean canAddMoney;
    private boolean canAddCoin;
    private boolean canWithdrawMoney;
    private boolean canWithdrawCoin;

    private boolean isCanAddMoney() {
        return canAddMoney;
    }

    public boolean canAddMoney() {
        return canAddMoney;
    }

    public void preventAddingMoney() {
        canAddMoney = false;
    }

    public void enableAddingMoney() {
        canAddMoney = true;
    }

    private void setCanAddMoney(boolean canAddMoney) {
        this.canAddMoney = canAddMoney;
    }

    private boolean isCanAddCoin() {
        return canAddCoin;
    }

    public boolean canAddCoin() {
        return canAddCoin;
    }

    public void preventAddingCoin() {
        canAddCoin = false;
    }

    public void enableAddingCoin() {
        canAddCoin = true;
    }

    private void setCanAddCoin(boolean canAddCoin) {
        this.canAddCoin = canAddCoin;
    }

    private boolean isCanWithdrawMoney() {
        return canWithdrawMoney;
    }

    public boolean canWithdrawMoney() {
        return canWithdrawMoney;
    }

    private void setCanWithdrawMoney(boolean canWithdrawMoney) {
        this.canWithdrawMoney = canWithdrawMoney;
    }

    public void preventWithdrawingMoney() {
        canWithdrawMoney = false;
    }

    public void enableWithdrawingMoney() {
        canWithdrawMoney = true;
    }

    private boolean isCanWithdrawCoin() {
        return canWithdrawCoin;
    }

    public boolean canWithdrawCoin() {
        return canWithdrawCoin;
    }

    private void setCanWithdrawCoin(boolean canWithdrawCoin) {
        this.canWithdrawCoin = canWithdrawCoin;
    }

    public void preventWithdrawingCoin() {
        canWithdrawCoin = false;
    }

    public void enableWithdrawingCoin() {
        canWithdrawCoin = true;
    }
}
