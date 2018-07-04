package com.king.app.vrace.viewmodel.bean;

import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.LegTeam;
import com.king.app.vrace.model.entity.Team;
import com.king.app.vrace.model.entity.TeamSeason;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/4 14:35
 */
public class TeamChartBean {

    private List<Leg> legList;

    private List<Team> teamList;

    private List<List<LegTeam>> teamResultList;

    private List<Integer> yValueList;

    public List<Leg> getLegList() {
        return legList;
    }

    public void setLegList(List<Leg> legList) {
        this.legList = legList;
    }

    public List<Team> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<Team> teamList) {
        this.teamList = teamList;
    }

    public List<List<LegTeam>> getTeamResultList() {
        return teamResultList;
    }

    public void setTeamResultList(List<List<LegTeam>> teamResultList) {
        this.teamResultList = teamResultList;
    }

    public List<Integer> getyValueList() {
        return yValueList;
    }

    public void setyValueList(List<Integer> yValueList) {
        this.yValueList = yValueList;
    }
}
