package com.king.app.vrace.viewmodel.bean;

import com.king.app.vrace.model.entity.LegTeam;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/28 17:09
 */
public class StatisticWinnerItem {

    private String type;

    private int count;

    private String seasons;

    private int sortValue;

    private List<LegTeam> legTeamList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSeasons() {
        return seasons;
    }

    public void setSeasons(String seasons) {
        this.seasons = seasons;
    }

    public int getSortValue() {
        return sortValue;
    }

    public void setSortValue(int sortValue) {
        this.sortValue = sortValue;
    }

    public List<LegTeam> getLegTeamList() {
        return legTeamList;
    }

    public void setLegTeamList(List<LegTeam> legTeamList) {
        this.legTeamList = legTeamList;
    }
}
