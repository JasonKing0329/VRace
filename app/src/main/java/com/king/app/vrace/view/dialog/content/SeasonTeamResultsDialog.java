package com.king.app.vrace.view.dialog.content;

import android.arch.lifecycle.ViewModelProviders;

import com.king.app.vrace.R;
import com.king.app.vrace.base.IFragmentHolder;
import com.king.app.vrace.databinding.FragmentSeasonTeamResultsBinding;
import com.king.app.vrace.view.dialog.DraggableContentFragment;
import com.king.app.vrace.viewmodel.SeasonTeamResultsViewModel;

/**
 * @desc
 * @auth 景阳
 * @time 2018/6/26 0026 19:57
 */

@Deprecated
public class SeasonTeamResultsDialog extends DraggableContentFragment<FragmentSeasonTeamResultsBinding> {

    private SeasonTeamResultsViewModel mModel;

    private long seasonId;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_season_team_results;
    }

    @Override
    protected void initView() {
        mModel = ViewModelProviders.of(this).get(SeasonTeamResultsViewModel.class);

        mModel.dataObserver.observe(this, tableData -> mBinding.tableView.getBuilder()
                .setLegResults(tableData.getLegResults())
                .setLegTitleList(tableData.getLegTitleList())
                .setRelationshipList(tableData.getRelationshipList())
                .setTeamList(tableData.getTeamList())
                .setTitleBgColor(tableData.getTitleBgColor())
                .build());

        mModel.loadResults(seasonId);
    }

    public void setSeasonId(long seasonId) {
        this.seasonId = seasonId;
    }
}
