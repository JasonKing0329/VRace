package com.king.app.vrace.model.bean;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/3 11:17
 */
public class TeamResult {

    private double point;

    private int champions;

    private int highPosition;

    private int lowPosition;

    private int endPosition;

    private String endRank;

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = point;
    }

    public int getChampions() {
        return champions;
    }

    public void setChampions(int champions) {
        this.champions = champions;
    }

    public int getHighPosition() {
        return highPosition;
    }

    public void setHighPosition(int highPosition) {
        this.highPosition = highPosition;
    }

    public int getLowPosition() {
        return lowPosition;
    }

    public void setLowPosition(int lowPosition) {
        this.lowPosition = lowPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public String getEndRank() {
        return endRank;
    }

    public void setEndRank(String endRank) {
        this.endRank = endRank;
    }
}
