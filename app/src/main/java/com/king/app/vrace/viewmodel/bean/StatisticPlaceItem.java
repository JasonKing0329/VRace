package com.king.app.vrace.viewmodel.bean;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/28 17:09
 */
public class StatisticPlaceItem {

    private String place;

    private int count;

    private String seasons;

    private String imgPath;

    private String continent;

    private long lastSeasonId;

    private int firstSeasonIndex;

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
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

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public long getLastSeasonId() {
        return lastSeasonId;
    }

    public void setLastSeasonId(long lastSeasonId) {
        this.lastSeasonId = lastSeasonId;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public int getFirstSeasonIndex() {
        return firstSeasonIndex;
    }

    public void setFirstSeasonIndex(int firstSeasonIndex) {
        this.firstSeasonIndex = firstSeasonIndex;
    }
}
