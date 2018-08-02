package com.king.app.vrace.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.king.app.vrace.base.BaseViewModel;
import com.king.app.vrace.conf.LegType;
import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.LegDao;
import com.king.app.vrace.model.entity.LegPlaces;
import com.king.app.vrace.model.entity.LegPlacesDao;
import com.king.app.vrace.utils.PlaceUtil;
import com.king.app.vrace.viewmodel.bean.CountryItem;

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
 * @date: 2018/8/1 17:53
 */
public class CountryViewModel extends BaseViewModel {

    public MutableLiveData<List<CountryItem>> countryObserver = new MutableLiveData<>();

    public CountryViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadData(String country) {
        loadingObserver.setValue(true);
        queryCountry(country)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<CountryItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<CountryItem> countryItems) {
                        loadingObserver.setValue(false);
                        countryObserver.setValue(countryItems);
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

    public Observable<List<CountryItem>> queryCountry(String country) {
        return Observable.create(e -> {
            List<CountryItem> list = new ArrayList<>();

            // find all legs except start and final
            QueryBuilder<Leg> builder = getDaoSession().getLegDao().queryBuilder();
            builder.join(LegPlaces.class, LegPlacesDao.Properties.LegId)
                    .where(LegPlacesDao.Properties.Country.eq(country));
            builder.where(LegDao.Properties.Index.gt(0));
            builder.where(LegDao.Properties.Type.notEq(LegType.FINAL.ordinal()));
            builder.orderAsc(LegDao.Properties.Index);
            List<Leg> legs = builder.build().list();

            // group legs by season
            Map<Integer, CountryItem> seasonLegMap = new HashMap<>();
            for (Leg leg:legs) {
                int seasonIndex = leg.getSeason().getIndex();
                CountryItem item = seasonLegMap.get(seasonIndex);
                if (item == null) {
                    item = new CountryItem();
                    item.setSeason(leg.getSeason());
                    item.setLegList(new ArrayList<>());
                    seasonLegMap.put(seasonIndex, item);
                    list.add(item);
                }
                item.getLegList().add(leg);
            }

            // sort by season
            Collections.sort(list, (left, right) -> left.getSeason().getIndex() - right.getSeason().getIndex());

            Comparator<Leg> legComparator = (o1, o2) -> o1.getIndex() - o2.getIndex();
            // find previous/next leg for each item and create display text
            for (CountryItem item:list) {
                item.setSeasonText("S" + item.getSeason().getIndex());
                setLegText(item, legComparator);

                // previous leg
                Leg first = item.getLegList().get(0);
                item.setPreviousLeg(getLeg(first.getSeasonId(), first.getIndex() - 1));
                if (item.getPreviousLeg() != null) {
                    LegPlaces place = item.getPreviousLeg().getPlaceList().get(0);
                    item.setPreviousText(PlaceUtil.getCombinePlace(place.getCountry(), place.getCity()));
                }
                // next leg
                Leg last = item.getLegList().get(item.getLegList().size() - 1);
                Leg next = getLeg(last.getSeasonId(), last.getIndex() + 1);
                if (next != null) {
                    item.setNextLeg(next);
                    LegPlaces place = item.getNextLeg().getPlaceList().get(0);
                    item.setNextText(PlaceUtil.getCombinePlace(place.getCountry(), place.getCity()));
                }
            }

            e.onNext(list);
        });
    }

    private void setLegText(CountryItem item, Comparator<Leg> legComparator) {
        String legText;
        if (item.getLegList().size() > 1) {
            Collections.sort(item.getLegList(), legComparator);
            String city1 = item.getLegList().get(0).getPlaceList().get(0).getCity();
            String city2 = item.getLegList().get(1).getPlaceList().get(0).getCity();
            if (TextUtils.isEmpty(city1) && TextUtils.isEmpty(city2)) {
                legText = "Leg " + item.getLegList().get(0).getIndex();
                for (int i = 1; i < item.getLegList().size(); i ++) {
                    legText = legText + "," + item.getLegList().get(i).getIndex();
                }
            }
            else if (!TextUtils.isEmpty(city1) && !TextUtils.isEmpty(city2) && city1.equals(city2)) {
                legText = "L" + item.getLegList().get(0).getIndex() + "," + item.getLegList().get(1).getIndex()
                        + " " + city1;
            }
            else {
                legText = getLegText(item.getLegList().get(0).getIndex(), city1);
                for (int i = 1; i < item.getLegList().size(); i ++) {
                    legText = legText + "\n" + getLegText(item.getLegList().get(i).getIndex(), item.getLegList().get(i).getPlaceList().get(0).getCity());
                }
            }
        }
        else {
            String city = item.getLegList().get(0).getPlaceList().get(0).getCity();
            legText = getLegText(item.getLegList().get(0).getIndex(), city);
        }
        item.setLegText(legText);
    }

    private String getLegText(int legIndex, String city) {
        if (TextUtils.isEmpty(city)) {
            return "L" + legIndex;
        }
        else {
            return "L" + legIndex + " " + city;
        }
    }

    private Leg getLeg(long seasonId, int legIndex) {
        return getDaoSession().getLegDao().queryBuilder()
                .where(LegDao.Properties.SeasonId.eq(seasonId))
                .where(LegDao.Properties.Index.eq(legIndex))
                .build().unique();
    }

}
