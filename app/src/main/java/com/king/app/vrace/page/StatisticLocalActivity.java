package com.king.app.vrace.page;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.king.app.vrace.R;
import com.king.app.vrace.base.MvvmActivity;
import com.king.app.vrace.conf.AppConstants;
import com.king.app.vrace.databinding.ActivityStatisticLocalBinding;
import com.king.app.vrace.page.adapter.StatisticPlaceAdapter;
import com.king.app.vrace.utils.ScreenUtils;
import com.king.app.vrace.view.dialog.AlertDialogFragment;
import com.king.app.vrace.viewmodel.StatisticLocalViewModel;
import com.king.app.vrace.viewmodel.bean.PlaceSeason;
import com.king.app.vrace.viewmodel.bean.StatisticPlaceItem;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:31
 */
public class StatisticLocalActivity extends MvvmActivity<ActivityStatisticLocalBinding, StatisticLocalViewModel> {

    private StatisticPlaceAdapter adapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_statistic_local;
    }

    @Override
    protected void initView() {

        mBinding.tvDepart.setSelected(true);

        mBinding.rvPlaces.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvPlaces.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = ScreenUtils.dp2px(10);
            }
        });

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());

        mBinding.llDepart.setOnClickListener(view -> onSelectLocalType(AppConstants.STAT_LOCAL_DEPART));
        mBinding.llFinal.setOnClickListener(view -> onSelectLocalType(AppConstants.STAT_LOCAL_FINAL));
    }

    private void onSelectLocalType(int type) {
        if (type == AppConstants.STAT_LOCAL_FINAL) {
            mBinding.llFinal.setShowDividers(LinearLayoutCompat.SHOW_DIVIDER_END);
            mBinding.tvFinal.setSelected(true);
            mBinding.llDepart.setShowDividers(LinearLayoutCompat.SHOW_DIVIDER_NONE);
            mBinding.tvDepart.setSelected(false);
        }
        else {
            mBinding.llFinal.setShowDividers(LinearLayoutCompat.SHOW_DIVIDER_NONE);
            mBinding.tvFinal.setSelected(false);
            mBinding.llDepart.setShowDividers(LinearLayoutCompat.SHOW_DIVIDER_END);
            mBinding.tvDepart.setSelected(true);
        }
        mModel.statistic(type);
    }

    @Override
    protected StatisticLocalViewModel createViewModel() {
        return ViewModelProviders.of(this).get(StatisticLocalViewModel.class);
    }

    @Override
    protected void initData() {
        mModel.placeObserver.observe(this, list -> showPlaces(list));
        mModel.placeSeasonsObserver.observe(this, list -> showPlaceSeasons(list));
    }

    private void showPlaces(List<StatisticPlaceItem> teams) {
        if (adapter == null) {
            adapter = new StatisticPlaceAdapter();
            adapter.setList(teams);
            adapter.setOnItemClickListener((view, position, data) -> {
                onSelectCity(data.getPlace());
            });
            mBinding.rvPlaces.setAdapter(adapter);
        }
        else {
            adapter.setList(teams);
            adapter.notifyDataSetChanged();
        }
        mBinding.actionbar.updateMenuText(R.id.menu_group_mode, "Group by continent");
    }

    private void showPlaceSeasons(List<PlaceSeason> list) {
        String[] textList = mModel.convertToTextList(list);
        new AlertDialogFragment()
                .setTitle(null)
                .setItems(textList, (dialog, which) -> goToSeasonOrLeg(list.get(which)))
                .show(getSupportFragmentManager(), "AlertDialogFragment");
    }

    private void goToSeasonOrLeg(PlaceSeason placeSeason) {
        Intent intent = new Intent();
        if (placeSeason.getLegs().size() > 1) {
            intent.setClass(this, SeasonActivity.class);
            intent.putExtra(SeasonActivity.EXTRA_SEASON_ID, placeSeason.getSeason().getId());
        }
        else {
            intent.setClass(this, LegActivity.class);
            intent.putExtra(LegActivity.EXTRA_LEG_ID, placeSeason.getLegs().get(0).getId());
        }
        startActivity(intent);
    }

    private void onSelectCity(String city) {
        mModel.findCitySeasons(city);
    }

}
