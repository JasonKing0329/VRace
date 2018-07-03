package com.king.app.vrace.viewmodel.bean;

import com.king.app.vrace.model.bean.TeamResult;
import com.king.app.vrace.model.entity.Season;
import com.king.app.vrace.model.entity.Team;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/22 17:30
 */
public class TeamListItem {

    private Team bean;

    private String name;

    private String gender;

    private String relationship;

    private String place;

    private String occupy;

    private TeamResult result;

    private Season season;

    public Team getBean() {
        return bean;
    }

    public void setBean(Team bean) {
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

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public TeamResult getResult() {
        return result;
    }

    public void setResult(TeamResult result) {
        this.result = result;
    }
}
