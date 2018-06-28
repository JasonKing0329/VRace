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
import com.king.app.vrace.viewmodel.bean.StatisticPlaceItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @date: 2018/6/28 17:11
 */
public class StatisticPlaceViewModel extends BaseViewModel {

    public MutableLiveData<List<StatisticPlaceItem>> placeObserver = new MutableLiveData<>();

    private int mStatisticType;

    public StatisticPlaceViewModel(@NonNull Application application) {
        super(application);

        mStatisticType = SettingProperty.getStatisticPlaceType();

        statistic();
    }

    public void statistic() {
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
            List<StatisticPlaceItem> list = new ArrayList<>();
            List<Leg> legs = getDaoSession().getLegDao().loadAll();

            Map<String, StatisticPlaceItem> placeMap = new HashMap<>();

            for (Leg leg:legs) {
                LegPlaces lp = null;
                if (leg.getPlaceList().size() > 0) {
                    lp = leg.getPlaceList().get(0);
                }
                if (lp != null) {
                    // 同季的国家算一次
                    String key = leg.getSeasonId() + "_" + lp.getCountry();
                    StatisticPlaceItem item = placeMap.get(key);
                    if (item == null) {
                        item = new StatisticPlaceItem();
                        item.setCountry(lp.getCountry());
                        item.setFlagPath(ImageProvider.getCountryFlagPath(lp.getCountry()));
                        list.add(item);
                        placeMap.put(key, item);
                    }
                    item.setCount(item.getCount() + 1);
                    if (TextUtils.isEmpty(item.getSeasons())) {
                        item.setSeasons("S" + leg.getSeason().getIndex());
                    }
                    else {
                        item.setSeasons(item.getSeasons() + ", S" + leg.getSeason().getIndex());
                    }
                }
            }
            e.onNext(list);
        });
    }

    private void queryByGroup() {
    }

}
