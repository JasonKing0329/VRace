package com.king.app.vrace.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.vrace.base.BaseViewModel;
import com.king.app.vrace.conf.AppConstants;
import com.king.app.vrace.model.entity.EliminationReason;
import com.king.app.vrace.model.entity.EliminationReasonDao;
import com.king.app.vrace.model.entity.LegTeam;
import com.king.app.vrace.model.entity.TeamElimination;
import com.king.app.vrace.model.entity.TeamEliminationDao;
import com.king.app.vrace.model.setting.SettingProperty;
import com.king.app.vrace.viewmodel.bean.EliminationItem;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
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

    public MutableLiveData<List<EliminationItem>> reasonsObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> deleteObserver = new MutableLiveData<>();

    public MutableLiveData<String[]> popupObserver = new MutableLiveData<>();

    private Map<Long, Boolean> mCheckMap;

    private List<TeamElimination> mPopupList;

    public EliminationReasonViewModel(@NonNull Application application) {
        super(application);
        mCheckMap = new HashMap<>();
    }

    public void loadReasons() {
        loadingObserver.setValue(true);
        queryReasons()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<EliminationItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<EliminationItem> relationships) {
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

    private Observable<List<EliminationItem>> queryReasons() {
        return Observable.create(e -> {
            List<EliminationItem> total = new ArrayList<>();
            List<EliminationReason> parents = getDaoSession().getEliminationReasonDao()
                    .queryBuilder()
                    .where(EliminationReasonDao.Properties.ParentId.eq(0))
                    .build().list();
            for (EliminationReason parent:parents) {
                EliminationItem item = new EliminationItem();
                item.setBean(parent);
                item.setName(parent.getName());
                total.add(item);

                List<EliminationReason> children = getDaoSession().getEliminationReasonDao()
                        .queryBuilder()
                        .where(EliminationReasonDao.Properties.ParentId.eq(parent.getId()))
                        .build().list();
                if (children.size() == 0) {
                    int count = (int) getDaoSession().getTeamEliminationDao().queryBuilder()
                            .where(TeamEliminationDao.Properties.ReasonId.eq(parent.getId()))
                            .buildCount().count();
                    item.setNumber(count);
                }
                else {
                    int sum = 0;
                    for (int i = 0; i < children.size(); i ++) {
                        EliminationItem child = new EliminationItem();
                        child.setBean(children.get(i));
                        child.setName(children.get(i).getName());
                        total.add(child);
                        int count = (int) getDaoSession().getTeamEliminationDao().queryBuilder()
                                .where(TeamEliminationDao.Properties.ReasonId.eq(children.get(i).getId()))
                                .buildCount().count();
                        child.setNumber(count);
                        sum += count;
                    }
                    item.setNumber(sum);
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

    public void convertToTextList(EliminationReason item) {

        Observable.create((ObservableOnSubscribe<List<TeamElimination>>) e -> {
            List<TeamElimination> list = new ArrayList<>();
            // parent reason
            if (item.getParent() == null) {
                List<EliminationReason> children = getDaoSession().getEliminationReasonDao().queryBuilder()
                        .where(EliminationReasonDao.Properties.ParentId.eq(item.getId()))
                        .build().list();
                if (children.size() == 0) {
                    list = getReasonRelations(item.getId());
                }
                else {
                    for (EliminationReason reason:children) {
                        list.addAll(getReasonRelations(reason.getId()));
                    }
                }
            }
            else {
                list = getReasonRelations(item.getId());
            }
            e.onNext(list);
        })
                .flatMap(list -> {
                    mPopupList = list;
                    return toTexts(list);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(String[] texts) {
                        popupObserver.setValue(texts);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public TeamElimination getSelectedEliminateTeam(int position) {
        return mPopupList.get(position);
    }

    private List<TeamElimination> getReasonRelations(long reasonId) {

        List<TeamElimination> list = getDaoSession().getTeamEliminationDao().queryBuilder()
                .where(TeamEliminationDao.Properties.ReasonId.eq(reasonId))
                .build().list();
        return list;
    }

    private ObservableSource<String[]> toTexts(List<TeamElimination> list) {
        return observer -> {
            String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); i ++) {
                TeamElimination te = list.get(i);
                String teamCode;
                if (AppConstants.DATABASE_REAL == SettingProperty.getDatabaseType()) {
                    teamCode = te.getTeam().getPlayerList().get(0).getName() + "&" + te.getTeam().getPlayerList().get(1).getName();
                }
                else {
                    teamCode = te.getTeam().getCode();
                }
                array[i] = "S" + te.getSeason().getIndex() + "  " + teamCode
                        + "  Leg" + te.getLeg().getIndex() + " " + te.getLeg().getPlaceList().get(0).getCountry();
            }
            observer.onNext(array);
        };
    }
}
