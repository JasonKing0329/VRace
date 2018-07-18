package com.king.app.vrace.page;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;

import com.king.app.jactionbar.OnConfirmListener;
import com.king.app.vrace.R;
import com.king.app.vrace.base.MvvmActivity;
import com.king.app.vrace.conf.AppConstants;
import com.king.app.vrace.databinding.ActivityPlayerListBinding;
import com.king.app.vrace.model.entity.Player;
import com.king.app.vrace.model.setting.SettingProperty;
import com.king.app.vrace.page.adapter.PlayerListAdapter;
import com.king.app.vrace.utils.ScreenUtils;
import com.king.app.vrace.view.dialog.DraggableDialogFragment;
import com.king.app.vrace.view.dialog.content.PlayerEditor;
import com.king.app.vrace.view.widget.FitSideBar;
import com.king.app.vrace.viewmodel.PlayerListViewModel;

import java.util.List;
import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:31
 */
public class PlayerListActivity extends MvvmActivity<ActivityPlayerListBinding, PlayerListViewModel> {

    public static final String RESP_PLAYER_ID = "player_id";

    private PlayerListAdapter adapter;

    private boolean isEditMode;

    @Override
    protected int getContentView() {
        return R.layout.activity_player_list;
    }

    @Override
    protected void initView() {
        mBinding.rvPlayers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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
                    editPlayer(null);
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
                case R.id.menu_download:
                    mModel.downloadPlayers();
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

        mBinding.sidebar.setOnSidebarStatusListener(new FitSideBar.OnSidebarStatusListener() {
            @Override
            public void onChangeFinished() {

            }

            @Override
            public void onSideIndexChanged(String index) {
                int position = mModel.getIndexPosition(index);
                mBinding.rvPlayers.scrollToPosition(position);
            }
        });
    }

    @Override
    protected PlayerListViewModel createViewModel() {
        return ViewModelProviders.of(this).get(PlayerListViewModel.class);
    }

    @Override
    protected void initData() {
        mModel.playersObserver.observe(this, players -> showPlayers(players));
        mModel.deleteObserver.observe(this, deleted -> {
            mBinding.actionbar.cancelConfirmStatus();
            adapter.setSelectMode(false);
            adapter.notifyDataSetChanged();
            mModel.loadPlayers();
        });
        mModel.indexObserver.observe(this, list -> {
            mBinding.sidebar.clear();
            for (String index:list) {
                mBinding.sidebar.addIndex(index);
            }
            mBinding.sidebar.build();
        });

        mModel.loadPlayers();
    }

    private void warningDelete() {
        showConfirmCancelMessage("Delete player will delete all data related to player, continue?",
                (dialog, which) -> mModel.deletePlayers(), null);
    }

    private void showPlayers(List<Player> teams) {
        if (adapter == null) {
            adapter = new PlayerListAdapter();
            adapter.setList(teams);
            adapter.setCheckMap(mModel.getCheckMap());
            adapter.setOnItemClickListener((view, position, data) -> {
                if (isEditMode) {
                    editPlayer(data);
                }
                else {
                    onSelectPlayer(data);
                }
            });
            mBinding.rvPlayers.setAdapter(adapter);
        }
        else {
            adapter.setList(teams);
            adapter.notifyDataSetChanged();
        }
    }

    private void onSelectPlayer(Player data) {
        Intent intent = new Intent();
        intent.putExtra(RESP_PLAYER_ID, data.getId());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void editPlayer(Player player) {
        PlayerEditor editor = new PlayerEditor();
        editor.setPlayer(player);
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setMaxHeight(ScreenUtils.getScreenHeight() * 4 / 5);
        dialogFragment.setContentFragment(editor);
        if (player == null) {
            dialogFragment.setTitle("New player");
        }
        else {
            dialogFragment.setTitle("Edit " + player.getName());
        }
        dialogFragment.show(getSupportFragmentManager(), "TeamEditor");
    }
}
