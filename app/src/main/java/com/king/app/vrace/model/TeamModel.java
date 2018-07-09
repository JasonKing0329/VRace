package com.king.app.vrace.model;

import com.king.app.vrace.base.RaceApplication;
import com.king.app.vrace.conf.LegType;
import com.king.app.vrace.model.bean.TeamResult;
import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.LegDao;
import com.king.app.vrace.model.entity.LegTeam;
import com.king.app.vrace.model.entity.LegTeamDao;
import com.king.app.vrace.model.entity.Season;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/3 11:10
 */
public class TeamModel {

    public TeamResult getTeamSeasonResults(long teamId, long seasonId) {
        TeamResult result = new TeamResult();
        List<LegTeam> legTeams = RaceApplication.getInstance().getDaoSession().getLegTeamDao().queryBuilder()
                .where(LegTeamDao.Properties.TeamId.eq(teamId))
                .where(LegTeamDao.Properties.SeasonId.eq(seasonId))
                .build().list();
        int sum = 0;
        int max = 0;
        int min = Integer.MAX_VALUE;
        int legs = legTeams.size();
        for (LegTeam lt:legTeams) {
            if (lt.getLeg().getIndex() == 0) {
                legs --;
                continue;
            }
            if (lt.getEliminated()) {
                result.setEndRank(lt.getLeg().getIndex() + " Legs");
                result.setEndPosition(lt.getPosition());
            }
            else if (lt.getLeg().getType() == LegType.FINAL.ordinal()) {
                switch (lt.getPosition()) {
                    case 1:
                        result.setEndRank("Winner");
                        result.setEndPosition(1);
                        break;
                    case 2:
                        result.setEndRank("2nd");
                        result.setEndPosition(2);
                        break;
                    case 3:
                        result.setEndRank("3rd");
                        result.setEndPosition(3);
                        break;
                    case 4:
                        result.setEndRank("4th");
                        result.setEndPosition(4);
                        break;
                }
            }
            if (lt.getPosition() > max) {
                max = lt.getPosition();
            }
            if (lt.getPosition() < min) {
                min = lt.getPosition();
            }
            sum += lt.getPosition();
        }
        result.setHighPosition(min);
        result.setLowPosition(max);
        // 赛段数大于5，去掉1最高去掉1最低取平均；小于等于5，全部取平均
        if (legs > 5) {
            result.setPoint((double) (sum - max - min) / (double) (legs - 2));
        }
        // 还处于未编辑状态
        else if (legs == 0) {
            result.setPoint(0);
        }
        else {
            result.setPoint((double) sum / (double) legs);
        }

        // 计算赛冠
        long count = RaceApplication.getInstance().getDaoSession().getLegTeamDao().queryBuilder()
                .where(LegTeamDao.Properties.TeamId.eq(teamId))
                .where(LegTeamDao.Properties.SeasonId.eq(seasonId))
                .where(LegTeamDao.Properties.Position.eq(1))
                .buildCount().count();
        result.setChampions((int) count);
        return result;
    }

    public LegTeam getFirstEliminatedTeam(Season season) {
        LegTeam team;

        // start line elimination
        Leg leg0 = getSeasonLeg(season.getId(), 0);
        if (leg0.getType() == LegType.START_LINE_EL.ordinal()) {
            team = RaceApplication.getInstance().getDaoSession().getLegTeamDao().queryBuilder()
                    .where(LegTeamDao.Properties.SeasonId.eq(season.getId()))
                    .where(LegTeamDao.Properties.LegId.eq(leg0.getId()))
                    .where(LegTeamDao.Properties.Eliminated.eq(1))
                    .build().unique();
            return team;
        }

        // normal elimination in first leg
        Leg leg1 = getSeasonLeg(season.getId(), 1);
        if (leg1.getType() != LegType.NEL.ordinal() && leg1.getType() != LegType.DLL.ordinal()) {
            team = RaceApplication.getInstance().getDaoSession().getLegTeamDao().queryBuilder()
                    .where(LegTeamDao.Properties.SeasonId.eq(season.getId()))
                    .where(LegTeamDao.Properties.LegId.eq(leg1.getId()))
                    .where(LegTeamDao.Properties.IsLast.eq(1))
                    .where(LegTeamDao.Properties.Eliminated.eq(1))
                    .build().unique();
            return team;
        }

        // NEL, TBC
        Leg leg2 = getSeasonLeg(season.getId(), 2);
        team = RaceApplication.getInstance().getDaoSession().getLegTeamDao().queryBuilder()
                .where(LegTeamDao.Properties.SeasonId.eq(season.getId()))
                .where(LegTeamDao.Properties.LegId.eq(leg2.getId()))
                .where(LegTeamDao.Properties.IsLast.eq(1))
                .where(LegTeamDao.Properties.Eliminated.eq(1))
                .build().unique();
        return team;
    }

    private Leg getSeasonLeg(long seasonId, int index) {
        return RaceApplication.getInstance().getDaoSession().getLegDao().queryBuilder()
                .where(LegDao.Properties.SeasonId.eq(seasonId))
                .where(LegDao.Properties.Index.eq(index))
                .build().unique();
    }
}
