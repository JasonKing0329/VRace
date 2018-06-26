package com.king.app.vrace.viewmodel.bean;

import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/6/26 0026 20:36
 */

public class TableData {
    private List<String> legTitleList;
    private List<String> teamList;
    private List<String> relationshipList;
    private String[][] legResults;

    public List<String> getLegTitleList() {
        return legTitleList;
    }

    public void setLegTitleList(List<String> legTitleList) {
        this.legTitleList = legTitleList;
    }

    public List<String> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<String> teamList) {
        this.teamList = teamList;
    }

    public List<String> getRelationshipList() {
        return relationshipList;
    }

    public void setRelationshipList(List<String> relationshipList) {
        this.relationshipList = relationshipList;
    }

    public String[][] getLegResults() {
        return legResults;
    }

    public void setLegResults(String[][] legResults) {
        this.legResults = legResults;
    }
}
