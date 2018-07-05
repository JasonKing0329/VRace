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

    private String country;

    private Season season;

    private List<Leg> legs;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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
