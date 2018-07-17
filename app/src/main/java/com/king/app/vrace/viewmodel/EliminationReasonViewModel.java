package com.king.app.vrace.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.vrace.base.BaseViewModel;
import com.king.app.vrace.model.entity.EliminationReason;
import com.king.app.vrace.model.entity.EliminationReasonDao;
import com.king.app.vrace.model.entity.TeamEliminationDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
 * @date: 2018/6/21 19:21
 */
public class EliminationReasonViewModel extends BaseViewModel {

    public MutableLiveData<List<EliminationReason>> reasonsObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> deleteObserver = new MutableLiveData<>();

    private Map<Long, Boolean> mCheckMap;

    public EliminationReasonViewModel(@NonNull Application application) {
        super(application);
        mCheckMap = new HashMap<>();
    }

    public void loadReasons() {
        loadingObserver.setValue(true);
        queryReasons()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<EliminationReason>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<EliminationReason> relationships) {
                        loadingObserver.setValue(false);
                        reasonsObserver.setValue(relationships);
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

    private Observable<List<EliminationReason>> queryReasons() {
        return Observable.create(e -> {
            List<EliminationReason> total = new ArrayList<>();
            List<EliminationReason> parents = getDaoSession().getEliminationReasonDao()
                    .queryBuilder()
                    .where(EliminationReasonDao.Properties.ParentId.eq(0))
                    .build().list();
            for (EliminationReason parent:parents) {
                total.add(parent);
                List<EliminationReason> children = getDaoSession().getEliminationReasonDao()
                        .queryBuilder()
                        .where(EliminationReasonDao.Properties.ParentId.eq(parent.getId()))
                        .build().list();
                if (children.size() > 0) {
                    total.addAll(children);
                }
            }
            e.onNext(total);
        });
    }

    public void deleteReasons() {
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
            long reasonId = it.next();
            // delete from relationship
            EliminationReason reason = getDaoSession().getEliminationReasonDao().queryBuilder()
                    .where(EliminationReasonDao.Properties.Id.eq(reasonId))
                    .build().unique();
            if (reason != null) {
                // if it's parent, delete all children
                if (reason.getParentId() != 0) {
                    getDaoSession().getEliminationReasonDao().queryBuilder()
                            .where(EliminationReasonDao.Properties.ParentId.eq(reasonId))
                            .buildDelete()
                            .executeDeleteWithoutDetachingEntities();
                }
                getDaoSession().getEliminationReasonDao().delete(reason);
            }
            getDaoSession().getEliminationReasonDao().detachAll();
            // delete from team_elimination
            getDaoSession().getTeamEliminationDao().queryBuilder()
                    .where(TeamEliminationDao.Properties.ReasonId.eq(reasonId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getTeamEliminationDao().detachAll();
        }
    }
}
