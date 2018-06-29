package com.king.app.vrace.page;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;

import com.king.app.vrace.R;
import com.king.app.vrace.base.MvvmActivity;
import com.king.app.vrace.base.RaceApplication;
import com.king.app.vrace.databinding.ActivityTagListBinding;
import com.king.app.vrace.model.ParcelableTags;
import com.king.app.vrace.model.entity.PersonTag;
import com.king.app.vrace.page.adapter.TagListAdapter;
import com.king.app.vrace.view.dialog.SimpleDialogs;
import com.king.app.vrace.viewmodel.TagListViewModel;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:31
 */
public class TagListActivity extends MvvmActivity<ActivityTagListBinding, TagListViewModel> {

    public static final String RESP_TAG_IDS = "tag_ids";

    public static final String REQUEST_TEAM_ID = "team_id";

    private TagListAdapter adapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_tag_list;
    }

    @Override
    protected void initView() {
        mBinding.rvTags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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
                    editTag(null);
                    break;
                case R.id.menu_delete:
                    warningDelete();
                    break;
                case R.id.menu_select_done:
                    if (adapter != null) {
                        onSelectDone();
                    }
                    break;
            }
        });
    }

    @Override
    protected TagListViewModel createViewModel() {
        return ViewModelProviders.of(this).get(TagListViewModel.class);
    }

    @Override
    protected void initData() {
        mModel.playersObserver.observe(this, tags -> showTags(tags));
        mModel.deleteObserver.observe(this, deleted -> {
            mBinding.actionbar.cancelConfirmStatus();
            adapter.setSelectMode(false);
            adapter.notifyDataSetChanged();
            mModel.loadTags(getTeamIdFromIntent());
        });

        mModel.loadTags(getTeamIdFromIntent());
    }

    private long getTeamIdFromIntent() {
        return getIntent().getLongExtra(REQUEST_TEAM_ID, -1);
    }

    private void warningDelete() {
        showConfirmCancelMessage("Delete tag will delete all data related to tag, continue?",
                (dialog, which) -> mModel.deleteTags(), null);
    }

    private void showTags(List<PersonTag> teams) {
        if (adapter == null) {
            adapter = new TagListAdapter();
            adapter.setList(teams);
            adapter.setSelectMode(true);
            adapter.setCheckMap(mModel.getCheckMap());
            adapter.setOnEditItemListener(tag -> editTag(tag));
            mBinding.rvTags.setAdapter(adapter);
        }
        else {
            adapter.setList(teams);
            adapter.notifyDataSetChanged();
        }
    }

    private void onSelectDone() {
        ParcelableTags tags = mModel.getSelectedTags();
        Intent intent = new Intent();
        intent.putExtra(RESP_TAG_IDS, tags);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void editTag(PersonTag tag) {
        String editText = "";
        if (tag != null) {
            editText = tag.getTag();
        }
        new SimpleDialogs().openInputDialog(this, "Edit tag", editText, name -> {
            PersonTag personTag = tag;
            if (personTag == null) {
                personTag = new PersonTag();
            }
            personTag.setTag(name);
            RaceApplication.getInstance().getDaoSession().getPersonTagDao().insertOrReplace(personTag);
            mModel.loadTags(getTeamIdFromIntent());
        });
    }
}
