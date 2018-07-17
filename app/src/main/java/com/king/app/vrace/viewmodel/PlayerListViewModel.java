package com.king.app.vrace.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.king.app.vrace.base.BaseViewModel;
import com.king.app.vrace.model.entity.Player;
import com.king.app.vrace.model.entity.PlayerDao;
import com.king.app.vrace.model.entity.TeamPlayersDao;

import java.util.ArrayList;
import java.util.Collections;
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
public class PlayerListViewModel extends BaseViewModel {

    public MutableLiveData<List<Player>> playersObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> deleteObserver = new MutableLiveData<>();

    public MutableLiveData<List<String>> provincesObserver = new MutableLiveData<>();

    public MutableLiveData<List<String>> citiesObserver = new MutableLiveData<>();

    private Map<Long, Boolean> mCheckMap;

    public PlayerListViewModel(@NonNull Application application) {
        super(application);
        mCheckMap = new HashMap<>();
    }

    public void loadPlayers() {
        loadingObserver.setValue(true);
        queryPlayers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Player>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Player> seasons) {
                        loadingObserver.setValue(false);
                        playersObserver.setValue(seasons);
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

    private Observable<List<Player>> queryPlayers() {
        return Observable.create(e -> {
            List<Player> list = getDaoSession().getPlayerDao().loadAll();
            // 为方便添加按添加顺序倒序排序
            Collections.reverse(list);
            e.onNext(list);
        });
    }

    public void deletePlayers() {
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
            long playerId = it.next();
            // delete from player
            getDaoSession().getPlayerDao().queryBuilder()
                    .where(PlayerDao.Properties.Id.eq(playerId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getPlayerDao().detachAll();
            // delete from team_player
            getDaoSession().getTeamPlayersDao().queryBuilder()
                    .where(TeamPlayersDao.Properties.PlayerId.eq(playerId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getTeamPlayersDao().detachAll();
        }
    }

    public void loadAutoComplete() {
        queryProvinces()
                .flatMap(list -> {
                    provincesObserver.postValue(list);
                    return queryCities();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<String> list) {
                        citiesObserver.setValue(list);
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

    private Observable<List<String>> queryProvinces() {
        return Observable.create(e -> {
            List<String> list = new ArrayList<>();
            String column = PlayerDao.Properties.Province.columnName;
            Cursor cursor = getDaoSession().getDatabase().rawQuery("SELECT " + column + " FROM " + PlayerDao.TABLENAME + " GROUP BY " + column, null);
            while (cursor.moveToNext()) {
                list.add(cursor.getString(0));
            }
            e.onNext(list);
        });
    }

    private Observable<List<String>> queryCities() {
        return Observable.create(e -> {
            List<String> list = new ArrayList<>();
            String column = PlayerDao.Properties.City.columnName;
            Cursor cursor = getDaoSession().getDatabase().rawQuery("SELECT " + column + " FROM " + PlayerDao.TABLENAME + " GROUP BY " + column, null);
            while (cursor.moveToNext()) {
                list.add(cursor.getString(0));
            }
            e.onNext(list);
        });
    }
}
