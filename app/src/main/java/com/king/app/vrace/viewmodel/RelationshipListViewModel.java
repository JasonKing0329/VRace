package com.king.app.vrace.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.vrace.base.BaseViewModel;
import com.king.app.vrace.model.entity.Relationship;
import com.king.app.vrace.model.entity.RelationshipDao;
import com.king.app.vrace.model.entity.Team;
import com.king.app.vrace.model.entity.TeamDao;

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
public class RelationshipListViewModel extends BaseViewModel {

    public MutableLiveData<List<Relationship>> relationsObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> deleteObserver = new MutableLiveData<>();

    private Map<Long, Boolean> mCheckMap;

    public RelationshipListViewModel(@NonNull Application application) {
        super(application);
        mCheckMap = new HashMap<>();
    }

    public void loadRelationships() {
        loadingObserver.setValue(true);
        queryRelationships()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Relationship>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Relationship> relationships) {
                        loadingObserver.setValue(false);
                        relationsObserver.setValue(relationships);
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

    private Observable<List<Relationship>> queryRelationships() {
        return Observable.create(e -> {
            List<Relationship> total = new ArrayList<>();
            List<Relationship> parents = getDaoSession().getRelationshipDao()
                    .queryBuilder()
                    .where(RelationshipDao.Properties.ParentId.eq(0))
                    .build().list();
            for (Relationship parent:parents) {
                total.add(parent);
                List<Relationship> children = getDaoSession().getRelationshipDao()
                        .queryBuilder()
                        .where(RelationshipDao.Properties.ParentId.eq(parent.getId()))
                        .build().list();
                if (children.size() > 0) {
                    total.addAll(children);
                }
            }
            e.onNext(total);
        });
    }

    public void deleteRelationships() {
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
            long relationshipId = it.next();
            // delete from relationship
            Relationship relationship = getDaoSession().getRelationshipDao().queryBuilder()
                    .where(RelationshipDao.Properties.Id.eq(relationshipId))
                    .build().unique();
            if (relationship != null) {
                // if it's parent, delete all children
                if (relationship.getParentId() != 0) {
                    getDaoSession().getRelationshipDao().queryBuilder()
                            .where(RelationshipDao.Properties.ParentId.eq(relationshipId))
                            .buildDelete()
                            .executeDeleteWithoutDetachingEntities();
                }
                getDaoSession().getRelationshipDao().delete(relationship);
            }
            getDaoSession().getRelationshipDao().detachAll();
            // delete from team
            Team team = getDaoSession().getTeamDao().queryBuilder()
                    .where(TeamDao.Properties.RelationshipId.eq(relationshipId))
                    .build().unique();
            if (team != null) {
                team.setRelationshipId(0);
                getDaoSession().getTeamDao().delete(team);
            }
        }
    }
}
