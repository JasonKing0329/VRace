package com.king.app.vrace.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseViewModel;
import com.king.app.vrace.conf.LegType;
import com.king.app.vrace.model.entity.EliminationReason;
import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.LegDao;
import com.king.app.vrace.model.entity.LegSpecial;
import com.king.app.vrace.model.entity.LegSpecialDao;
import com.king.app.vrace.model.entity.LegTeam;
import com.king.app.vrace.model.entity.LegTeamDao;
import com.king.app.vrace.model.entity.Team;
import com.king.app.vrace.model.entity.TeamElimination;
import com.king.app.vrace.model.entity.TeamEliminationDao;
import com.king.app.vrace.model.entity.TeamSeason;
import com.king.app.vrace.model.entity.TeamSeasonDao;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
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

    private LegTeam mTeamToEliminate;

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
                    LegTeam legTeam = getDaoSession().getLegTeamDao().queryBuilder()
                            .where(LegTeamDao.Properties.SeasonId.eq(mLeg.getSeasonId()))
                            .where(LegTeamDao.Properties.TeamId.eq(team.getTeamId()))
                            .where(LegTeamDao.Properties.Eliminated.eq(1))
                            .build().unique();
                    if (legTeam != null && legTeam.getLeg().getIndex() < mLeg.getIndex()) {
                        continue;
                    }
                    list.add(team.getTeam());
                }
            }
            e.onNext(list);
        });
    }

    private void loadTeamRank() {
        Observable<List<Object>> observable;
        // 起跑线无任务不加载rank list
        if (mLeg.getType() == LegType.START_LINE.ordinal()) {
            observable = toLegData(null);
        }
        else {
            observable = queryRankTeams()
                            .flatMap(list -> toLegData(list));
        }
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Object>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Object> list) {
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

    private Observable<List<Object>> toLegData(List<LegTeam> teamRanks) {
        return Observable.create(e -> {
            List<Object> list = new ArrayList<>();
            list.add(mLeg);
            if (teamRanks != null) {
                list.addAll(teamRanks);
            }
            e.onNext(list);
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
                legTeam.setSeasonId(mLeg.getSeasonId());
                legTeam.setIsLast(i == mLeg.getPlayerNumber() - 1);
                // 淘汰赛段与起跑线淘汰最后一名标记为淘汰
                if (mLeg.getType() == LegType.EL.ordinal() || mLeg.getType() == LegType.START_LINE_EL.ordinal()) {
                    if (legTeam.getIsLast()) {
                        legTeam.setEliminated(true);
                    }
                }
                // 双淘汰赛段最后两名标记为淘汰
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
            // 必须相关的都detach，并且通知mLeg刷新，否则很多时候会出现再次进入不刷新的bug
            getDaoSession().getLegTeamDao().detachAll();
            getDaoSession().getLegDao().detachAll();

            // 还必须通知mLeg刷新，例如如果不加这两句，进入一个新leg，
            // 新添加几个legteam，又编辑了leg的description，退出leg，再进入leg，legteam部分就一片空白，但是如果划掉任务重启app，添加过的legteam就出现了
            // 调试时发现leg实体里teamList的数量是0，所以应该是GreenDao的缓存机制造成的，mLeg取到了缓存里的Leg，关系数据没有更新
            mLeg.resetTeamList();
            mLeg.refresh();
        }
    }

    public boolean isLastItem(int position) {
        if (mPageList != null) {
            return position == mPageList.size() - 1;
        }
        return false;
    }

    public void deleteItem(LegTeam bean) {
        if (bean != null && bean.getId() != null) {
            if (bean.getEliminated()) {
                rememberTeamToEliminate(null);
            }
            bean.delete();
            getDaoSession().getLegTeamDao().detachAll();
            long legId = mLeg.getId();
            getDaoSession().getLegDao().detachAll();
            loadLegs(legId);
        }
    }

    public void addSpecialForLeg(int type) {
        LegSpecial special = getDaoSession().getLegSpecialDao().queryBuilder()
                .where(LegSpecialDao.Properties.LegId.eq(mLeg.getId()))
                .where(LegSpecialDao.Properties.SpecialType.eq(type))
                .build().unique();
        if (special == null) {
            special = new LegSpecial();
            special.setLegId(mLeg.getId());
            special.setSpecialType(type);
            getDaoSession().getLegSpecialDao().insert(special);
            getDaoSession().getLegSpecialDao().detachAll();
            getDaoSession().getLegDao().detachAll();
            mLeg.resetSpecialList();
            mLeg.refresh();
        }
    }

    public void removeSpecial(LegSpecial legSpecial) {
        getDaoSession().getLegSpecialDao().delete(legSpecial);
        getDaoSession().getLegSpecialDao().detachAll();
        getDaoSession().getLegDao().detachAll();
        mLeg.resetSpecialList();
        mLeg.refresh();
    }

    public void rememberTeamToEliminate(LegTeam legTeam) {
        mTeamToEliminate = legTeam;
    }

    public void addReasonForElimination(long reasonId) {
        if (mTeamToEliminate != null) {
            TeamElimination te = new TeamElimination();
            te.setLegId(mLeg.getId());
            te.setSeasonId(mLeg.getSeasonId());
            te.setTeamId(mTeamToEliminate.getTeamId());
            te.setReasonId(reasonId);
            getDaoSession().getTeamEliminationDao().insert(te);
            getDaoSession().getTeamEliminationDao().detachAll();
        }
    }

    public void deleteEliminateReason(LegTeam legTeam, EliminationReason bean) {
        getDaoSession().getTeamEliminationDao().queryBuilder()
                .where(TeamEliminationDao.Properties.LegId.eq(legTeam.getLegId()))
                .where(TeamEliminationDao.Properties.TeamId.eq(legTeam.getTeamId()))
                .where(TeamEliminationDao.Properties.ReasonId.eq(bean.getId()))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
        getDaoSession().getTeamEliminationDao().detachAll();
    }
}
