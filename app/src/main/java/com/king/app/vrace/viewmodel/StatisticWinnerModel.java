package com.king.app.vrace.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.king.app.vrace.base.BaseViewModel;
import com.king.app.vrace.conf.AppConstants;
import com.king.app.vrace.conf.GenderType;
import com.king.app.vrace.conf.LegType;
import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.LegDao;
import com.king.app.vrace.model.entity.LegTeam;
import com.king.app.vrace.model.entity.LegTeamDao;
import com.king.app.vrace.model.entity.PersonTag;
import com.king.app.vrace.model.entity.Relationship;
import com.king.app.vrace.model.entity.TeamSeason;
import com.king.app.vrace.viewmodel.bean.StatisticWinnerItem;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @desc
 * @auth 景阳
 * @time 2018/7/2 0002 20:17
 */

public class StatisticWinnerModel extends BaseViewModel {

    public MutableLiveData<List<Object>> dataObserver = new MutableLiveData<>();

    public StatisticWinnerModel(@NonNull Application application) {
        super(application);
    }

    public void loadData() {
        queryWinners()
                .flatMap(list -> combineData(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Object>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Object> list) {
                        dataObserver.setValue(list);
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

    private Observable<List<Object>> combineData(List<LegTeam> list){
        return Observable.combineLatest(toRelationItems(list), toGenderItems(list), toEpItems(list), toHeadItems(list), toTagItems(list)
                , (relations, genders, eps, heads, tags) -> {

                    List<Object> results = new ArrayList<>();
                    results.add("Team type");
                    results.addAll(genders);
                    results.add("Relationships");
                    results.addAll(relations);
                    results.add("Tags");
                    results.addAll(tags);
                    results.add("分赛冠");
                    results.addAll(eps);
                    results.add("片头");
                    results.addAll(heads);
                    return results;
                });
    }

    private Observable<List<LegTeam>> queryWinners() {
        return Observable.create(e -> {
            QueryBuilder<LegTeam> builder = getDaoSession().getLegTeamDao().queryBuilder();
            builder.join(LegTeamDao.Properties.LegId, Leg.class)
                    .where(LegDao.Properties.Type.eq(LegType.FINAL.ordinal()));
            builder.where(LegTeamDao.Properties.Position.eq(1));
            List<LegTeam> list = builder.build().list();
            e.onNext(list);
        });
    }

    private Observable<List<StatisticWinnerItem>> toRelationItems(List<LegTeam> list) {
        return Observable.create(e -> {
            List<StatisticWinnerItem> results = new ArrayList<>();
            HashMap<Relationship, StatisticWinnerItem> relationMap = new HashMap<>();
            for (LegTeam legTeam:list) {
                Relationship relationship = legTeam.getTeam().getRelationship();
                if (relationMap.get(relationship) == null) {
                    relationMap.put(relationship, new StatisticWinnerItem());
                    relationMap.get(relationship).setType(relationship.getName());
                    results.add(relationMap.get(relationship));
                }
                relationMap.get(relationship).setCount(relationMap.get(relationship).getCount() + 1);
                if (TextUtils.isEmpty(relationMap.get(relationship).getSeasons())) {
                    relationMap.get(relationship).setSeasons("S" + legTeam.getSeason().getIndex());
                }
                else {
                    relationMap.get(relationship).setSeasons(relationMap.get(relationship).getSeasons() + ", S" + legTeam.getSeason().getIndex());
                }
            }
            Collections.sort(results, new WinnerComparator());
            e.onNext(results);
        });
    }

    private Observable<List<StatisticWinnerItem>> toGenderItems(List<LegTeam> list) {
        return Observable.create(e -> {
            List<StatisticWinnerItem> results = new ArrayList<>();
            HashMap<Integer, StatisticWinnerItem> genderMap = new HashMap<>();
            for (LegTeam legTeam:list) {
                int genderType = legTeam.getTeam().getGenderType();
                if (genderMap.get(genderType) == null) {
                    genderMap.put(genderType, new StatisticWinnerItem());
                    genderMap.get(genderType).setType(AppConstants.getGenderText(GenderType.values()[genderType]));
                    results.add(genderMap.get(genderType));
                }
                genderMap.get(genderType).setCount(genderMap.get(genderType).getCount() + 1);
                if (TextUtils.isEmpty(genderMap.get(genderType).getSeasons())) {
                    genderMap.get(genderType).setSeasons("S" + legTeam.getSeason().getIndex());
                }
                else {
                    genderMap.get(genderType).setSeasons(genderMap.get(genderType).getSeasons() + ", S" + legTeam.getSeason().getIndex());
                }
            }
            Collections.sort(results, new WinnerComparator());
            e.onNext(results);
        });
    }

    private Observable<List<StatisticWinnerItem>> toHeadItems(List<LegTeam> list) {
        return Observable.create(e -> {
            List<StatisticWinnerItem> results = new ArrayList<>();
            HashMap<Integer, StatisticWinnerItem> positionMap = new HashMap<>();
            for (LegTeam legTeam:list) {
                int position = 0;
                for (TeamSeason ts:legTeam.getTeam().getSeasonList()) {
                    if (ts.getSeasonId() == legTeam.getSeasonId()) {
                        position = ts.getEpisodeSeq();
                    }
                }
                if (positionMap.get(position) == null) {
                    positionMap.put(position, new StatisticWinnerItem());
                    positionMap.get(position).setType("片头" + position);
                    positionMap.get(position).setSortValue(position);
                    results.add(positionMap.get(position));
                }
                positionMap.get(position).setCount(positionMap.get(position).getCount() + 1);
                if (TextUtils.isEmpty(positionMap.get(position).getSeasons())) {
                    positionMap.get(position).setSeasons("S" + legTeam.getSeason().getIndex());
                }
                else {
                    positionMap.get(position).setSeasons(positionMap.get(position).getSeasons() + ", S" + legTeam.getSeason().getIndex());
                }
            }
            Collections.sort(results, new WinnerComparator());
            e.onNext(results);
        });
    }

    private Observable<List<StatisticWinnerItem>> toEpItems(List<LegTeam> list) {
        return Observable.create(e -> {
            List<StatisticWinnerItem> results = new ArrayList<>();
            HashMap<Integer, StatisticWinnerItem> positionMap = new HashMap<>();
            for (LegTeam legTeam:list) {
                // 查询夺冠的赛段
                List<LegTeam> legs = getDaoSession().getLegTeamDao().queryBuilder()
                        .where(LegTeamDao.Properties.TeamId.eq(legTeam.getTeamId()))
                        .where(LegTeamDao.Properties.Position.eq(1))
                        .where(LegTeamDao.Properties.SeasonId.eq(legTeam.getSeasonId()))
                        .build().list();
                for (LegTeam lt:legs) {
                    // 过滤final赛段
                    if (lt.getLeg().getType() == LegType.FINAL.ordinal()) {
                        continue;
                    }
                    int position = lt.getLeg().getIndex();
                    if (positionMap.get(position) == null) {
                        positionMap.put(position, new StatisticWinnerItem());
                        positionMap.get(position).setType("EP" + position);
                        positionMap.get(position).setSortValue(position);
                        results.add(positionMap.get(position));
                    }
                    positionMap.get(position).setCount(positionMap.get(position).getCount() + 1);
                    if (TextUtils.isEmpty(positionMap.get(position).getSeasons())) {
                        positionMap.get(position).setSeasons("S" + legTeam.getSeason().getIndex());
                    }
                    else {
                        positionMap.get(position).setSeasons(positionMap.get(position).getSeasons() + ", S" + legTeam.getSeason().getIndex());
                    }
                }
            }
            Collections.sort(results, new WinnerComparator());
            e.onNext(results);
        });
    }

    private Observable<List<StatisticWinnerItem>> toTagItems(List<LegTeam> list) {
        return Observable.create(e -> {
            List<StatisticWinnerItem> results = new ArrayList<>();
            HashMap<String, StatisticWinnerItem> tagMap = new HashMap<>();
            for (LegTeam legTeam:list) {
                // 查询夺冠的赛段
                List<PersonTag> tags = legTeam.getTeam().getTagList();
                for (PersonTag pt:tags) {
                    // 过滤final赛段
                    String tag = pt.getTag();
                    if (tagMap.get(tag) == null) {
                        tagMap.put(tag, new StatisticWinnerItem());
                        tagMap.get(tag).setType(tag);
                        results.add(tagMap.get(tag));
                    }
                    tagMap.get(tag).setCount(tagMap.get(tag).getCount() + 1);
                    if (TextUtils.isEmpty(tagMap.get(tag).getSeasons())) {
                        tagMap.get(tag).setSeasons("S" + legTeam.getSeason().getIndex());
                    }
                    else {
                        tagMap.get(tag).setSeasons(tagMap.get(tag).getSeasons() + ", S" + legTeam.getSeason().getIndex());
                    }
                }
            }
            Collections.sort(results, new WinnerComparator());
            e.onNext(results);
        });
    }

    public class WinnerComparator implements Comparator<StatisticWinnerItem> {

        @Override
        public int compare(StatisticWinnerItem left, StatisticWinnerItem right) {
            return left.getSortValue() - right.getSortValue();
        }
    }
}
