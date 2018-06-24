package com.king.app.vrace.page;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;

import com.king.app.jactionbar.OnConfirmListener;
import com.king.app.vrace.R;
import com.king.app.vrace.base.MvvmActivity;
import com.king.app.vrace.base.RaceApplication;
import com.king.app.vrace.databinding.ActivityRelationshipBinding;
import com.king.app.vrace.model.entity.Relationship;
import com.king.app.vrace.page.adapter.RelationListAdapter;
import com.king.app.vrace.view.dialog.SimpleDialogs;
import com.king.app.vrace.viewmodel.RelationshipListViewModel;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:31
 */
public class RelationshipListActivity extends MvvmActivity<ActivityRelationshipBinding, RelationshipListViewModel> {

    public static final String RESP_RELATIONSHIP_ID = "relationship_Id";

    private RelationListAdapter adapter;

    private boolean isEditMode;

    @Override
    protected int getContentView() {
        return R.layout.activity_relationship;
    }

    @Override
    protected void initView() {
        mBinding.rvRelations.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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
                    editRelationship(null, null);
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
    protected RelationshipListViewModel createViewModel() {
        return ViewModelProviders.of(this).get(RelationshipListViewModel.class);
    }

    @Override
    protected void initData() {
        mModel.relationsObserver.observe(this, relationships -> showRelationships(relationships));
        mModel.deleteObserver.observe(this, deleted -> {
            mBinding.actionbar.cancelConfirmStatus();
            adapter.setSelectMode(false);
            adapter.notifyDataSetChanged();
            mModel.loadRelationships();
        });

        mModel.loadRelationships();
    }

    private void warningDelete() {
        showConfirmCancelMessage("Delete relationship will delete all data related to relationship, continue?",
                (dialog, which) -> mModel.deleteRelationships(), null);
    }

    private void showRelationships(List<Relationship> relationships) {
        if (adapter == null) {
            adapter = new RelationListAdapter();
            adapter.setList(relationships);
            adapter.setCheckMap(mModel.getCheckMap());
            adapter.setOnItemClickListener((view, position, data) -> {
                if (isEditMode) {
                    editRelationship(data, null);
                }
                else {
                    onSelectRelationship(data);
                }
            });
            adapter.setOnAddSubRelationListener(parent -> editRelationship(null, parent));
            mBinding.rvRelations.setAdapter(adapter);
        }
        else {
            adapter.setList(relationships);
            adapter.notifyDataSetChanged();
        }
    }

    private void onSelectRelationship(Relationship data) {
        Intent intent = new Intent();
        intent.putExtra(RESP_RELATIONSHIP_ID, data.getId());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void editRelationship(Relationship relationship, Relationship parent) {

        SimpleDialogs simpleDialogs = new SimpleDialogs();
        simpleDialogs.openInputDialog(this, "Relationship name", relationship == null ? null:relationship.getName(), name -> {
            Relationship mRelation;
            if (relationship == null) {
                mRelation = new Relationship();
            }
            else {
                mRelation = relationship;
            }
            mRelation.setName(name);
            if (parent != null) {
                mRelation.setParentId(parent.getId());
            }
            RaceApplication.getInstance().getDaoSession().getRelationshipDao().insertOrReplace(mRelation);
            mModel.loadRelationships();
        });
    }
}
