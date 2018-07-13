package com.king.app.vrace.page.adapter;

import android.graphics.Color;

import com.king.app.vrace.conf.AppConstants;
import com.king.app.vrace.model.entity.LegTeam;
import com.king.app.vrace.model.setting.SettingProperty;
import com.king.app.vrace.view.widget.RankChartAdapter;
import com.king.app.vrace.viewmodel.bean.TeamChartBean;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/4 14:30
 */
public class TeamChartAdapter extends RankChartAdapter {

    private TeamChartBean mChartBean;

    public void setChartBean(TeamChartBean mChartBean) {
        this.mChartBean = mChartBean;
    }

    @Override
    public int getXAxisCount() {
        return mChartBean.getLegList().size();
    }

    @Override
    public String getXAxisName(int position) {
        String place = mChartBean.getLegList().get(position).getPlaceList().get(0).getCountry();
        if (SettingProperty.getDatabaseType() == AppConstants.DATABASE_REAL) {
            if (place.equals("美国")) {
                place = mChartBean.getLegList().get(position).getPlaceList().get(0).getCity();
            }
        }
        else {
            if (place.equals("中国")) {
                place = mChartBean.getLegList().get(position).getPlaceList().get(0).getCity();
            }
        }
        return "L" + mChartBean.getLegList().get(position).getIndex() + "\n"
                + place;
    }

    @Override
    public int getYAxisCount() {
        return mChartBean.getyValueList().size();
    }

    @Override
    public String getYAxisName(int position) {
        return String.valueOf(mChartBean.getyValueList().get(position));
    }

    @Override
    public Integer getYAxisValue(int i) {
        return mChartBean.getyValueList().get(i);
    }

    @Override
    public int getLineCount() {
        return mChartBean.getTeamList().size();
    }

    @Override
    public int getLineColor(int position) {
        int color = mChartBean.getTeamList().get(position).getSpecialColor();
        if (color == Color.WHITE) {
            color = Color.parseColor("#333333");
        }
        return color;
    }

    @Override
    public Integer getValue(int lineIndex, int xIndex) {
        try {
            // 有的season第0赛段没有排名
            int legIndex = xIndex;
            LegTeam legTeam = findLegTeam(lineIndex, legIndex);
            if (legTeam == null) {
                return null;
            }
            else {
                return legTeam.getPosition();
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getText(int lineIndex, int xIndex) {
        if (xIndex == getXAxisCount() - 1) {
            return mChartBean.getTeamList().get(lineIndex).getCode();
        }
        return null;
    }

    private LegTeam findLegTeam(int lineIndex, int legIndex) {
        List<LegTeam> legTeams = mChartBean.getTeamResultList().get(lineIndex);
        for (int i = 0; i < legTeams.size(); i ++) {
            if (legTeams.get(i).getLeg().getIndex() == legIndex) {
                return legTeams.get(i);
            }
        }
        return null;
    }
}
