package com.king.app.vrace.page;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;

import com.king.app.jactionbar.OnConfirmListener;
import com.king.app.vrace.R;
import com.king.app.vrace.base.MvvmActivity;
import com.king.app.vrace.base.RaceApplication;
import com.king.app.vrace.databinding.ActivityElimReasonsBinding;
import com.king.app.vrace.model.entity.EliminationReason;
import com.king.app.vrace.page.adapter.EliminationReasonListAdapter;
import com.king.app.vrace.view.dialog.SimpleDialogs;
import com.king.app.vrace.viewmodel.EliminationReasonViewModel;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:31
 */
public class EliminationReasonsActivity extends MvvmActivity<ActivityElimReasonsBinding, EliminationReasonViewModel> {

    public static final String RESP_REASON_ID = "reason_id";

    private EliminationReasonListAdapter adapter;

    private boolean isEditMode;

    @Override
    protected int getContentView() {
        return R.layout.activity_elim_reasons;
    }

    @Override
    protected void initView() {
        mBinding.rvReasons.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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
                    editReason(null, null);
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
    protected EliminationReasonViewModel createViewModel() {
        return ViewModelProviders.of(this).get(EliminationReasonViewModel.class);
    }

    @Override
    protected void initData() {
        mModel.reasonsObserver.observe(this, reasons -> showReasons(reasons));
        mModel.deleteObserver.observe(this, deleted -> {
            mBinding.actionbar.cancelConfirmStatus();
            adapter.setSelectMode(false);
            adapter.notifyDataSetChanged();
            mModel.loadReasons();
        });

        mModel.loadReasons();
    }

    private void warningDelete() {
        showConfirmCancelMessage("Delete relationship will delete all data related to relationship, continue?",
                (dialog, which) -> mModel.deleteReasons(), null);
    }

    private void showReasons(List<EliminationReason> reasons) {
        if (adapter == null) {
            adapter = new EliminationReasonListAdapter();
            adapter.setList(reasons);
            adapter.setCheckMap(mModel.getCheckMap());
            adapter.setOnItemClickListener((view, position, data) -> {
                if (isEditMode) {
                    editReason(data, null);
                }
                else {
                    onSelectRelationship(data);
                }
            });
            adapter.setOnAddSubReasonListener(parent -> editReason(null, parent));
            mBinding.rvReasons.setAdapter(adapter);
        }
        else {
            adapter.setList(reasons);
            adapter.notifyDataSetChanged();
        }
    }

    private void onSelectRelationship(EliminationReason data) {
        Intent intent = new Intent();
        intent.putExtra(RESP_REASON_ID, data.getId());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void editReason(EliminationReason reason, EliminationReason parent) {

        SimpleDialogs simpleDialogs = new SimpleDialogs();
        simpleDialogs.openInputDialog(this, "Reason name", reason == null ? null:reason.getName(), name -> {
            EliminationReason mReason;
            if (reason == null) {
                mReason = new EliminationReason();
            }
            else {
                mReason = reason;
            }
            mReason.setName(name);
            if (parent != null) {
                mReason.setParentId(parent.getId());
            }
            RaceApplication.getInstance().getDaoSession().getEliminationReasonDao().insertOrReplace(mReason);
            mModel.loadReasons();
        });
    }
}
