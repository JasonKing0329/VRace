package com.king.app.vrace.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.king.app.vrace.base.BaseViewModel;
import com.king.app.vrace.conf.AppConstants;
import com.king.app.vrace.model.ImageProvider;
import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.LegPlaces;
import com.king.app.vrace.model.setting.SettingProperty;
import com.king.app.vrace.viewmodel.bean.StatContinentItem;
import com.king.app.vrace.viewmodel.bean.StatCountryItem;
import com.king.app.vrace.viewmodel.bean.StatisticPlaceItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
 * @date: 2018/6/28 17:11
 */
public class StatisticPlaceViewModel extends BaseViewModel {

    public MutableLiveData<List<StatisticPlaceItem>> placeObserver = new MutableLiveData<>();

    public MutableLiveData<List<StatContinentItem>> groupsObserver = new MutableLiveData<>();

    private int mStatisticType;

    public StatisticPlaceViewModel(@NonNull Application application) {
        super(application);

        statistic(SettingProperty.getStatisticPlaceType());
    }

    public void statistic(int type) {
        mStatisticType = type;

        if (mStatisticType == AppConstants.STAT_PLACE_GROUP_BY_CONT) {
            queryByGroup();
        }
        else {
            queryTotal();
        }
    }

    private void queryTotal() {
        getTotal()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<StatisticPlaceItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<StatisticPlaceItem> list) {
                        placeObserver.setValue(list);
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

    private Observable<List<StatisticPlaceItem>> getTotal() {
        return Observable.create(e -> {
            List<StatisticPlaceItem> list = getPlaceItems();
            e.onNext(list);
        });
    }

    private void queryByGroup() {
        getGroups()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<StatContinentItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<StatContinentItem> list) {
                        groupsObserver.setValue(list);
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

    private Observable<List<StatContinentItem>> getGroups() {
        return Observable.create(e -> {
            List<StatisticPlaceItem> list = getPlaceItems();
            List<StatContinentItem> groups = new ArrayList<>();

            Map<String, List<StatCountryItem>> groupMap = new HashMap<>();
            for (StatisticPlaceItem placeItem:list) {
                String continent = placeItem.getContinent();
                if (groupMap.get(continent) == null) {
                    groupMap.put(continent, new ArrayList<>());
                }
                StatCountryItem countryItem = new StatCountryItem();
                countryItem.setBean(placeItem);
                groupMap.get(continent).add(countryItem);
            }
            for (String continent:groupMap.keySet()) {
                StatContinentItem group = new StatContinentItem();
                group.setContinent(continent);
                group.setChildItemList(groupMap.get(continent));
                group.setCount(group.getChildItemList().size());
                groups.add(group);
            }
            e.onNext(groups);
        });
    }

    public List<StatisticPlaceItem> getPlaceItems() {
        List<StatisticPlaceItem> list = new ArrayList<>();
        List<Leg> legs = getDaoSession().getLegDao().loadAll();

        Map<String, StatisticPlaceItem> placeMap = new HashMap<>();

        for (Leg leg:legs) {
            LegPlaces lp = null;
            if (leg.getPlaceList().size() > 0) {
                lp = leg.getPlaceList().get(0);
            }
            if (lp != null) {
                String key = lp.getCountry();
                if ("中国".equals(key)) {
                    continue;
                }
                StatisticPlaceItem item = placeMap.get(key);
                if (item == null) {
                    item = new StatisticPlaceItem();
                    item.setCountry(lp.getCountry());
                    item.setContinent(lp.getContinent());
                    item.setFlagPath(ImageProvider.getCountryFlagPath(lp.getCountry()));
                    list.add(item);
                    placeMap.put(key, item);
                }
                else {
                    // 同季的国家算一次
                    if (leg.getSeasonId() == item.getLastSeasonId()) {
                        continue;
                    }
                }
                item.setLastSeasonId(leg.getSeasonId());
                item.setCount(item.getCount() + 1);
                if (TextUtils.isEmpty(item.getSeasons())) {
                    item.setSeasons("S" + leg.getSeason().getIndex());
                }
                else {
                    item.setSeasons(item.getSeasons() + ", S" + leg.getSeason().getIndex());
                }
            }
        }
        Collections.sort(list, new CountComparator());
        return list;
    }

    private class CountComparator implements Comparator<StatisticPlaceItem> {

        @Override
        public int compare(StatisticPlaceItem left, StatisticPlaceItem right) {
            return right.getCount() - left.getCount();
        }
    }
}
