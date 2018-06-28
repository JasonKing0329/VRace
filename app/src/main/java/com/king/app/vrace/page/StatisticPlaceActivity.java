package com.king.app.vrace.page;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.king.app.vrace.R;
import com.king.app.vrace.base.MvvmActivity;
import com.king.app.vrace.conf.AppConstants;
import com.king.app.vrace.databinding.ActivityStatisticPlaceBinding;
import com.king.app.vrace.model.setting.SettingProperty;
import com.king.app.vrace.page.adapter.StatCountryAdapter;
import com.king.app.vrace.page.adapter.StatPlaceAdapter;
import com.king.app.vrace.page.adapter.StatisticPlaceAdapter;
import com.king.app.vrace.utils.ScreenUtils;
import com.king.app.vrace.viewmodel.StatisticPlaceViewModel;
import com.king.app.vrace.viewmodel.bean.StatContinentItem;
import com.king.app.vrace.viewmodel.bean.StatCountryItem;
import com.king.app.vrace.viewmodel.bean.StatisticPlaceItem;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:31
 */
public class StatisticPlaceActivity extends MvvmActivity<ActivityStatisticPlaceBinding, StatisticPlaceViewModel> {

    private StatisticPlaceAdapter adapter;
    private StatPlaceAdapter groupAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_statistic_place;
    }

    @Override
    protected void initView() {
        mBinding.rvPlaces.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvPlaces.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = ScreenUtils.dp2px(10);
            }
        });

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());

        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_group_mode:
                    if (SettingProperty.getStatisticPlaceType() == AppConstants.STAT_PLACE_GROUP_BY_CONT) {
                        SettingProperty.setStatisticPlaceType(AppConstants.STAT_PLACE_GROUP_NONE);
                        mModel.statistic(AppConstants.STAT_PLACE_GROUP_NONE);
                    }
                    else {
                        SettingProperty.setStatisticPlaceType(AppConstants.STAT_PLACE_GROUP_BY_CONT);
                        mModel.statistic(AppConstants.STAT_PLACE_GROUP_BY_CONT);
                    }
                    break;
            }
        });

    }

    @Override
    protected StatisticPlaceViewModel createViewModel() {
        return ViewModelProviders.of(this).get(StatisticPlaceViewModel.class);
    }

    @Override
    protected void initData() {
        mModel.placeObserver.observe(this, list -> showPlaces(list));
        mModel.groupsObserver.observe(this, list -> showGroups(list));
    }

    private void showGroups(List<StatContinentItem> list) {
        groupAdapter = new StatPlaceAdapter(list);
        groupAdapter.setOnCountryItemClickListener(new StatCountryAdapter.OnCountryItemClickListener() {
            @Override
            public void onClickCountry(StatCountryItem item) {

            }
        });
        mBinding.rvPlaces.setAdapter(groupAdapter);
        mBinding.actionbar.updateMenuText(R.id.menu_group_mode, "No group");
    }

    private void showPlaces(List<StatisticPlaceItem> teams) {
        if (adapter == null) {
            adapter = new StatisticPlaceAdapter();
            adapter.setList(teams);
            adapter.setOnItemClickListener((view, position, data) -> {

            });
            mBinding.rvPlaces.setAdapter(adapter);
        }
        else {
            adapter.setList(teams);
            adapter.notifyDataSetChanged();
        }
        mBinding.actionbar.updateMenuText(R.id.menu_group_mode, "Group by continent");
    }

}
