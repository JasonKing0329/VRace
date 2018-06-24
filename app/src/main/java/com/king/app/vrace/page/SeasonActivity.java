package com.king.app.vrace.page;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;

import com.king.app.vrace.R;
import com.king.app.vrace.base.MvvmActivity;
import com.king.app.vrace.databinding.ActivitySeasonBinding;
import com.king.app.vrace.viewmodel.SeasonViewModel;

/**
 * @desc
 * @auth 景阳
 * @time 2018/6/24 0024 15:56
 */

public class SeasonActivity extends MvvmActivity<ActivitySeasonBinding, SeasonViewModel> {

    public static final String EXTRA_SEASON_ID = "season_id";

    @Override
    protected int getContentView() {
        return R.layout.activity_season;
    }

    @Override
    protected void initView() {
        mBinding.rvLegs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.ivTeams.setOnClickListener(view -> manageTeams());

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
    }

    private void manageTeams() {
        Intent intent = new Intent(this, SeasonTeamActivity.class);
        intent.putExtra(SeasonTeamActivity.EXTRA_SEASON_ID, mModel.getSeason().getId());
        startActivity(intent);
    }

    @Override
    protected SeasonViewModel createViewModel() {
        return ViewModelProviders.of(this).get(SeasonViewModel.class);
    }

    @Override
    protected void initData() {

        mModel.loadSeason(getIntent().getLongExtra(EXTRA_SEASON_ID, -1));
    }
}
