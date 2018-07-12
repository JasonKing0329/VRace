package com.king.app.vrace.viewmodel.bean;

import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.Season;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/5 15:46
 */
public class PlaceSeason {

    private String place;

    private Season season;

    private List<Leg> legs;

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }
}
