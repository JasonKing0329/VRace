package com.king.app.vrace.viewmodel.bean;

import com.king.app.vrace.view.widget.ResultsTableView;

import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/6/26 0026 20:36
 */

public class TableData {
    private List<ResultsTableView.CellData> legTitleList;
    private List<ResultsTableView.CellData> teamList;
    private List<ResultsTableView.CellData> relationshipList;
    private ResultsTableView.CellData[][] legResults;

    private int columnTeamColor;
    private int titleBgColor;

    public List<ResultsTableView.CellData> getLegTitleList() {
        return legTitleList;
    }

    public void setLegTitleList(List<ResultsTableView.CellData> legTitleList) {
        this.legTitleList = legTitleList;
    }

    public List<ResultsTableView.CellData> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<ResultsTableView.CellData> teamList) {
        this.teamList = teamList;
    }

    public List<ResultsTableView.CellData> getRelationshipList() {
        return relationshipList;
    }

    public void setRelationshipList(List<ResultsTableView.CellData> relationshipList) {
        this.relationshipList = relationshipList;
    }

    public ResultsTableView.CellData[][] getLegResults() {
        return legResults;
    }

    public void setLegResults(ResultsTableView.CellData[][] legResults) {
        this.legResults = legResults;
    }

    public int getColumnTeamColor() {
        return columnTeamColor;
    }

    public void setColumnTeamColor(int columnTeamColor) {
        this.columnTeamColor = columnTeamColor;
    }

    public int getTitleBgColor() {
        return titleBgColor;
    }

    public void setTitleBgColor(int titleBgColor) {
        this.titleBgColor = titleBgColor;
    }
}
