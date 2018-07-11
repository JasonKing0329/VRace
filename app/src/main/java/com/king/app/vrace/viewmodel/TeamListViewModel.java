package com.king.app.vrace.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseViewModel;
import com.king.app.vrace.conf.AppConstants;
import com.king.app.vrace.conf.GenderType;
import com.king.app.vrace.model.TeamModel;
import com.king.app.vrace.model.bean.TeamResult;
import com.king.app.vrace.model.entity.Team;
import com.king.app.vrace.model.entity.TeamDao;
import com.king.app.vrace.model.entity.TeamPlayersDao;
import com.king.app.vrace.model.entity.TeamSeasonDao;
import com.king.app.vrace.model.entity.TeamTagDao;
import com.king.app.vrace.utils.PlaceUtil;
import com.king.app.vrace.viewmodel.bean.StatProvinceItem;
import com.king.app.vrace.viewmodel.bean.StatTeamItem;
import com.king.app.vrace.viewmodel.bean.TeamListItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
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

    public MutableLiveData<List<StatProvinceItem>> provinceTeamsObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> deleteObserver = new MutableLiveData<>();

    private Map<Long, Boolean> mCheckMap;

    private TeamModel teamModel;

    private int mSortType;

    public TeamListViewModel(@NonNull Application application) {
        super(application);
        mCheckMap = new HashMap<>();
        mSortType = AppConstants.TEAM_SORT_NONE;
        teamModel = new TeamModel();
    }

    public Map<Long, Boolean> getCheckMap() {
        return mCheckMap;
    }

    public void onSortTypeChanged(int type) {
        if (mSortType != type) {
            mSortType = type;
            loadTeams();
        }
    }

    public void loadTeams() {
        loadingObserver.setValue(true);
        if (mSortType == AppConstants.TEAM_SORT_PROVINCE) {
            loadProvinceGroup();
            return;
        }
        queryTeams()
                .flatMap(list -> toViewItems(list))
                .flatMap(list -> sortItems(list))
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

    private void loadProvinceGroup() {
        queryTeams()
                .flatMap(list -> toViewItems(list))
                .flatMap(list -> toProvinceGroup(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<StatProvinceItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<StatProvinceItem> list) {
                        loadingObserver.setValue(false);
                        provinceTeamsObserver.setValue(list);
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

    private Observable<List<TeamListItem>> sortItems(List<TeamListItem> teams) {
        return Observable.create(e -> {
            // 为方便添加，按添加顺序后添加的在前
            if (mSortType == AppConstants.TEAM_SORT_NONE) {
                Collections.reverse(teams);
            }
            else {
                Collections.sort(teams, new TeamComparator());
            }
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
                    String seasons = "S" + team.getSeasonList().get(0).getSeason().getIndex();
                    String uniqueSeason = seasons;
                    // 参与多季度的team单独显示，并显示过往季度
                    if (team.getSeasonList().size() > 1) {
                        for (int i = 1; i < team.getSeasonList().size(); i ++) {
                            seasons = "S" + team.getSeasonList().get(i).getSeason().getIndex() + "," + seasons;
                            TeamListItem multiItem = new TeamListItem();
                            multiItem.setBean(team);
                            multiItem.setName(seasons + "\n" + team.getCode());
                            multiItem.setSeason(team.getSeasonList().get(i).getSeason());
                            parseTeam(team, multiItem);
                            list.add(multiItem);
                        }
                    }
                    item.setSeason(team.getSeasonList().get(0).getSeason());
                    item.setName(uniqueSeason + "\n" + team.getCode());
                }
                else {
                    item.setName(team.getCode());
                }
                parseTeam(team, item);
                list.add(item);
            }
            e.onNext(list);
        });
    }

    private void parseTeam(Team team, TeamListItem item) {
        item.setGender(AppConstants.getGenderText(GenderType.values()[team.getGenderType()]));
        item.setRelationship(team.getRelationship().getName());
        item.setPlace(PlaceUtil.getCombinePlace(team.getProvince(), team.getCity()));
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

        // 计算point及赛冠
        if (item.getSeason() != null) {
            TeamResult result = teamModel.getTeamSeasonResults(team.getId(), item.getSeason().getId());
            item.setResult(result);
        }
    }

    private Observable<List<StatProvinceItem>> toProvinceGroup(List<TeamListItem> list) {
        return Observable.create(e -> {
            List<StatProvinceItem> groups = new ArrayList<>();
            Map<String, StatProvinceItem> provinceMap = new HashMap<>();
            for (TeamListItem team:list) {
                String province = team.getBean().getProvince();
                StatProvinceItem item = provinceMap.get(province);
                if (item == null) {
                    item = new StatProvinceItem();
                    item.setProvince(province);
                    item.setChildItemList(new ArrayList<>());
                    provinceMap.put(province, item);
                    groups.add(item);
                }
                StatTeamItem sti = new StatTeamItem();
                sti.setBean(team);
                item.getChildItemList().add(sti);
            }

            // sort children
            StatTeamComparator teamComparator = new StatTeamComparator();
            for (StatProvinceItem item:groups) {
                item.setCount(item.getChildItemList().size());
                Collections.sort(item.getChildItemList(), teamComparator);
            }
            // sort province
            Collections.sort(groups, new StatProvinceComparator());

            e.onNext(groups);
        });
    }

    private class TeamComparator implements Comparator<TeamListItem> {

        @Override
        public int compare(TeamListItem left, TeamListItem right) {
            int valueL = 0;
            int valueR = 0;
            switch (mSortType) {
                case AppConstants.TEAM_SORT_SEASON:// 降序，第二关键词endPosition升序
                    // 没有season的表示是新添加的team，排在最前
                    valueL = left.getSeason() == null ? -1:left.getSeason().getIndex();
                    valueR = right.getSeason() == null ? -1:right.getSeason().getIndex();
                    if (valueL == valueR && valueL != -1) {
                        valueL = left.getResult().getEndPosition();
                        valueR = right.getResult().getEndPosition();
                        return valueL - valueR;
                    }
                    else {
                        return valueR - valueL;
                    }
                case AppConstants.TEAM_SORT_POINT:// 升序，第二关键词endPosition升序，第三关键词赛冠降序
                    // 没有result的表示是新添加的team，排在最后
                    double vl;
                    if (left.getResult() == null || left.getResult().getPoint() == 0) {
                        vl = 9999;
                    }
                    else {
                        vl = left.getResult().getPoint();
                    }
                    double vr;
                    if (right.getResult() == null || right.getResult().getPoint() == 0) {
                        vr = 9999;
                    }
                    else {
                        vr = right.getResult().getPoint();
                    }
                    if (vl == vr && vl != -1) {
                        vl = left.getResult().getEndPosition();
                        vr = right.getResult().getEndPosition();
                        if (vl == vr) {
                            vl = left.getResult().getChampions();
                            vr = right.getResult().getChampions();
                            return compareDouble(vr, vl);
                        }
                        else {
                            return compareDouble(vl, vr);
                        }
                    }
                    else {
                        return compareDouble(vl, vr);
                    }
                case AppConstants.TEAM_SORT_CHAMPIONS:// 降序，第二关键词endPosition升序，第三关键词point升序
                    // 没有result的表示是新添加的team，排在最后
                    vl = left.getResult() == null ? -1:left.getResult().getChampions();
                    vr = right.getResult() == null ? -1:right.getResult().getChampions();
                    if (vl == vr && vl != -1) {
                        vl = left.getResult().getEndPosition();
                        vr = right.getResult().getEndPosition();
                        if (vl == vr) {
                            vl = left.getResult().getPoint();
                            vr = right.getResult().getPoint();
                            return compareDouble(vl, vr);
                        }
                        else {
                            return compareDouble(vl, vr);
                        }
                    }
                    else {
                        return compareDouble(vr, vl);
                    }
            }
            return 0;
        }
    }

    private class StatTeamComparator implements Comparator<StatTeamItem> {

        @Override
        public int compare(StatTeamItem left, StatTeamItem right) {
            // 以endPosition升序，第二关键词point升序
            double vl = left.getBean().getResult().getEndPosition();
            double vr = right.getBean().getResult().getEndPosition();
            if (vl == vr) {
                vl = left.getBean().getResult().getPoint();
                vr = right.getBean().getResult().getPoint();
                return compareDouble(vl, vr);
            }
            else {
                return compareDouble(vl, vr);
            }
        }
    }

    private class StatProvinceComparator implements Comparator<StatProvinceItem> {

        @Override
        public int compare(StatProvinceItem left, StatProvinceItem right) {
            return right.getCount() - left.getCount();
        }
    }

    private int compareDouble(double left, double right) {
        if (left < right) {
            return -1;
        }
        else if (left > right) {
            return 1;
        }
        else {
            return 0;
        }
    }

    private int compareInt(int left, int right) {
        return left - right;
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
            getDaoSession().getTeamSeasonDao().queryBuilder()
                    .where(TeamSeasonDao.Properties.TeamId.eq(teamId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getTeamSeasonDao().detachAll();
            // delete from team_tag
            getDaoSession().getTeamTagDao().queryBuilder()
                    .where(TeamTagDao.Properties.TeamId.eq(teamId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getTeamTagDao().detachAll();
        }
    }
}
