package com.king.app.vrace.viewmodel.bean;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/28 17:09
 */
public class StatisticPlaceItem {

    private String country;

    private int count;

    private String seasons;

    private String flagPath;

    private String continent;

    private long lastSeasonId;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public String getFlagPath() {
        return flagPath;
    }

    public void setFlagPath(String flagPath) {
        this.flagPath = flagPath;
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
}
