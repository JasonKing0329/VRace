package com.king.app.vrace.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseViewModel;
import com.king.app.vrace.base.RaceApplication;
import com.king.app.vrace.conf.AppConstants;
import com.king.app.vrace.conf.LegType;
import com.king.app.vrace.model.MapModel;
import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.LegDao;
import com.king.app.vrace.model.entity.LegPlaces;
import com.king.app.vrace.model.entity.MapCountry;
import com.king.app.vrace.model.entity.MapCountryDao;
import com.king.app.vrace.model.entity.MapPathDao;
import com.king.app.vrace.model.entity.Season;
import com.king.app.vrace.model.entity.SeasonDao;
import com.king.app.vrace.view.widget.map.MapItem;
import com.king.app.vrace.viewmodel.bean.MapData;

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
 * @date: 2018/7/24 9:24
 */
public class SeasonMapViewModel extends BaseViewModel {

    public MutableLiveData<Season> seasonObserver = new MutableLiveData<>();

    public MutableLiveData<MapData> mapObserver = new MutableLiveData<>();

    private Season mSeason;

    private MapModel mapModel;

    private MapData mapData;

    public SeasonMapViewModel(@NonNull Application application) {
        super(application);
        mapModel = new MapModel();
        mapData = new MapData();
    }

    public void loadMap(long seasonId) {
        loadingObserver.setValue(true);
        mapModel.loadWorldMap()
                .flatMap(map -> {
                    mapData.setMap(map);
                    return mapModel.loadMapItems(map);
                })
                .flatMap(list -> toSeasonMap(list, seasonId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<MapData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(MapData data) {
                        loadingObserver.setValue(false);
                        seasonObserver.setValue(mSeason);
                        mapObserver.setValue(data);
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

    private Observable<MapData> toSeasonMap(List<MapItem> list, long seasonId) {
        return Observable.create(e -> {
            mapData.setItemList(list);

            mSeason = getDaoSession().getSeasonDao().load(seasonId);

            List<Leg> legs = getDaoSession().getLegDao().queryBuilder()
                    .where(LegDao.Properties.SeasonId.eq(seasonId))
                    .orderAsc(LegDao.Properties.Index)
                    .build().list();
            if (legs.size() > 0) {
                Map<String, Boolean> placeMap = new HashMap<>();
                MapCountryDao mapCountryDao = getDaoSession().getMapCountryDao();

                for (Leg leg:legs) {
                    for (LegPlaces lp:leg.getPlaceList()) {
                        String countryChn = lp.getCountry();
                        String countryEng;
                        MapCountry mc = mapCountryDao.queryBuilder()
                                .where(MapCountryDao.Properties.NameChn.eq(countryChn))
                                .build().unique();
                        if (mc == null) {
                            continue;
                        }
                        else {
                            countryEng = mc.getNameInMap();
                            placeMap.put(countryEng, true);
                        }
                    }
                }

                StringBuffer legDesc = new StringBuffer();
                for (int i = 0; i < legs.size(); i ++) {
                    Leg leg = legs.get(i);
                    if (leg.getIndex() == 0) {
                        legDesc.append(leg.getPlaceList().get(0).getCountry()).append("/").append(leg.getPlaceList().get(0).getCity());
                    }
                    else if (leg.getType() == LegType.FINAL.ordinal()) {
                        legDesc.append(" --> ").append(leg.getPlaceList().get(0).getCountry()).append("/").append(leg.getPlaceList().get(0).getCity());
                    }
                    else {
                        String country = leg.getPlaceList().get(0).getCountry();
                        String strLeg = "Leg " + leg.getIndex();
                        for (int j = i + 1; j < legs.size(); j ++) {
                            String country1 = legs.get(j).getPlaceList().get(0).getCountry();
                            if (country.equals(country1)) {
                                i ++;
                                strLeg = strLeg + "," + legs.get(j).getIndex();
                            }
                            else {
                                break;
                            }
                        }
                        legDesc.append(" --> ").append(strLeg).append(" ").append(leg.getPlaceList().get(0).getCountry());
                    }
                }
                mapData.setLegsDesc(legDesc.toString());

                for (MapItem item:list) {
                    String countryEng = item.getBean().getPlace();
                    if (placeMap.get(countryEng) != null) {
                        item.setSelected(true);
                        placeMap.remove(countryEng);
                    }
                }
            }

            e.onNext(mapData);
        });
    }

    public void preSeason() {
        Season season = getDaoSession().getSeasonDao().queryBuilder()
                .where(SeasonDao.Properties.Index.eq(mSeason.getIndex() - 1))
                .build().unique();
        if (season == null) {
            messageObserver.setValue("No previous season found");
        }
        else {
            loadMap(season.getId());
        }
    }

    public void nextSeason() {
        Season season = getDaoSession().getSeasonDao().queryBuilder()
                .where(SeasonDao.Properties.Index.eq(mSeason.getIndex() + 1))
                .build().unique();
        if (season == null) {
            messageObserver.setValue("No next season found");
        }
        else {
            loadMap(season.getId());
        }
    }

    public void recreateMap() {
        loadingObserver.setValue(true);
        deleteMapData()
                .flatMap(obj -> mapModel.createMap(RaceApplication.getInstance().getResources(), R.raw.world_map, AppConstants.MAP_ID_WORLD, AppConstants.MAP_ID_WORLD_NAME))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Object data) {
                        loadingObserver.setValue(false);
                        loadMap(mSeason.getId());
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

    public void recreateCountry() {
        loadingObserver.setValue(true);
        deleteMapCountryData()
                .flatMap(obj -> mapModel.createMapCountry(mapData.getMap()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Object data) {
                        loadingObserver.setValue(false);
                        loadMap(mSeason.getId());
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

    private Observable<Object> deleteMapData() {
        return Observable.create(e -> {
            getDaoSession().getMapBeanDao().deleteByKey(AppConstants.MAP_ID_WORLD);
            getDaoSession().getMapPathDao().queryBuilder()
                    .where(MapPathDao.Properties.MapId.eq(AppConstants.MAP_ID_WORLD))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();

            getDaoSession().getMapBeanDao().detachAll();
            getDaoSession().getMapPathDao().detachAll();
            e.onNext(new Object());
        });
    }

    private Observable<Object> deleteMapCountryData() {
        return Observable.create(e -> {
            getDaoSession().getMapCountryDao().queryBuilder()
                    .where(MapCountryDao.Properties.MapId.eq(AppConstants.MAP_ID_WORLD))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getMapCountryDao().detachAll();
            e.onNext(new Object());
        });
    }
}
