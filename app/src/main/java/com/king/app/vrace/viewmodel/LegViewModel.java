package com.king.app.vrace.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseViewModel;
import com.king.app.vrace.conf.LegType;
import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.LegDao;
import com.king.app.vrace.model.entity.LegTeam;
import com.king.app.vrace.model.entity.LegTeamDao;
import com.king.app.vrace.model.entity.Team;
import com.king.app.vrace.model.entity.TeamSeason;
import com.king.app.vrace.model.entity.TeamSeasonDao;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/25 11:18
 */
public class LegViewModel extends BaseViewModel {

    public MutableLiveData<Leg> legObserver = new MutableLiveData<>();

    public MutableLiveData<List<Team>> teamsObserver = new MutableLiveData<>();

    public MutableLiveData<List<Object>> ranksObserver = new MutableLiveData<>();

    public ObservableField<String> placeText = new ObservableField<>();
    public ObservableField<String> typeText = new ObservableField<>();

    private List<Object> mPageList;

    private Leg mLeg;

    public LegViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadLegs(long legId) {
        queryLeg(legId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Leg>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Leg leg) {
                        mLeg = leg;

                        String legTypes[] = getApplication().getResources().getStringArray(R.array.leg_type);
                        typeText.set(legTypes[leg.getType()]);
                        String place = getLegPlace(leg);
                        placeText.set(place);
                        legObserver.setValue(leg);

                        loadTeams();
                        loadTeamRank();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private String getLegPlace(Leg leg) {

        StringBuffer place = new StringBuffer();
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
        return place.toString();
    }

    private Observable<Leg> queryLeg(long legId) {
        return Observable.create(e -> e.onNext(getDaoSession().getLegDao().queryBuilder()
            .where(LegDao.Properties.Id.eq(legId))
            .build().uniqueOrThrow()));
    }

    private void loadTeams() {
        queryTeams()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Team>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Team> teams) {
                        teamsObserver.setValue(teams);
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

    private Observable<List<Team>> queryTeams() {
        return Observable.create(e -> {
            List<Team> list = new ArrayList<>();
            if (mLeg.getPlayerNumber() == mLeg.getTeamList().size()) {
                for (LegTeam lt:mLeg.getTeamList()) {
                    list.add(lt.getTeam());
                }
            }
            else {
                List<TeamSeason> teams = getDaoSession().getTeamSeasonDao().queryBuilder()
                        .where(TeamSeasonDao.Properties.SeasonId.eq(mLeg.getSeasonId()))
                        .build().list();
                // except eliminated team before current leg
                for (TeamSeason team:teams) {
                    // FIXME 验证方法暂未实现
//                    LegTeam legTeam = getDaoSession().getLegTeamDao().queryBuilder()
//                            .where(LegTeamDao.Properties.LegId.eq(team.getId()))
//                            .where(LegTeamDao.Properties.TeamId.eq(team.getId()))
//                            .where(LegTeamDao.Properties.Eliminated.eq(true))
//                            .build().unique();
//                    if (legTeam != null && legTeam.getLeg().getIndex() < mLeg.getIndex()) {
//                        continue;
//                    }
                    list.add(team.getTeam());
                }
            }
            e.onNext(list);
        });
    }

    private void loadTeamRank() {
        queryRankTeams()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<LegTeam>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<LegTeam> legTeams) {
                        List<Object> list = new ArrayList<>();
                        list.add(mLeg);
                        list.addAll(legTeams);
                        mPageList = list;
                        ranksObserver.setValue(mPageList);
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

    private Observable<List<LegTeam>> queryRankTeams() {
        return Observable.create(e -> {
            List<LegTeam> list = new ArrayList<>();
            for (int i = 0; i < mLeg.getPlayerNumber(); i ++) {
                LegTeam legTeam = null;
                for (int j = 0; j < mLeg.getTeamList().size(); j ++) {
                    if (mLeg.getTeamList().get(j).getPosition() == i + 1) {
                        legTeam = mLeg.getTeamList().get(j);
                        break;
                    }
                }
                if (legTeam == null) {
                    legTeam = new LegTeam();
                }
                legTeam.setPosition(i + 1);
                legTeam.setLegId(mLeg.getId());
                legTeam.setIsLast(i == mLeg.getPlayerNumber() - 1);
                if (mLeg.getType() == LegType.EL.ordinal()) {
                    if (legTeam.getIsLast()) {
                        legTeam.setEliminated(true);
                    }
                }
                else if (mLeg.getType() == LegType.DEL.ordinal()) {
                    if (i >= mLeg.getPlayerNumber() - 2) {
                        legTeam.setEliminated(true);
                    }
                }
                list.add(legTeam);
            }
            e.onNext(list);
        });
    }

    public void updateTeam(LegTeam selectedItem, Team data) {
        if (selectedItem != null) {
            selectedItem.setTeamId(data.getId());
            getDaoSession().getLegTeamDao().insertOrReplace(selectedItem);
            // 必须相关的都detach，否则不刷新
            getDaoSession().getLegTeamDao().detachAll();
            getDaoSession().getLegDao().detachAll();
        }
    }

    public boolean isLastItem(int position) {
        if (mPageList != null) {
            return position == mPageList.size() - 1;
        }
        return false;
    }
}
