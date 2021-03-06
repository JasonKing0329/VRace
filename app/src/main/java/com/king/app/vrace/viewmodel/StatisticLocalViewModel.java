package com.king.app.vrace.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.king.app.vrace.base.BaseViewModel;
import com.king.app.vrace.conf.AppConstants;
import com.king.app.vrace.conf.LegType;
import com.king.app.vrace.model.ImageProvider;
import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.LegDao;
import com.king.app.vrace.model.entity.LegPlaces;
import com.king.app.vrace.model.entity.LegPlacesDao;
import com.king.app.vrace.viewmodel.bean.PlaceSeason;
import com.king.app.vrace.viewmodel.bean.StatisticPlaceItem;

import org.greenrobot.greendao.query.QueryBuilder;

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
public class StatisticLocalViewModel extends BaseViewModel {

    public MutableLiveData<List<StatisticPlaceItem>> placeObserver = new MutableLiveData<>();

    public MutableLiveData<List<PlaceSeason>> placeSeasonsObserver = new MutableLiveData<>();

    private int mStatisticType;

    public StatisticLocalViewModel(@NonNull Application application) {
        super(application);

        mStatisticType = -1;
        statistic(AppConstants.STAT_LOCAL_DEPART);
    }

    public void statistic(int type) {
        if (mStatisticType != type) {
            mStatisticType = type;

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

    public List<StatisticPlaceItem> getPlaceItems() {
        List<StatisticPlaceItem> list = new ArrayList<>();
        QueryBuilder<Leg> builder = getDaoSession().getLegDao().queryBuilder();
        if (mStatisticType == AppConstants.STAT_LOCAL_FINAL) {
            builder.where(LegDao.Properties.Type.eq(LegType.FINAL.ordinal()));
        }
        else {
            builder.where(LegDao.Properties.Index.eq(0));
        }
        List<Leg> legs = builder.build().list();

        Map<String, StatisticPlaceItem> placeMap = new HashMap<>();

        for (Leg leg:legs) {
            LegPlaces lp = null;
            if (leg.getPlaceList().size() > 0) {
                lp = leg.getPlaceList().get(0);
            }
            if (lp != null) {
                String key = lp.getCity();
                StatisticPlaceItem item = placeMap.get(key);
                if (item == null) {
                    item = new StatisticPlaceItem();
                    item.setPlace(lp.getCity());
                    item.setContinent(lp.getContinent());
                    item.setImgPath(ImageProvider.getLegImagePath(leg));
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

    public void findCitySeasons(String city) {
        getCitySeasons(city)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<PlaceSeason>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<PlaceSeason> seasons) {
                        placeSeasonsObserver.setValue(seasons);
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

    private Observable<List<PlaceSeason>> getCitySeasons(String city) {
        return Observable.create(e -> {
            List<PlaceSeason> list = new ArrayList<>();
            List<LegPlaces> places = getDaoSession().getLegPlacesDao().queryBuilder()
                    .where(LegPlacesDao.Properties.City.eq(city))
                    .build().list();

            Map<Long, PlaceSeason> seasonMap = new HashMap<>();
            for (LegPlaces legPlaces:places) {
                long seasonId = legPlaces.getLeg().getSeasonId();
                PlaceSeason ps = seasonMap.get(seasonId);
                if (ps == null) {
                    ps = new PlaceSeason();
                    ps.setPlace(city);
                    ps.setSeason(legPlaces.getLeg().getSeason());
                    ps.setLegs(new ArrayList<>());
                    list.add(ps);
                    seasonMap.put(seasonId, ps);
                }
                ps.getLegs().add(legPlaces.getLeg());
            }

            Collections.sort(list, new PlaceSeasonComparator());
            e.onNext(list);
        });
    }

    public String[] convertToTextList(List<PlaceSeason> list) {
        String[] textList = new String[list.size()];
        for (int n = 0; n < list.size(); n ++) {
            PlaceSeason ps = list.get(n);
            String item = "S" + ps.getSeason().getIndex() + "   Leg " + ps.getLegs().get(0).getIndex();
            if (ps.getLegs().size() > 1) {
                for (int i = 1; i < ps.getLegs().size(); i ++) {
                    item = item + ", Leg " + ps.getLegs().get(i).getIndex();
                }
            }
            textList[n] = item;
        }
        return textList;
    }

    private class CountComparator implements Comparator<StatisticPlaceItem> {

        @Override
        public int compare(StatisticPlaceItem left, StatisticPlaceItem right) {
            return right.getCount() - left.getCount();
        }
    }

    private class PlaceSeasonComparator implements Comparator<PlaceSeason> {

        @Override
        public int compare(PlaceSeason left, PlaceSeason right) {
            return left.getSeason().getIndex() - right.getSeason().getIndex();
        }
    }
}
