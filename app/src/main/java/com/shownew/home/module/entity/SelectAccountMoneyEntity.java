package com.shownew.home.module.entity;

/**
 * @author Jason
 * @version 1.0
 * @date 2017/4/21 0021
 */

public class SelectAccountMoneyEntity {
    private String money;
    private boolean isSelect;

    public SelectAccountMoneyEntity(String money, boolean isSelect) {
        this.money = money;
        this.isSelect = isSelect;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
