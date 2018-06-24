package com.king.app.vrace.page;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.LinearLayoutManager;

import com.king.app.jactionbar.OnConfirmListener;
import com.king.app.vrace.R;
import com.king.app.vrace.base.MvvmActivity;
import com.king.app.vrace.databinding.ActivitySeasonTeamsBinding;
import com.king.app.vrace.page.adapter.SeasonTeamsAdapter;
import com.king.app.vrace.utils.ScreenUtils;
import com.king.app.vrace.view.dialog.DraggableDialogFragment;
import com.king.app.vrace.view.dialog.content.SeasonTeamEditor;
import com.king.app.vrace.viewmodel.SeasonViewModel;
import com.king.app.vrace.viewmodel.bean.SeasonTeamItem;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:31
 */
public class SeasonTeamActivity extends MvvmActivity<ActivitySeasonTeamsBinding, SeasonViewModel> {

    public static final String EXTRA_SEASON_ID = "season_id";

    private SeasonTeamsAdapter adapter;

    private boolean isEditMode;

    @Override
    protected int getContentView() {
        return R.layout.activity_season_teams;
    }

    @Override
    protected void initView() {
        mBinding.rvTeams.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        mBinding.rvTeams.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//                if (parent.getChildAdapterPosition(view) > 0) {
//                    outRect.top = ScreenUtils.dp2px(10);
//                }
//            }
//        });

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());

        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_add:
                    editTeam(null);
                    break;
                case R.id.menu_delete:
                    if (adapter != null) {
                        mBinding.actionbar.showConfirmStatus(menuId);
                        adapter.setSelectMode(true);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case R.id.menu_edit:
                    mBinding.actionbar.showConfirmStatus(menuId);
                    isEditMode = true;
                    break;
            }
        });

        mBinding.actionbar.setOnConfirmListener(new OnConfirmListener() {
            @Override
            public boolean disableInstantDismissConfirm() {
                return false;
            }

            @Override
            public boolean disableInstantDismissCancel() {
                return false;
            }

            @Override
            public boolean onConfirm(int actionId) {
                switch (actionId) {
                    case R.id.menu_delete:
                        warningDelete();
                        return false;
                }
                isEditMode = false;
                return true;
            }

            @Override
            public boolean onCancel(int actionId) {
                switch (actionId) {
                    case R.id.menu_delete:
                        adapter.setSelectMode(false);
                        adapter.notifyDataSetChanged();
                        break;
                }
                isEditMode = false;
                return true;
            }
        });
    }

    @Override
    protected SeasonViewModel createViewModel() {
        return ViewModelProviders.of(this).get(SeasonViewModel.class);
    }

    @Override
    protected void initData() {
        mModel.seasonObserver.observe(this, season -> mBinding.actionbar.setTitle("S" + season.getIndex()));
        mModel.teamsObserver.observe(this, teams -> showTeams(teams));
        mModel.deleteTeamsObserver.observe(this, deleted -> {
            mBinding.actionbar.cancelConfirmStatus();
            adapter.setSelectMode(false);
            adapter.notifyDataSetChanged();
            mModel.loadTeams(mModel.getSeason().getId());
        });

        mModel.loadTeams(getIntent().getLongExtra(EXTRA_SEASON_ID, -1));
    }

    private void warningDelete() {
        showConfirmCancelMessage("Delete season will delete all data related to season, continue?",
                (dialog, which) -> mModel.deleteTeams(), null);
    }

    private void showTeams(List<SeasonTeamItem> teams) {
        if (adapter == null) {
            adapter = new SeasonTeamsAdapter();
            adapter.setList(teams);
            adapter.setCheckMap(mModel.getTeamCheckMap());
            adapter.setOnItemClickListener((view, position, data) -> {
                if (isEditMode) {
                    editTeam(data);
                }
                else {
                    showTeamPage(data);
                }
            });
            mBinding.rvTeams.setAdapter(adapter);
        }
        else {
            adapter.setList(teams);
            adapter.notifyDataSetChanged();
        }
    }

    private void showTeamPage(SeasonTeamItem data) {
    }

    private void editTeam(SeasonTeamItem team) {
        SeasonTeamEditor editor = new SeasonTeamEditor();
        editor.setSeasonId(mModel.getSeason().getId());
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setMaxHeight(ScreenUtils.getScreenHeight() * 4 / 5);
        dialogFragment.setContentFragment(editor);
        if (team == null) {
            dialogFragment.setTitle("New team for S" + mModel.getSeason().getIndex());
        }
        else {
            editor.setTeam(team.getBean());
            dialogFragment.setTitle("Edit S" + mModel.getSeason().getIndex() + " " + team.getName());
        }
        dialogFragment.show(getSupportFragmentManager(), "SeasonTeamEditor");
    }
}
