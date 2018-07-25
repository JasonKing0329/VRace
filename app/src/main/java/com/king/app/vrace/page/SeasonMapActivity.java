package com.king.app.vrace.page;

import android.arch.lifecycle.ViewModelProviders;
import android.text.TextUtils;

import com.king.app.vrace.R;
import com.king.app.vrace.base.MvvmActivity;
import com.king.app.vrace.databinding.ActivitySeasonMapBinding;
import com.king.app.vrace.page.adapter.SeasonMapAdapter;
import com.king.app.vrace.viewmodel.SeasonMapViewModel;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/24 9:24
 */
public class SeasonMapActivity extends MvvmActivity<ActivitySeasonMapBinding, SeasonMapViewModel> {

    public static final String EXTRA_SEASON_ID = "season_id";

    private SeasonMapAdapter adapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_season_map;
    }

    @Override
    protected void initView() {
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_pre_season:
                    mModel.preSeason();
                    break;
                case R.id.menu_next_season:
                    mModel.nextSeason();
                    break;
                case R.id.menu_recreate_map:
                    mModel.recreateMap();
                    break;
                case R.id.menu_recreate_country:
                    mModel.recreateCountry();
                    break;
            }
        });
        mBinding.actionbar.setOnBackListener(() -> finish());

        mBinding.mapView.disableTogglePlace();
    }

    @Override
    protected SeasonMapViewModel createViewModel() {
        return ViewModelProviders.of(this).get(SeasonMapViewModel.class);
    }

    @Override
    protected void initData() {
        mModel.mapObserver.observe(this, data -> {
            adapter = new SeasonMapAdapter();
            adapter.setMap(data.getMap());
            adapter.setMapItems(data.getItemList());
            adapter.setLinePoint(data.getLinePoint());
            mBinding.mapView.setAdapter(adapter);
            mBinding.tvLegs.setText(data.getLegsDesc());
            if (!TextUtils.isEmpty(data.getErrorInfo())) {
                showMessageLong(data.getErrorInfo());
            }
        });
        mModel.seasonObserver.observe(this, season -> mBinding.actionbar.setTitle("Season" + season.getIndex()));
        mModel.loadMap(getIntent().getLongExtra(EXTRA_SEASON_ID, -1));
    }

}
