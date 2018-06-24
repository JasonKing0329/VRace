package com.king.app.vrace.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.king.app.vrace.base.BaseViewModel;
import com.king.app.vrace.conf.AppConstants;
import com.king.app.vrace.conf.GenderType;
import com.king.app.vrace.model.entity.Season;
import com.king.app.vrace.model.entity.SeasonDao;
import com.king.app.vrace.model.entity.TeamSeason;
import com.king.app.vrace.model.entity.TeamSeasonDao;
import com.king.app.vrace.viewmodel.bean.SeasonTeamItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @desc
 * @auth 景阳
 * @time 2018/6/24 0024 15:56
 */

public class SeasonViewModel extends BaseViewModel {

    public MutableLiveData<List<SeasonTeamItem>> teamsObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> deleteObserver = new MutableLiveData<>();

    public MutableLiveData<Season> seasonObserver = new MutableLiveData<>();

    private Map<Long, Boolean> mCheckMap;

    private Season mSeason;

    public SeasonViewModel(@NonNull Application application) {
        super(application);
        mCheckMap = new HashMap<>();
    }

    public Season getSeason() {
        return mSeason;
    }

    public Map<Long, Boolean> getCheckMap() {
        return mCheckMap;
    }

    public void loadSeason(long seasonId) {
        querySeason(seasonId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Season>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Season season) {
                        mSeason = season;
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void loadTeams(long seasonId) {
        loadingObserver.setValue(true);
        querySeason(seasonId)
                .flatMap(season -> {
                    mSeason = season;
                    seasonObserver.postValue(mSeason);
                    return querySeasonTeams(seasonId);
                })
                .flatMap(list -> toViewItems(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<SeasonTeamItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<SeasonTeamItem> seasonTeamItems) {
                        loadingObserver.setValue(false);
                        teamsObserver.setValue(seasonTeamItems);
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

    private Observable<Season> querySeason(long seasonId) {
        return Observable.create(e -> e.onNext(getDaoSession().getSeasonDao().queryBuilder()
            .where(SeasonDao.Properties.Id.eq(seasonId))
            .build().unique()));
    }

    private Observable<List<TeamSeason>> querySeasonTeams(long seasonId) {
        return Observable.create(e -> e.onNext(getDaoSession().getTeamSeasonDao().queryBuilder()
                .where(TeamSeasonDao.Properties.SeasonId.eq(seasonId))
                .orderAsc(TeamSeasonDao.Properties.EpisodeSeq)
                .build().list()));
    }

    private Observable<List<SeasonTeamItem>> toViewItems(List<TeamSeason> teams) {
        return Observable.create(e -> {
            List<SeasonTeamItem> list = new ArrayList<>();
            for (TeamSeason team:teams) {
                SeasonTeamItem item = new SeasonTeamItem();
                item.setEpSeq(String.valueOf(team.getEpisodeSeq()));
                item.setBean(team);
                item.setName(team.getTeam().getCode());
                item.setGender(AppConstants.getGenderText(GenderType.values()[team.getTeam().getGenderType()]));
                item.setRelationship(team.getTeam().getRelationship().getName());
                if (TextUtils.isEmpty(team.getTeam().getProvince())) {
                    if (!TextUtils.isEmpty(team.getTeam().getCity())) {
                        item.setPlace(team.getTeam().getCity());
                    }
                }
                else {
                    if (TextUtils.isEmpty(team.getTeam().getCity())) {
                        item.setPlace(team.getTeam().getProvince());
                    }
                    else {
                        if (team.getTeam().getCity().equals(team.getTeam().getProvince())) {
                            item.setPlace(team.getTeam().getCity());
                        }
                        else {
                            item.setPlace(team.getTeam().getProvince() + "/" + team.getTeam().getCity());
                        }
                    }
                }
                String occupy = null;
                if (team.getTeam().getPlayerList().size() > 0) {
                    String occupy1 = team.getTeam().getPlayerList().get(0).getOccupy();
                    String occupy2 = team.getTeam().getPlayerList().get(1).getOccupy();
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
            // delete from team_season
            getDaoSession().getTeamSeasonDao().queryBuilder()
                    .where(TeamSeasonDao.Properties.TeamId.eq(teamId))
                    .where(TeamSeasonDao.Properties.SeasonId.eq(mSeason.getId()))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getTeamSeasonDao().detachAll();
        }
    }
}
