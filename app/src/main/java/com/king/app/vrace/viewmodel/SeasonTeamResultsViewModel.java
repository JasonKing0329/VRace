package com.king.app.vrace.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableInt;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.king.app.vrace.base.BaseViewModel;
import com.king.app.vrace.conf.LegType;
import com.king.app.vrace.model.TeamModel;
import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.LegDao;
import com.king.app.vrace.model.entity.LegPlaces;
import com.king.app.vrace.model.entity.LegTeam;
import com.king.app.vrace.model.entity.LegTeamDao;
import com.king.app.vrace.model.entity.Season;
import com.king.app.vrace.model.entity.SeasonDao;
import com.king.app.vrace.model.entity.Team;
import com.king.app.vrace.model.entity.TeamSeason;
import com.king.app.vrace.model.entity.TeamSeasonDao;
import com.king.app.vrace.view.widget.ResultsTableView;
import com.king.app.vrace.viewmodel.bean.TableData;
import com.king.app.vrace.viewmodel.bean.TeamChartBean;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @desc
 * @auth 景阳
 * @time 2018/6/26 0026 20:00
 */

public class SeasonTeamResultsViewModel extends BaseViewModel {

    public MutableLiveData<Season> seasonObserver = new MutableLiveData<>();

    public MutableLiveData<TableData> dataObserver = new MutableLiveData<>();

    public MutableLiveData<TeamChartBean> chartObserver = new MutableLiveData<>();

    public ObservableInt tableVisibility = new ObservableInt();

    public ObservableInt chartVisibility = new ObservableInt();

    private TeamModel teamModel;

    private TeamChartBean mTeamChartBean;

    private Season mSeason;

    public SeasonTeamResultsViewModel(@NonNull Application application) {
        super(application);
        tableVisibility.set(View.VISIBLE);
        chartVisibility.set(View.GONE);
        teamModel = new TeamModel();
    }

    public void loadResults(long seasonId) {
        loadingObserver.setValue(true);
        loadSeason(seasonId)
                .flatMap(season -> {
                    mSeason = season;
                    seasonObserver.postValue(season);
                    return createTableData(seasonId);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<TableData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(TableData tableData) {
                        dataObserver.setValue(tableData);
                        loadChartData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<Season> loadSeason(long seasonId) {
        return Observable.create(e -> e.onNext(getDaoSession().getSeasonDao().queryBuilder().where(SeasonDao.Properties.Id.eq(seasonId)).build().unique()));
    }

    private Observable<TableData> createTableData(long seasonId) {
        return Observable.create(e -> {
            TableData data = new TableData();
            List<TeamSeason> teams = getDaoSession().getTeamSeasonDao().queryBuilder()
                    .where(TeamSeasonDao.Properties.SeasonId.eq(seasonId))
                    .build().list();
            List<TeamResults> teamResults = loadTeamResults(seasonId, teams);
            Collections.sort(teamResults, new ResultsComparator());
            data.setTeamList(new ArrayList<>());
            data.setRelationshipList(new ArrayList<>());

            int titleColor = Color.parseColor("#eaecf0");
            int cellColor = Color.parseColor("#f8f9fa");
            int titleTextColor = Color.parseColor("#333333");
            int normalTextColor = Color.parseColor("#666666");
            int colorEliminated = Color.parseColor("#fd5300");
            int colorLast = Color.parseColor("#0000ff");

            data.setColumnTeamColor(titleColor);
            data.setTitleBgColor(titleColor);
            for (TeamResults team:teamResults) {
                data.getTeamList().add(new ResultsTableView.CellData(team.team.getCode(), titleTextColor, 0));
                data.getRelationshipList().add(new ResultsTableView.CellData(team.team.getRelationship().getName(), titleTextColor, 0));
            }

            List<Leg> legs = getDaoSession().getLegDao().queryBuilder()
                    .where(LegDao.Properties.SeasonId.eq(seasonId))
                    .where(LegDao.Properties.Index.gt(0))
                    .build().list();
            data.setLegTitleList(new ArrayList<>());
            for (Leg leg:legs) {
                String legTitle = getLegTitle(leg);
                data.getLegTitleList().add(new ResultsTableView.CellData(legTitle, titleTextColor, titleColor));
            }
            ResultsTableView.CellData[][] results = new ResultsTableView.CellData[teams.size()][];
            data.setLegResults(results);
            for (int i = 0; i < results.length; i ++) {
                results[i] = new ResultsTableView.CellData[legs.size()];
            }
            for (int row = 0; row < teamResults.size(); row ++) {
                List<LegTeam> legTeams = teamResults.get(row).legTeams;
                for (int col = 0; col < legTeams.size(); col ++) {
                    ResultsTableView.CellData cellData = new ResultsTableView.CellData();
                    // 被淘汰标红
                    if (legTeams.get(col).getEliminated()) {
                        cellData.textColor = colorEliminated;
                    }
                    // 非淘汰、连续赛段最后一名
                    else if (legTeams.get(col).getIsLast() && legTeams.get(col).getLeg().getType() != LegType.FINAL.ordinal()) {
                        cellData.textColor = colorLast;
                    }
                    else {
                        cellData.textColor = normalTextColor;
                    }
                    cellData.background = cellColor;
                    cellData.text = formatRank(legTeams.get(col).getPosition());
                    results[row][legTeams.get(col).getLeg().getIndex() - 1] = cellData;
                }

            }
            e.onNext(data);
        });
    }

    private String getLegTitle(Leg leg) {
        String result = "L" + leg.getIndex();
        if (leg.getPlaceList().size() > 0) {
            LegPlaces place = leg.getPlaceList().get(0);
            if (TextUtils.isEmpty(place.getCity())) {
                result = result + "\n" + place.getCountry();
            }
            else {
                result = result + "\n" + place.getCountry() + "-" + place.getCity();
            }
        }
        return result;
    }

    private String formatRank(int rank) {
        return String.valueOf(rank);
    }

    private List<TeamResults> loadTeamResults(long seasonId, List<TeamSeason> teams) {
        List<TeamResults> results = new ArrayList<>();
        for (int i = 0; i < teams.size(); i ++) {
            List<LegTeam> legTeams = getDaoSession().getLegTeamDao().queryBuilder()
                    .where(LegTeamDao.Properties.SeasonId.eq(seasonId))
                    .where(LegTeamDao.Properties.TeamId.eq(teams.get(i).getTeam().getId()))
                    .build().list();
            TeamResults tr = new TeamResults();
            tr.team = teams.get(i).getTeam();
            tr.legTeams = new ArrayList<>();
            for (LegTeam lt:legTeams) {
                if (lt.getLeg().getIndex() == 0) {
                    continue;
                }
                tr.legTeams.add(lt);
                // 进入final，直接取position
                if (lt.getLeg().getType() == LegType.FINAL.ordinal()) {
                    tr.rank = lt.getPosition();
                    break;
                }
                // 如果被淘汰，淘汰站可确定名次
                if (lt.getEliminated()) {
                    // 双淘汰根据是否最后一名确定名次
                    if (lt.getLeg().getType() == LegType.DEL.ordinal() && !lt.getIsLast()) {
                        tr.rank = lt.getLeg().getPlayerNumber() - 1;
                    }
                    else {
                        tr.rank = lt.getLeg().getPlayerNumber();
                    }
                }
            }
            results.add(tr);
        }
        return results;
    }

    public void tableView() {
        if (tableVisibility.get() != View.VISIBLE) {
            chartVisibility.set(View.GONE);
            tableVisibility.set(View.VISIBLE);
        }
    }

    public void chartView() {
        if (chartVisibility.get() != View.VISIBLE) {
            tableVisibility.set(View.GONE);
            chartVisibility.set(View.VISIBLE);
        }
    }

    private void loadChartData() {
        getChartData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<TeamChartBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(TeamChartBean teamChartBean) {
                        loadingObserver.setValue(false);
                        mTeamChartBean = teamChartBean;
                        chartObserver.setValue(teamChartBean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<TeamChartBean> getChartData() {
        return Observable.create(e -> {
            TeamChartBean bean = new TeamChartBean();
            List<Leg> legs = getDaoSession().getLegDao().queryBuilder()
                    .where(LegDao.Properties.SeasonId.eq(mSeason.getId()))
                    .build().list();
            bean.setLegList(legs);

            List<TeamSeason> teams = getDaoSession().getTeamSeasonDao().queryBuilder()
                    .where(TeamSeasonDao.Properties.SeasonId.eq(mSeason.getId()))
                    .build().list();

            bean.setyValueList(new ArrayList<>());
            TeamLegComparator comparator = new TeamLegComparator();
            for (int i = 0; i < teams.size(); i ++) {
                bean.getyValueList().add(teams.size() - i);
            }

            bean.setTeamResultList(new ArrayList<>());
            bean.setTeamList(new ArrayList<>());
            // 实测全显示效果不好，只显示决赛队伍的走势
            QueryBuilder<LegTeam> builder = getDaoSession().getLegTeamDao().queryBuilder();
            builder.join(LegTeamDao.Properties.LegId, Leg.class)
                    .where(LegDao.Properties.Type.eq(LegType.FINAL.ordinal()));
            List<LegTeam> legTeams = builder
                    .where(LegTeamDao.Properties.SeasonId.eq(mSeason.getId()))
                    .build().list();
            for (int i = 0; i < legTeams.size(); i ++) {
                Team team = legTeams.get(i).getTeam();
                bean.getTeamList().add(team);
                List<LegTeam> lt = getDaoSession().getLegTeamDao().queryBuilder()
                        .where(LegTeamDao.Properties.SeasonId.eq(mSeason.getId()))
                        .where(LegTeamDao.Properties.TeamId.eq(team.getId()))
                        .build().list();
                Collections.sort(lt, comparator);
                bean.getTeamResultList().add(lt);
            }
            e.onNext(bean);
        });
    }

    public void preSeason() {
        Season season = getDaoSession().getSeasonDao().queryBuilder()
                .where(SeasonDao.Properties.Index.eq(mSeason.getIndex() - 1))
                .build().unique();
        if (season == null) {
            messageObserver.setValue("No previous season found");
        }
        else {
            loadResults(season.getId());
        }
    }

    public void nextSeason() {
        Season season = getDaoSession().getSeasonDao().queryBuilder()
                .where(SeasonDao.Properties.Index.eq(mSeason.getIndex() + 1))
                .build().unique();
        if (season == null) {
            messageObserver.setValue("No next season found");
        }
        else {
            loadResults(season.getId());
        }
    }

    private class TeamResults {
        Team team;
        private List<LegTeam> legTeams;
        private int rank;
    }

    private class ResultsComparator implements Comparator<TeamResults> {

        @Override
        public int compare(TeamResults left, TeamResults right) {
            return left.rank - right.rank;
        }
    }

    private class TeamLegComparator implements Comparator<LegTeam> {

        @Override
        public int compare(LegTeam left, LegTeam right) {
            return left.getLeg().getIndex() - right.getLeg().getIndex();
        }
    }
}
