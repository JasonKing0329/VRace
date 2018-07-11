package com.king.app.vrace.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.king.app.vrace.base.BaseViewModel;
import com.king.app.vrace.model.TeamModel;
import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.LegDao;
import com.king.app.vrace.model.entity.LegTeam;
import com.king.app.vrace.model.entity.LegTeamDao;
import com.king.app.vrace.model.entity.Player;
import com.king.app.vrace.model.entity.Team;
import com.king.app.vrace.model.entity.TeamSeason;
import com.king.app.vrace.utils.PlaceUtil;
import com.king.app.vrace.viewmodel.bean.TeamChartBean;
import com.king.app.vrace.viewmodel.bean.TeamSeasonItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
 * @date: 2018/7/11 11:46
 */
public class TeamViewModel extends BaseViewModel {

    public MutableLiveData<TeamChartBean> dataObserver = new MutableLiveData<>();

    public MutableLiveData<Team> teamObserver = new MutableLiveData<>();

    public MutableLiveData<List<TeamSeasonItem>> teamSeasonObserver = new MutableLiveData<>();

    public ObservableField<String> titleText = new ObservableField<>();

    public ObservableField<String> player1Text = new ObservableField<>();

    public ObservableField<String> player2Text = new ObservableField<>();

    public ObservableField<String> age1Text = new ObservableField<>();

    public ObservableField<String> age2Text = new ObservableField<>();

    public ObservableField<String> place1Text = new ObservableField<>();

    public ObservableField<String> place2Text = new ObservableField<>();

    public ObservableField<String> occupy1Text = new ObservableField<>();

    public ObservableField<String> occupy2Text = new ObservableField<>();

    public ObservableField<String> teamPlaceText = new ObservableField<>();

    private Team mTeam;

    public TeamViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadTeam(long mTeamId) {
        queryTeam(mTeamId)
                .flatMap(team -> {
                    mTeam = team;
                    teamObserver.postValue(team);
                    return loadSeasons(team);
                })
                .flatMap(seasons -> {
                    teamSeasonObserver.postValue(seasons);
                    return toChartBean(mTeam);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<TeamChartBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(TeamChartBean bean) {
                        dataObserver.setValue(bean);

                        Player player = mTeam.getPlayerList().get(0);
                        player1Text.set(player.getName());
                        age1Text.set(String.valueOf(player.getAge()));
                        place1Text.set(PlaceUtil.getCombinePlace(player.getProvince(), player.getCity()));
                        occupy1Text.set(player.getOccupy());

                        player = mTeam.getPlayerList().get(1);
                        player2Text.set(player.getName());
                        age2Text.set(String.valueOf(player.getAge()));
                        place2Text.set(PlaceUtil.getCombinePlace(player.getProvince(), player.getCity()));
                        occupy2Text.set(player.getOccupy());

                        titleText.set(mTeam.getCode());
                        teamPlaceText.set(PlaceUtil.getCombinePlace(mTeam.getProvince(), mTeam.getCity()));
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

    private Observable<Team> queryTeam(long mTeamId) {
        return Observable.create(e -> e.onNext(getDaoSession().getTeamDao().load(mTeamId)));
    }

    private Observable<List<TeamSeasonItem>> loadSeasons(Team team) {
        return Observable.create(e -> {
            List<TeamSeasonItem> list = new ArrayList<>();
            TeamModel model = new TeamModel();
            for (TeamSeason ts:team.getSeasonList()) {
                TeamSeasonItem item = new TeamSeasonItem();
                item.setBean(ts.getSeason());
                item.setSeason("Season " + ts.getSeason().getIndex());
                item.setResult(model.getTeamSeasonResults(ts.getTeamId(), ts.getSeasonId()).getEndRank());
                list.add(item);
            }
            e.onNext(list);
        });
    }

    private Observable<TeamChartBean> toChartBean(Team team) {
        return Observable.create(e -> {
            TeamChartBean bean = new TeamChartBean();
            bean.setLegList(new ArrayList<>());
            bean.setyValueList(new ArrayList<>());
            for (int i = 0; i < 12; i ++) {
                bean.getyValueList().add(12 - i);
            }
            bean.setTeamList(new ArrayList<>());
            bean.setTeamResultList(new ArrayList<>());
            bean.setSeasonList(new ArrayList<>());
            TeamLegComparator comparator = new TeamLegComparator();
            for (TeamSeason ts:team.getSeasonList()) {
                bean.getSeasonList().add(ts.getSeason());
                List<Leg> legs = getDaoSession().getLegDao().queryBuilder()
                        .where(LegDao.Properties.SeasonId.eq(ts.getSeasonId()))
                        .build().list();
                if (legs.size() > bean.getLegList().size()) {
                    bean.setLegList(legs);
                }
                List<LegTeam> lts = getDaoSession().getLegTeamDao().queryBuilder()
                        .where(LegTeamDao.Properties.SeasonId.eq(ts.getSeasonId()))
                        .where(LegTeamDao.Properties.TeamId.eq(ts.getTeamId()))
                        .build().list();
                // sort by leg index
                Collections.sort(lts, comparator);

                bean.getTeamList().add(team);
                bean.getTeamResultList().add(lts);
            }
            e.onNext(bean);
        });
    }

    private class TeamLegComparator implements Comparator<LegTeam> {

        @Override
        public int compare(LegTeam left, LegTeam right) {
            return left.getLeg().getIndex() - right.getLeg().getIndex();
        }
    }
}
