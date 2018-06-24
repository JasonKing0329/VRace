package com.king.app.vrace.viewmodel.bean;

import com.king.app.vrace.conf.LegType;
import com.king.app.vrace.model.entity.Leg;

/**
 * @desc
 * @auth 景阳
 * @time 2018/6/24 0024 21:53
 */

public class LegItem {

    private Leg bean;

    private String index;

    private String place;

    private LegType type;

    private String players;

    public Leg getBean() {
        return bean;
    }

    public void setBean(Leg bean) {
        this.bean = bean;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public LegType getType() {
        return type;
    }

    public void setType(LegType type) {
        this.type = type;
    }

    public String getPlayers() {
        return players;
    }

    public void setPlayers(String players) {
        this.players = players;
    }
}
