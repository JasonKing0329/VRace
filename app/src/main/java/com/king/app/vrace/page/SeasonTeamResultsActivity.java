package com.king.app.vrace.page;

import android.arch.lifecycle.ViewModelProviders;

import com.king.app.vrace.R;
import com.king.app.vrace.base.MvvmActivity;
import com.king.app.vrace.databinding.ActivitySeasonTeamResultsBinding;
import com.king.app.vrace.page.adapter.TeamChartAdapter;
import com.king.app.vrace.viewmodel.SeasonTeamResultsViewModel;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/4 13:59
 */
public class SeasonTeamResultsActivity extends MvvmActivity<ActivitySeasonTeamResultsBinding, SeasonTeamResultsViewModel> {

    public static final String EXTRA_SEASON_ID = "season_id";

    @Override
    protected int getContentView() {
        return R.layout.activity_season_team_results;
    }

    @Override
    protected void initView() {
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_chart:
                    mModel.chartView();
                    break;
                case R.id.menu_table:
                    mModel.tableView();
                    break;
                case R.id.menu_pre_season:
                    mModel.preSeason();
                    break;
                case R.id.menu_next_season:
                    mModel.nextSeason();
                    break;
            }
        });
        mBinding.actionbar.setOnBackListener(() -> finish());

        mBinding.chart.setDrawDashGrid(true);
    }

    @Override
    protected SeasonTeamResultsViewModel createViewModel() {
        return ViewModelProviders.of(this).get(SeasonTeamResultsViewModel.class);
    }

    @Override
    protected void initData() {
        mBinding.setModel(mModel);

        mModel.seasonObserver.observe(this, season -> mBinding.actionbar.setTitle("Season " + season.getIndex()));
        mModel.dataObserver.observe(this, tableData -> {
            mBinding.tableView.getBuilder()
                    .setLegResults(tableData.getLegResults())
                    .setLegTitleList(tableData.getLegTitleList())
                    .setRelationshipList(tableData.getRelationshipList())
                    .setTeamList(tableData.getTeamList())
                    .setTitleBgColor(tableData.getTitleBgColor())
                    .build();
        });

        mModel.chartObserver.observe(this, teamChartBean -> {
            TeamChartAdapter adapter = new TeamChartAdapter();
            adapter.setChartBean(teamChartBean);
            mBinding.chart.setAdapter(adapter);
        });

        mModel.loadResults(getIntent().getLongExtra(EXTRA_SEASON_ID, -1));
    }
}
