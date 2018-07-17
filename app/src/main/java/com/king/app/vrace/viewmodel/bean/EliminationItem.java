package com.king.app.vrace.viewmodel.bean;

import com.king.app.vrace.model.entity.EliminationReason;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/17 14:15
 */
public class EliminationItem {

    private String name;

    private int number;

    private EliminationReason bean;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public EliminationReason getBean() {
        return bean;
    }

    public void setBean(EliminationReason bean) {
        this.bean = bean;
    }
}
