package com.king.app.vrace.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.vrace.base.BaseViewModel;
import com.king.app.vrace.conf.AppConstants;
import com.king.app.vrace.conf.GenderType;
import com.king.app.vrace.model.entity.LegDao;
import com.king.app.vrace.model.entity.SeasonDao;
import com.king.app.vrace.model.entity.Team;
import com.king.app.vrace.model.entity.TeamDao;
import com.king.app.vrace.model.entity.TeamPlayersDao;
import com.king.app.vrace.model.entity.TeamSeasonDao;
import com.king.app.vrace.model.entity.TeamTagDao;
import com.king.app.vrace.viewmodel.bean.TeamListItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/22 17:01
 */
public class TeamListViewModel extends BaseViewModel {

    public MutableLiveData<List<TeamListItem>> teamsObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> deleteObserver = new MutableLiveData<>();

    private Map<Long, Boolean> mCheckMap;

    public TeamListViewModel(@NonNull Application application) {
        super(application);
    }

    public Map<Long, Boolean> getCheckMap() {
        return mCheckMap;
    }

    public void loadTeams() {
        loadingObserver.setValue(true);
        queryTeams()
                .flatMap(list -> sortItems(list))
                .flatMap(list -> toViewItems(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<TeamListItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<TeamListItem> teamListItems) {
                        loadingObserver.setValue(false);
                        teamsObserver.setValue(teamListItems);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<List<Team>> queryTeams() {
        return Observable.create(e -> e.onNext(getDaoSession().getTeamDao().loadAll()));
    }

    private Observable<List<Team>> sortItems(List<Team> teams) {
        return Observable.create(e -> {
            Collections.sort(teams, new TeamComparator());
            e.onNext(teams);
        });
    }

    private Observable<List<TeamListItem>> toViewItems(List<Team> teams) {
        return Observable.create(e -> {
            List<TeamListItem> list = new ArrayList<>();
            for (Team team:teams) {
                TeamListItem item = new TeamListItem();
                item.setBean(team);
                if (team.getSeasonList().size() > 0) {
                    item.setName("S" + team.getSeasonList().get(0).getSeason().getIndex() + "\n" + team.getCode());
                }
                else {
                    item.setName(team.getCode());
                }
                item.setGender(AppConstants.getGenderText(GenderType.values()[team.getGenderType()]));
                item.setRelationship(team.getRelationship().getName());
                item.setPlace(team.getProvince() + "/" + team.getCity());
                String occupy = null;
                if (team.getPlayerList().size() > 0) {
                    String occupy1 = team.getPlayerList().get(0).getOccupy();
                    String occupy2 = team.getPlayerList().get(1).getOccupy();
                    if (occupy1.equals(occupy2)) {
                        occupy = occupy1;
                    }
                    else {
                        occupy = occupy1 + "；" + occupy2;
                    }
                }
                item.setOccupy(occupy);
                list.add(item);
            }
            e.onNext(list);
        });
    }

    private class TeamComparator implements Comparator<Team> {

        @Override
        public int compare(Team left, Team right) {
            // 按season index排序
            int valueL = left.getSeasonList().size() > 0 ? left.getSeasonList().get(0).getSeason().getIndex() : Integer.MAX_VALUE;
            int valueR = left.getSeasonList().size() > 0 ? right.getSeasonList().get(0).getSeason().getIndex() : Integer.MAX_VALUE;
            return valueL - valueR;
        }
    }

    public void deleteTeams() {
        Observable.create(e -> getDaoSession().runInTx(() -> {
            executeDelete();
            e.onNext(new Object());
        }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Object object) {
                        loadingObserver.setValue(false);
                        deleteObserver.setValue(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void executeDelete() {
        Iterator<Long> it = mCheckMap.keySet().iterator();
        while (it.hasNext()) {
            long teamId = it.next();
            // delete from team
            getDaoSession().getTeamDao().queryBuilder()
                    .where(TeamDao.Properties.Id.eq(teamId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getTeamDao().detachAll();
            // delete from team_player
            getDaoSession().getTeamPlayersDao().queryBuilder()
                    .where(TeamPlayersDao.Properties.TeamId.eq(teamId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getTeamPlayersDao().detachAll();
            // delete from team_season
            getDaoSession().getSeasonDao().queryBuilder()
                    .where(TeamSeasonDao.Properties.TeamId.eq(teamId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getSeasonDao().detachAll();
            // delete from team_tag
            getDaoSession().getTeamTagDao().queryBuilder()
                    .where(TeamTagDao.Properties.TeamId.eq(teamId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getTeamTagDao().detachAll();
        }
    }
}
