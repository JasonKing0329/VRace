package com.king.app.vrace.page.adapter;

import android.graphics.Color;

import com.king.app.vrace.R;
import com.king.app.vrace.model.entity.LegTeam;
import com.king.app.vrace.model.entity.Season;
import com.king.app.vrace.view.widget.RankChartAdapter;
import com.king.app.vrace.viewmodel.bean.TeamChartBean;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/4 14:30
 */
public class TeamSeasonChartAdapter extends RankChartAdapter {

    private TeamChartBean mChartBean;

    private int[] colors = new int[] {
            Color.parseColor("#f1303d"), Color.parseColor("#375BF1"), Color.parseColor("#34A350")
    };

    public void setChartBean(TeamChartBean mChartBean) {
        this.mChartBean = mChartBean;
    }

    @Override
    public int getXAxisCount() {
        return mChartBean.getLegList().size();
    }

    @Override
    public String getXAxisName(int position) {
        return "L" + mChartBean.getLegList().get(position).getIndex();
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
        if (mChartBean.getTeamList().size() > 1) {
            return colors[position];
        }
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
        if (xIndex == mChartBean.getTeamResultList().get(lineIndex).size() - 1) {
            Season season = mChartBean.getSeasonList().get(lineIndex);
            return "S" + season.getIndex();
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
