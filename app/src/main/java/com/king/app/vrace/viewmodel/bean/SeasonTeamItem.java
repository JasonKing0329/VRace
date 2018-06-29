package com.king.app.vrace.viewmodel.bean;

import com.king.app.vrace.model.entity.TeamSeason;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/22 17:30
 */
public class SeasonTeamItem {

    private TeamSeason bean;

    private String epSeq;

    private String name;

    private String gender;

    private String relationship;

    private String place;

    private String occupy;

    private String result;

    private double point;

    public TeamSeason getBean() {
        return bean;
    }

    public void setBean(TeamSeason bean) {
        this.bean = bean;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getOccupy() {
        return occupy;
    }

    public void setOccupy(String occupy) {
        this.occupy = occupy;
    }

    public String getEpSeq() {
        return epSeq;
    }

    public void setEpSeq(String epSeq) {
        this.epSeq = epSeq;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = point;
    }
}
