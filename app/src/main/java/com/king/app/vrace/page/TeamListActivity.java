package com.king.app.vrace.page;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.PopupMenu;

import com.king.app.jactionbar.OnConfirmListener;
import com.king.app.vrace.R;
import com.king.app.vrace.base.MvvmActivity;
import com.king.app.vrace.conf.AppConstants;
import com.king.app.vrace.databinding.ActivityTeamListBinding;
import com.king.app.vrace.page.adapter.StatProvinceTeamAdapter;
import com.king.app.vrace.page.adapter.TeamListAdapter;
import com.king.app.vrace.utils.ScreenUtils;
import com.king.app.vrace.view.dialog.DraggableDialogFragment;
import com.king.app.vrace.view.dialog.content.TeamEditor;
import com.king.app.vrace.viewmodel.TeamListViewModel;
import com.king.app.vrace.viewmodel.bean.StatProvinceItem;
import com.king.app.vrace.viewmodel.bean.TeamListItem;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:31
 */
public class TeamListActivity extends MvvmActivity<ActivityTeamListBinding, TeamListViewModel> {

    public static final String EXTRA_SELECT_MODE = "select_mode";

    public static final String RESP_TEAM_ID = "team_id";

    private TeamListAdapter adapter;
    private StatProvinceTeamAdapter groupAdapter;

    private boolean isEditMode;

    private boolean isSelectMode;

    @Override
    protected int getContentView() {
        return R.layout.activity_team_list;
    }

    @Override
    protected void initView() {

        isSelectMode = getIntent().getBooleanExtra(EXTRA_SELECT_MODE, false);

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

        mBinding.actionbar.registerPopupMenu(R.id.menu_sort);
        mBinding.actionbar.setPopupMenuProvider((iconMenuId, anchorView) -> {
            switch (iconMenuId) {
                case R.id.menu_sort:
                    return getSortPopup(anchorView);
            }
            return null;
        });
    }

    private PopupMenu getSortPopup(View anchorView) {
        PopupMenu menu = new PopupMenu(this, anchorView);
        menu.getMenuInflater().inflate(R.menu.team_sort, menu.getMenu());
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_sort_champions:
                    mModel.onSortTypeChanged(AppConstants.TEAM_SORT_CHAMPIONS);
                    break;
                case R.id.menu_sort_point:
                    mModel.onSortTypeChanged(AppConstants.TEAM_SORT_POINT);
                    break;
                case R.id.menu_sort_season:
                    mModel.onSortTypeChanged(AppConstants.TEAM_SORT_SEASON);
                    break;
                case R.id.menu_sort_province:
                    mModel.onSortTypeChanged(AppConstants.TEAM_SORT_PROVINCE);
                    break;
                default:
                    mModel.onSortTypeChanged(AppConstants.TEAM_SORT_NONE);
                    break;
            }
            return true;
        });
        return menu;
    }

    @Override
    protected TeamListViewModel createViewModel() {
        return ViewModelProviders.of(this).get(TeamListViewModel.class);
    }

    @Override
    protected void initData() {
        mBinding.setModel(mModel);
        mModel.teamsObserver.observe(this, teams -> {
            groupAdapter = null;
            showTeams(teams);
        });
        mModel.deleteObserver.observe(this, deleted -> {
            mBinding.actionbar.cancelConfirmStatus();
            adapter.setSelectMode(false);
            adapter.notifyDataSetChanged();
            mModel.loadTeams();
        });
        mModel.provinceTeamsObserver.observe(this, teams -> {
            adapter = null;
            showProvinceTeams(teams);
        });

        mModel.loadTeams();
    }

    private void warningDelete() {
        showConfirmCancelMessage("Delete season will delete all data related to season, continue?",
                (dialog, which) -> mModel.deleteTeams(), null);
    }

    private void showTeams(List<TeamListItem> teams) {
        if (adapter == null) {
            adapter = new TeamListAdapter();
            adapter.setList(teams);
            adapter.setCheckMap(mModel.getCheckMap());
            adapter.setOnItemClickListener((view, position, data) -> {
                if (isEditMode) {
                    editTeam(data);
                }
                else {
                    if (isSelectMode) {
                        Intent intent = new Intent();
                        intent.putExtra(RESP_TEAM_ID, data.getBean().getId());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else {
                        showTeamPage(data);
                    }
                }
            });
            mBinding.rvTeams.setAdapter(adapter);
        }
        else {
            adapter.setList(teams);
            adapter.notifyDataSetChanged();
        }
    }

    private void showProvinceTeams(List<StatProvinceItem> teams) {
        groupAdapter = new StatProvinceTeamAdapter(teams);
        groupAdapter.setOnTeamItemClickListener(item -> {
            if (isEditMode) {
                editTeam(item.getBean());
            }
            else {
                if (isSelectMode) {
                    Intent intent = new Intent();
                    intent.putExtra(RESP_TEAM_ID, item.getBean().getBean().getId());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else {
                    showTeamPage(item.getBean());
                }
            }
        });
        mBinding.rvTeams.setAdapter(groupAdapter);
    }

    private void showTeamPage(TeamListItem data) {
    }

    private void editTeam(TeamListItem team) {
        TeamEditor editor = new TeamEditor();
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setMaxHeight(ScreenUtils.getScreenHeight() * 4 / 5);
        dialogFragment.setContentFragment(editor);
        if (team == null) {
            dialogFragment.setTitle("New team");
        }
        else {
            editor.setTeam(team.getBean());
            dialogFragment.setTitle("Edit " + team.getBean().getCode());
        }
        dialogFragment.show(getSupportFragmentManager(), "TeamEditor");
    }
}
