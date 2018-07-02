package com.king.app.vrace.page;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.king.app.jactionbar.OnConfirmListener;
import com.king.app.vrace.R;
import com.king.app.vrace.base.MvvmActivity;
import com.king.app.vrace.databinding.ActivitySeasonListBinding;
import com.king.app.vrace.model.entity.Season;
import com.king.app.vrace.page.adapter.SeasonListAdapter;
import com.king.app.vrace.utils.ScreenUtils;
import com.king.app.vrace.view.dialog.DraggableDialogFragment;
import com.king.app.vrace.view.dialog.content.SeasonEditor;
import com.king.app.vrace.viewmodel.SeasonListViewModel;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:31
 */
public class SeasonListActivity extends MvvmActivity<ActivitySeasonListBinding, SeasonListViewModel> {

    private SeasonListAdapter adapter;

    private boolean isEditMode;

    @Override
    protected int getContentView() {
        return R.layout.activity_season_list;
    }

    @Override
    protected void initView() {
        mBinding.rvSeasons.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvSeasons.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) > 0) {
                    outRect.top = ScreenUtils.dp2px(10);
                }
            }
        });

        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_add:
                    editSeason(null);
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
                case R.id.menu_teams:
                    goToTeamPage();
                    break;
                case R.id.menu_places:
                    goToPlacePage();
                    break;
                case R.id.menu_winners:
                    goToWinnerPage();
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

    private void goToPlacePage() {
        startActivity(new Intent(this, StatisticPlaceActivity.class));
    }

    private void goToTeamPage() {
        startActivity(new Intent(this, TeamListActivity.class));
    }

    private void goToWinnerPage() {
        startActivity(new Intent(this, StatisticWinnerActivity.class));
    }

    @Override
    protected SeasonListViewModel createViewModel() {
        return ViewModelProviders.of(this).get(SeasonListViewModel.class);
    }

    @Override
    protected void initData() {
        mBinding.setModel(mModel);
        mModel.seasonsObserver.observe(this, seasons -> showSeasons(seasons));
        mModel.deleteObserver.observe(this, deleted -> {
            mBinding.actionbar.cancelConfirmStatus();
            adapter.setSelectMode(false);
            adapter.notifyDataSetChanged();
        });

        mModel.loadSeasons();
    }

    private void warningDelete() {
        showConfirmCancelMessage("Delete season will delete all data related to season, continue?",
                (dialog, which) -> mModel.deleteSeasons(), null);
    }

    private void showSeasons(List<Season> seasons) {
        if (adapter == null) {
            adapter = new SeasonListAdapter();
            adapter.setList(seasons);
            adapter.setCheckMap(mModel.getCheckMap());
            adapter.setOnItemClickListener((view, position, data) -> {
                if (isEditMode) {
                    editSeason(data);
                }
                else {
                    showSeasonPage(data);
                }
            });
            mBinding.rvSeasons.setAdapter(adapter);
        }
        else {
            adapter.setList(seasons);
            adapter.notifyDataSetChanged();
        }
    }

    private void showSeasonPage(Season data) {
        Intent intent = new Intent(this, SeasonActivity.class);
        intent.putExtra(SeasonActivity.EXTRA_SEASON_ID, data.getId());
        startActivity(intent);
    }

    private void editSeason(Season season) {
        SeasonEditor editor = new SeasonEditor();
        editor.setSeason(season);
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(editor);
        if (season == null) {
            dialogFragment.setTitle("New season");
        }
        else {
            dialogFragment.setTitle("Edit S" + season.getIndex());
        }
        dialogFragment.show(getSupportFragmentManager(), "SeasonEditor");
    }
}
