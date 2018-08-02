package com.king.app.vrace.viewmodel.bean;

import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.Season;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/1 17:50
 */
public class CountryItem {

    private String seasonText;

    private String legText;

    private String previousText;

    private String nextText;

    private Season season;

    private List<Leg> legList;

    private Leg previousLeg;

    private Leg nextLeg;

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public List<Leg> getLegList() {
        return legList;
    }

    public void setLegList(List<Leg> legList) {
        this.legList = legList;
    }

    public Leg getPreviousLeg() {
        return previousLeg;
    }

    public void setPreviousLeg(Leg previousLeg) {
        this.previousLeg = previousLeg;
    }

    public Leg getNextLeg() {
        return nextLeg;
    }

    public void setNextLeg(Leg nextLeg) {
        this.nextLeg = nextLeg;
    }

    public String getSeasonText() {
        return seasonText;
    }

    public void setSeasonText(String seasonText) {
        this.seasonText = seasonText;
    }

    public String getLegText() {
        return legText;
    }

    public void setLegText(String legText) {
        this.legText = legText;
    }

    public String getPreviousText() {
        return previousText;
    }

    public void setPreviousText(String previousText) {
        this.previousText = previousText;
    }

    public String getNextText() {
        return nextText;
    }

    public void setNextText(String nextText) {
        this.nextText = nextText;
    }
}
