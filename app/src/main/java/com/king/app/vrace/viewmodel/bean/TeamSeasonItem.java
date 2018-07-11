package com.king.app.vrace.viewmodel.bean;

import com.king.app.vrace.model.entity.Season;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/11 16:50
 */
public class TeamSeasonItem {

    private String season;

    private String result;

    private Season bean;

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Season getBean() {
        return bean;
    }

    public void setBean(Season bean) {
        this.bean = bean;
    }
}
