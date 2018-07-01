package com.king.app.vrace.page;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;

import com.king.app.jactionbar.OnConfirmListener;
import com.king.app.vrace.R;
import com.king.app.vrace.base.MvvmActivity;
import com.king.app.vrace.databinding.ActivitySeasonBinding;
import com.king.app.vrace.page.adapter.LegAdapter;
import com.king.app.vrace.utils.ScreenUtils;
import com.king.app.vrace.view.dialog.DraggableDialogFragment;
import com.king.app.vrace.view.dialog.content.LegEditor;
import com.king.app.vrace.view.dialog.content.SeasonTeamResultsDialog;
import com.king.app.vrace.viewmodel.SeasonViewModel;
import com.king.app.vrace.viewmodel.bean.LegItem;

import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/6/24 0024 15:56
 */

public class SeasonActivity extends MvvmActivity<ActivitySeasonBinding, SeasonViewModel> {

    public static final String EXTRA_SEASON_ID = "season_id";

    private boolean isEditMode;

    private LegAdapter legAdapter;
    /**
     * 不设置statusbar背景，因为该页面运用了系统statusbar浮于head图片之上的效果
     * @return
     */
    @Override
    protected boolean updateStatusBarColor() {
        return false;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_season;
    }

    @Override
    protected void initView() {
        mBinding.rvLegs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.ivTeams.setOnClickListener(view -> manageTeams());

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_add:
                    editLeg(null);
                    break;
                case R.id.menu_delete:
                    if (legAdapter != null) {
                        mBinding.actionbar.showConfirmStatus(menuId);
                        legAdapter.setSelectMode(true);
                        legAdapter.notifyDataSetChanged();
                    }
                    break;
                case R.id.menu_edit:
                    mBinding.actionbar.showConfirmStatus(menuId);
                    isEditMode = true;
                    break;
                case R.id.menu_results:
                    showSeasonResultsDialog();
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
                        legAdapter.setSelectMode(false);
                        legAdapter.notifyDataSetChanged();
                        break;
                }
                isEditMode = false;
                return true;
            }
        });
    }

    private void showSeasonResultsDialog() {
        SeasonTeamResultsDialog content = new SeasonTeamResultsDialog();
        content.setSeasonId(mModel.getSeason().getId());
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setTitle("Results");
        dialogFragment.setContentFragment(content);
        dialogFragment.show(getSupportFragmentManager(), "SeasonTeamResultsDialog");
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

        mModel.seasonObserver.observe(this, season -> mBinding.actionbar.setTitle("Season " + season.getIndex()));
        mModel.legsObserver.observe(this, legItems -> showLegs(legItems));
        mModel.deleteLegsObserver.observe(this, deleted -> {
            mBinding.actionbar.cancelConfirmStatus();
            legAdapter.setSelectMode(false);
            legAdapter.notifyDataSetChanged();
            mModel.loadLegs(mModel.getSeason().getId());
        });
        mModel.loadLegs(getIntent().getLongExtra(EXTRA_SEASON_ID, -1));
    }

    private void warningDelete() {
        showConfirmCancelMessage("Delete leg will delete all data related to leg, continue?",
                (dialog, which) -> mModel.deleteLegs(), null);
    }

    private void showLegs(List<LegItem> legItems) {
        if (legAdapter == null) {
            legAdapter = new LegAdapter();
            legAdapter.setList(legItems);
            legAdapter.setCheckMap(mModel.getLegCheckMap());
            legAdapter.setOnItemClickListener((view, position, data) -> {
                if (isEditMode) {
                    editLeg(data);
                }
                else {
                    onSelectLeg(data);
                }
            });
            mBinding.rvLegs.setAdapter(legAdapter);
        }
        else {
            legAdapter.setList(legItems);
            legAdapter.notifyDataSetChanged();
        }
    }

    private void onSelectLeg(LegItem data) {
        Intent intent = new Intent(this, LegActivity.class);
        intent.putExtra(LegActivity.EXTRA_LEG_ID, data.getBean().getId());
        startActivity(intent);
    }

    private void editLeg(LegItem data) {
        LegEditor editor = new LegEditor();
        editor.setSeasonId(mModel.getSeason().getId());
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setMaxHeight(ScreenUtils.getScreenHeight() * 4 / 5);
        dialogFragment.setContentFragment(editor);
        if (data == null) {
            dialogFragment.setTitle("New Leg");
        }
        else {
            editor.setLeg(data.getBean());
            dialogFragment.setTitle("Edit Leg" + data.getIndex());
        }
        dialogFragment.show(getSupportFragmentManager(), "LegEditor");
    }
}
