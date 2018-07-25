package com.king.app.vrace.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
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
import com.king.app.vrace.utils.DebugLog;
import com.king.app.vrace.view.widget.map.LinePoint;
import com.king.app.vrace.view.widget.map.MapItem;
import com.king.app.vrace.viewmodel.bean.LegMapItem;
import com.king.app.vrace.viewmodel.bean.MapData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:draw world map, mark countries accessed, draw flight trace between countries
 * 1. parse svg file assets/World_Map.svg(use country's English name as 'android:name' attribute) to raw/world_map.xml
 * svg file is download from "https://commons.wikimedia.org/wiki/File:World_Map.svg"
 * convert website is "http://inloop.github.io/svg2android/"
 * 2. create database table map, map_path to save map and map path which includes all countries' name
 * 3. parse html from "https://baike.baidu.com/" with search words "中英文国家对照表", to create the relationship of english name and chinese name of country
 * create database table map_country
 * 4. query all legs in specific season to be marked as accessed in map
 * 5. create the flight trace leg by leg
 * 6. the country and map info are not perfect, so it need a little change by manual
 *    even so there are still some really small countries not in map eg. Singapore, Fiji, Vatican, Palau...
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
                .flatMap(list -> toSeasonMapData(list, seasonId))
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

    private Observable<MapData> toSeasonMapData(List<MapItem> list, long seasonId) {
        return Observable.create(e -> {
            mapData.setItemList(list);

            mSeason = getDaoSession().getSeasonDao().load(seasonId);

            List<Leg> legs = getDaoSession().getLegDao().queryBuilder()
                    .where(LegDao.Properties.SeasonId.eq(seasonId))
                    .orderAsc(LegDao.Properties.Index)
                    .build().list();
            if (legs.size() > 0) {
                String legDesc = getLegDesc(legs);
                mapData.setLegsDesc(legDesc);

                List<LegMapItem> mapLegs = convertLegToMapLegs(legs, list);
                LinePoint linePoint = getFlightLines(mapLegs);
                mapData.setLinePoint(linePoint);
            }
            e.onNext(mapData);
        });
    }

    private LinePoint getFlightLines(List<LegMapItem> mapLegs) {
        StringBuffer errorInfo = new StringBuffer();
        LinePoint linePoint = new LinePoint();
        Point lastPoint = null;
        LegMapItem lastItem = null;
        for (int i = 0; i < mapLegs.size(); i++) {
            LegMapItem item = mapLegs.get(i);
            if (item.getMapItem() == null) {
                errorInfo.append("【").append(item.getCountryChn()).append("(").append(item.getCountryInMap()).append(")").append("】");
                lastPoint = null;
                lastItem = null;
            } else {
                RectF rectF = new RectF();
                item.getMapItem().getPath().computeBounds(rectF, false);
                Point point = new Point((int) rectF.centerX(), (int) rectF.centerY());
                linePoint.addPoint(point);

                if (lastPoint != null) {
                    Path path = mapModel.createFlightPath(mapData, lastPoint, point, lastItem, item);
                    linePoint.addLinePath(path);
                }
                lastPoint = point;
                lastItem = item;
            }
        }
        if (errorInfo.toString().length() > 0) {
            errorInfo.append(" not in map");
        }
        mapData.setErrorInfo(errorInfo.toString());
        return linePoint;
    }

    private String getLegDesc(List<Leg> legs) {
        StringBuffer legDesc = new StringBuffer();
        for (int i = 0; i < legs.size(); i++) {
            Leg leg = legs.get(i);
            if (leg.getIndex() == 0) {
                legDesc.append(leg.getPlaceList().get(0).getCountry()).append("/").append(leg.getPlaceList().get(0).getCity());
            } else if (leg.getType() == LegType.FINAL.ordinal()) {
                legDesc.append(" --> ").append(leg.getPlaceList().get(0).getCountry()).append("/").append(leg.getPlaceList().get(0).getCity());
            } else {
                String country;
                if (leg.getPlaceList().size() > 1 && !leg.getPlaceList().get(0).getCountry().equals(leg.getPlaceList().get(1).getCountry())) {
                    country = leg.getPlaceList().get(0).getCountry() + ", " + leg.getPlaceList().get(1).getCountry();
                } else {
                    country = leg.getPlaceList().get(0).getCountry();
                }
                String strLeg = "Leg " + leg.getIndex();
                for (int j = i + 1; j < legs.size(); j++) {
                    String country1;
                    if (legs.get(j).getPlaceList().size() > 1 && !legs.get(j).getPlaceList().get(0).getCountry().equals(legs.get(j).getPlaceList().get(1).getCountry())) {
                        country1 = legs.get(j).getPlaceList().get(0).getCountry() + ", " + legs.get(j).getPlaceList().get(1).getCountry();
                    } else {
                        country1 = legs.get(j).getPlaceList().get(0).getCountry();
                    }
                    if (country.equals(country1)) {
                        i++;
                        strLeg = strLeg + "," + legs.get(j).getIndex();
                    } else {
                        break;
                    }
                }
                legDesc.append(" --> ").append(strLeg).append(" ").append(leg.getPlaceList().get(0).getCountry());
            }
        }
        return legDesc.toString();
    }

    /**
     * @param legs     is already ordered by index
     * @param mapItems
     * @return
     */
    private List<LegMapItem> convertLegToMapLegs(List<Leg> legs, List<MapItem> mapItems) {
        List<LegMapItem> list = new ArrayList<>();
        LegMapItem lastItem = null;
        MapCountryDao mapCountryDao = getDaoSession().getMapCountryDao();
        for (int i = 0; i < legs.size(); i++) {
            Leg leg = legs.get(i);
            for (LegPlaces lp : leg.getPlaceList()) {
                String country = lp.getCountry();
                if (lastItem == null || !lastItem.getCountryChn().equals(country)) {
                    LegMapItem item = new LegMapItem();
                    item.setLegs(new ArrayList<>());
                    item.setCountryChn(country);

                    MapCountry mc = mapCountryDao.queryBuilder()
                            .where(MapCountryDao.Properties.NameChn.eq(country))
                            .build().unique();
                    if (mc != null) {
                        item.setCountryInMap(mc.getNameInMap());
                        MapItem mapItem = findMapItemFrom(mapItems, mc.getNameInMap());
                        if (mapItem != null) {
                            mapItem.setSelected(true);
                            item.setMapItem(mapItem);
                        }
                    }
                    list.add(item);
                    lastItem = item;
                }
                lastItem.getLegs().add(leg);
            }
        }
        return list;
    }

    private MapItem findMapItemFrom(List<MapItem> mapItems, String nameInMap) {
        for (MapItem item : mapItems) {
            if (item.getBean().getPlace().equals(nameInMap)) {
                return item;
            }
        }
        return null;
    }

    public void preSeason() {
        Season season = getDaoSession().getSeasonDao().queryBuilder()
                .where(SeasonDao.Properties.Index.eq(mSeason.getIndex() - 1))
                .build().unique();
        if (season == null) {
            messageObserver.setValue("No previous season found");
        } else {
            loadMap(season.getId());
        }
    }

    public void nextSeason() {
        Season season = getDaoSession().getSeasonDao().queryBuilder()
                .where(SeasonDao.Properties.Index.eq(mSeason.getIndex() + 1))
                .build().unique();
        if (season == null) {
            messageObserver.setValue("No next season found");
        } else {
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
