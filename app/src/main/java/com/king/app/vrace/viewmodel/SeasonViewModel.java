package com.king.app.vrace.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.king.app.vrace.base.BaseViewModel;
import com.king.app.vrace.conf.AppConstants;
import com.king.app.vrace.conf.GenderType;
import com.king.app.vrace.conf.LegType;
import com.king.app.vrace.model.ImageProvider;
import com.king.app.vrace.model.TeamModel;
import com.king.app.vrace.model.bean.TeamResult;
import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.LegDao;
import com.king.app.vrace.model.entity.LegPlacesDao;
import com.king.app.vrace.model.entity.LegSpecialDao;
import com.king.app.vrace.model.entity.LegTeam;
import com.king.app.vrace.model.entity.LegTeamDao;
import com.king.app.vrace.model.entity.Season;
import com.king.app.vrace.model.entity.SeasonDao;
import com.king.app.vrace.model.entity.TeamSeason;
import com.king.app.vrace.model.entity.TeamSeasonDao;
import com.king.app.vrace.utils.PlaceUtil;
import com.king.app.vrace.viewmodel.bean.LegItem;
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

    public MutableLiveData<List<LegItem>> legsObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> deleteTeamsObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> deleteLegsObserver = new MutableLiveData<>();

    public MutableLiveData<Season> seasonObserver = new MutableLiveData<>();

    private Map<Long, Boolean> mLegCheckMap;

    private Map<Long, Boolean> mTeamCheckMap;

    private Season mSeason;

    private TeamModel teamModel;

    public SeasonViewModel(@NonNull Application application) {
        super(application);
        mLegCheckMap = new HashMap<>();
        mTeamCheckMap = new HashMap<>();
        teamModel = new TeamModel();
    }

    public Season getSeason() {
        return mSeason;
    }

    public Map<Long, Boolean> getLegCheckMap() {
        return mLegCheckMap;
    }

    public Map<Long, Boolean> getTeamCheckMap() {
        return mTeamCheckMap;
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
                String name = team.getTeam().getCode();
                // all star team has more than 1 season
                if (team.getTeam().getSeasonList().size() > 1) {
                    name = "";
                    for (TeamSeason season:team.getTeam().getSeasonList()) {
                        if (season.getSeason().getIndex() < team.getSeason().getIndex()) {
                            name = name + ",S" + season.getSeason().getIndex();
                        }
                    }
                    if (name.startsWith(",")) {
                        name = name.substring(1);
                    }
                    name = name + "\n" + team.getTeam().getCode();
                }
                item.setName(name);
                item.setGender(AppConstants.getGenderText(GenderType.values()[team.getTeam().getGenderType()]));
                item.setRelationship(team.getTeam().getRelationship().getName());
                TeamResult teamResult = teamModel.getTeamSeasonResults(team.getTeamId(), team.getSeasonId());
                item.setPoint(teamResult.getPoint());
                item.setResult(teamResult.getEndRank());
                item.setPlace(PlaceUtil.getCombinePlace(team.getTeam().getProvince(), team.getTeam().getCity()));
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
            executeTeam();
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
                        deleteTeamsObserver.setValue(true);
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

    private void executeTeam() {
        Iterator<Long> it = mTeamCheckMap.keySet().iterator();
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

    public void loadLegs(long seasonId) {
        querySeason(seasonId)
                .flatMap(season -> {
                    mSeason = season;
                    seasonObserver.postValue(season);
                    return queryLegs(mSeason.getId());
                })
                .flatMap(list -> toLegItems(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<LegItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<LegItem> legItems) {
                        legsObserver.setValue(legItems);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private Observable<List<Leg>> queryLegs(long seasonId) {
        return Observable.create(e -> e.onNext(getDaoSession().getLegDao().queryBuilder()
                .where(LegDao.Properties.SeasonId.eq(seasonId))
                .orderAsc(LegDao.Properties.Index)
                .build().list()));
    }

    private Observable<List<LegItem>> toLegItems(List<Leg> teams) {
        return Observable.create(e -> {
            List<LegItem> list = new ArrayList<>();
            for (Leg leg:teams) {
                LegItem item = new LegItem();
                item.setBean(leg);
                if (leg.getIndex() == 0) {
                    item.setIndex("Start Line");
                }
                else {
                    item.setIndex("Leg " + leg.getIndex());
                }
                if (leg.getPlayerNumber() < 4) {
                    item.setPlayers("Final " + leg.getPlayerNumber());
                }
                else {
                    item.setPlayers(leg.getPlayerNumber() + " teams");
                }
                StringBuffer place = new StringBuffer();
                // 第0个为主要国家/城市（决定封面的国家/城市）
                if (leg.getPlaceList().size() > 0) {
                    place.append(leg.getPlaceList().get(0).getCountry());
                    if (!TextUtils.isEmpty(leg.getPlaceList().get(0).getCity())) {
                        place.append("/").append(leg.getPlaceList().get(0).getCity());
                    }
                }
                if (leg.getPlaceList().size() > 1) {
                    place.append(" --> ").append(leg.getPlaceList().get(1).getCountry());
                    if (!TextUtils.isEmpty(leg.getPlaceList().get(1).getCity())) {
                        place.append("/").append(leg.getPlaceList().get(1).getCity());
                    }
                }
                if (leg.getPlaceList().size() > 2) {
                    place.append(" --> ").append(leg.getPlaceList().get(2).getCountry());
                    if (!TextUtils.isEmpty(leg.getPlaceList().get(2).getCity())) {
                        place.append("/").append(leg.getPlaceList().get(2).getCity());
                    }
                }
                item.setPlace(place.toString());
                item.setType(LegType.values()[leg.getType()]);
                item.setImagePath(ImageProvider.getLegImagePath(leg));
                list.add(item);
            }
            e.onNext(list);
        });
    }

    public void deleteLegs() {
        Observable.create(e -> getDaoSession().runInTx(() -> {
            executeDeleteLeg();
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
                        deleteLegsObserver.setValue(true);
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

    private void executeDeleteLeg() {
        Iterator<Long> it = mLegCheckMap.keySet().iterator();
        while (it.hasNext()) {
            long legId = it.next();
            // delete from team_season
            getDaoSession().getLegDao().queryBuilder()
                    .where(LegDao.Properties.Id.eq(legId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getLegDao().detachAll();
            // delete from leg_place
            getDaoSession().getLegPlacesDao().queryBuilder()
                    .where(LegPlacesDao.Properties.LegId.eq(legId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getLegPlacesDao().detachAll();
            // delete from leg_place
            getDaoSession().getLegSpecialDao().queryBuilder()
                    .where(LegSpecialDao.Properties.LegId.eq(legId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getLegSpecialDao().detachAll();
            // delete from leg_team
            getDaoSession().getLegTeamDao().queryBuilder()
                    .where(LegTeamDao.Properties.LegId.eq(legId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getLegTeamDao().detachAll();
        }
    }

}
