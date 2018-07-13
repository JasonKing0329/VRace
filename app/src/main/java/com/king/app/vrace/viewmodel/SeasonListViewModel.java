package com.king.app.vrace.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.vrace.base.BaseViewModel;
import com.king.app.vrace.model.entity.LegDao;
import com.king.app.vrace.model.entity.Season;
import com.king.app.vrace.model.entity.SeasonDao;
import com.king.app.vrace.model.entity.TeamSeasonDao;
import com.king.app.vrace.model.setting.SettingProperty;
import com.king.app.vrace.utils.DBExportor;

import java.util.HashMap;
import java.util.Iterator;
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
 * @date: 2018/6/21 19:21
 */
public class SeasonListViewModel extends BaseViewModel {

    public MutableLiveData<List<Season>> seasonsObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> deleteObserver = new MutableLiveData<>();

    private Map<Long, Boolean> mCheckMap;

    private int mDatabaseType;

    public SeasonListViewModel(@NonNull Application application) {
        super(application);
        mCheckMap = new HashMap<>();
        mDatabaseType = SettingProperty.getDatabaseType();
    }

    public void loadSeasons() {
        loadingObserver.setValue(true);
        querySeasons()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Season>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Season> seasons) {
                        loadingObserver.setValue(false);
                        seasonsObserver.setValue(seasons);
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

    public Map<Long, Boolean> getCheckMap() {
        return mCheckMap;
    }

    private Observable<List<Season>> querySeasons() {
        return Observable.create(e -> e.onNext(getDaoSession().getSeasonDao()
                    .queryBuilder()
                    .orderDesc(SeasonDao.Properties.Index)
                    .build().list()));
    }

    public void deleteSeasons() {
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
            long seasonId = it.next();
            // delete from season
            getDaoSession().getSeasonDao().queryBuilder()
                    .where(SeasonDao.Properties.Id.eq(seasonId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getSeasonDao().detachAll();
            // delete from leg
            getDaoSession().getLegDao().queryBuilder()
                    .where(LegDao.Properties.SeasonId.eq(seasonId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getLegDao().detachAll();
            // delete from team_season
            getDaoSession().getTeamSeasonDao().queryBuilder()
                    .where(TeamSeasonDao.Properties.SeasonId.eq(seasonId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getTeamSeasonDao().detachAll();
        }
    }

    public void saveAsHistory() {
        Observable.create(e -> {
            DBExportor.exportAsHistory();
            e.onNext(new Object());
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Object object) {
                        messageObserver.setValue("save successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("save failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public boolean isDatabaseChanged() {
        int type = SettingProperty.getDatabaseType();
        if (mDatabaseType != type) {
            mDatabaseType = type;
            return true;
        }
        return false;
    }
}
