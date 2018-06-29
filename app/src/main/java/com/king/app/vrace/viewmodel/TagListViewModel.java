package com.king.app.vrace.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.vrace.base.BaseViewModel;
import com.king.app.vrace.model.ParcelableTags;
import com.king.app.vrace.model.entity.PersonTag;
import com.king.app.vrace.model.entity.PersonTagDao;
import com.king.app.vrace.model.entity.Team;
import com.king.app.vrace.model.entity.TeamDao;
import com.king.app.vrace.model.entity.TeamTagDao;

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
public class TagListViewModel extends BaseViewModel {

    public MutableLiveData<List<PersonTag>> playersObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> deleteObserver = new MutableLiveData<>();

    private Map<Long, Boolean> mCheckMap;

    public TagListViewModel(@NonNull Application application) {
        super(application);
        mCheckMap = new HashMap<>();
    }

    public void loadTags(long teamId) {
        loadingObserver.setValue(true);
        queryTags()
                .flatMap(list -> initCheckStatus(teamId, list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<PersonTag>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<PersonTag> seasons) {
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

    private Observable<List<PersonTag>> queryTags() {
        return Observable.create(e -> e.onNext(getDaoSession().getPersonTagDao().loadAll()));
    }

    private Observable<List<PersonTag>> initCheckStatus(long teamId, List<PersonTag> tags) {
        return Observable.create(e -> {
            Team team = getDaoSession().getTeamDao().queryBuilder()
                    .where(TeamDao.Properties.Id.eq(teamId))
                    .unique();
            if (team != null) {
                List<PersonTag> selectedList = team.getTagList();
                for (PersonTag tag:selectedList) {
                    mCheckMap.put(tag.getId(), true);
                }
            }
            e.onNext(tags);
        });
    }

    public void deleteTags() {
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
            long tagId = it.next();
            // delete from player
            getDaoSession().getPersonTagDao().queryBuilder()
                    .where(PersonTagDao.Properties.Id.eq(tagId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getPersonTagDao().detachAll();
            // delete from team_player
            getDaoSession().getTeamTagDao().queryBuilder()
                    .where(TeamTagDao.Properties.TagId.eq(tagId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getTeamTagDao().detachAll();
        }
    }

    public ParcelableTags getSelectedTags() {
        ParcelableTags tags = new ParcelableTags();
        tags.setTagIdList(new ArrayList<>());
        Iterator<Long> it = mCheckMap.keySet().iterator();
        while (it.hasNext()) {
            tags.getTagIdList().add(it.next());
        }
        return tags;
    }
}
