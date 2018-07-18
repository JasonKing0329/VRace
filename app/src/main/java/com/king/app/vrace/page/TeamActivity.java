package com.king.app.vrace.page;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import com.king.app.vrace.R;
import com.king.app.vrace.base.MvvmActivity;
import com.king.app.vrace.conf.Gender;
import com.king.app.vrace.databinding.ActivityTeamBinding;
import com.king.app.vrace.model.entity.Player;
import com.king.app.vrace.page.adapter.TeamResultAdapter;
import com.king.app.vrace.page.adapter.TeamSeasonChartAdapter;
import com.king.app.vrace.viewmodel.TeamViewModel;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/11 13:43
 */
public class TeamActivity extends MvvmActivity<ActivityTeamBinding, TeamViewModel> {

    public static final String EXTRA_TEAM_ID = "team_id";

    @Override
    protected int getContentView() {
        return R.layout.activity_team;
    }

    @Override
    protected void initView() {
        mBinding.rvSeasons.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mBinding.chart.setDrawDashGrid(true);

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
    }

    @Override
    protected TeamViewModel createViewModel() {
        return ViewModelProviders.of(this).get(TeamViewModel.class);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initData();
    }

    @Override
    protected void initData() {
        mBinding.setModel(mModel);

        mModel.teamObserver.observe(this, team -> {
            updatePlayerBg(team.getPlayerList().get(0), mBinding.tvPlayer1);
            updatePlayerBg(team.getPlayerList().get(1), mBinding.tvPlayer2);
        });

        mModel.teamSeasonObserver.observe(this, seasons -> {
            TeamResultAdapter adapter = new TeamResultAdapter();
            adapter.setList(seasons);
            adapter.setOnItemClickListener((view, position, data) -> goToSeason(data.getBean().getId()));
            mBinding.rvSeasons.setAdapter(adapter);
        });

        mModel.dataObserver.observe(this, teamChartBean -> {
            TeamSeasonChartAdapter adapter = new TeamSeasonChartAdapter();
            adapter.setChartBean(teamChartBean);
            mBinding.chart.setAdapter(adapter);
        });

        mModel.loadTeam(getIntent().getLongExtra(EXTRA_TEAM_ID, -1));
    }

    private void goToSeason(long seasonId) {
        Intent intent = new Intent(this, SeasonActivity.class);
        intent.putExtra(SeasonActivity.EXTRA_SEASON_ID, seasonId);
        startActivity(intent);
    }

    private void updatePlayerBg(Player player, TextView textView) {
        GradientDrawable drawable = (GradientDrawable) textView.getBackground();
        drawable.setColor(player.getGender() == Gender.FEMALE.ordinal() ?
                getResources().getColor(R.color.colorAccent):getResources().getColor(R.color.colorPrimary));
        textView.setBackground(drawable);
    }
}
