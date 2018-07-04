package com.king.app.vrace.page;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.king.app.vrace.GlideApp;
import com.king.app.vrace.R;
import com.king.app.vrace.base.MvvmActivity;
import com.king.app.vrace.databinding.ActivityLegBinding;
import com.king.app.vrace.model.ImageProvider;
import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.LegTeam;
import com.king.app.vrace.model.entity.Team;
import com.king.app.vrace.page.adapter.LegTeamAdapter;
import com.king.app.vrace.page.adapter.leg.LegComplexAdapter;
import com.king.app.vrace.page.adapter.leg.RankAdapter;
import com.king.app.vrace.utils.ScreenUtils;
import com.king.app.vrace.view.dialog.SimpleDialogs;
import com.king.app.vrace.viewmodel.LegViewModel;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/25 11:18
 */
public class LegActivity extends MvvmActivity<ActivityLegBinding, LegViewModel> {

    public static final String EXTRA_LEG_ID = "leg_id";

    private LegTeamAdapter teamAdapter;

    private LegComplexAdapter pageAdapter;

    @Override
    protected boolean updateStatusBarColor() {
        return false;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_leg;
    }

    @Override
    protected void initView() {
        mBinding.setModel(mModel);
        mBinding.executePendingBindings();

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.rvRank.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvRank.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position > 0) {
                    outRect.top = ScreenUtils.dp2px(10);
                }
                if (mModel.isLastItem(position)) {
                    // rvTeam的最大高度
                    outRect.bottom = ScreenUtils.dp2px(120);
                }
            }
        });
    }

    @Override
    protected LegViewModel createViewModel() {
        return ViewModelProviders.of(this).get(LegViewModel.class);
    }

    @Override
    protected void initData() {
        mModel.legObserver.observe(this, leg -> showLegInfo(leg));
        mModel.teamsObserver.observe(this, teams -> showTeams(teams));
        mModel.ranksObserver.observe(this, teams -> showRanks(teams));

        mModel.loadLegs(getIntent().getLongExtra(EXTRA_LEG_ID, -1));
    }

    private void showLegInfo(Leg leg) {
        mBinding.actionbar.setTitle("S" + leg.getSeason().getIndex() + " Leg " + leg.getIndex());
        GlideApp.with(this)
                .load(ImageProvider.getLegImagePath(leg))
                .error(R.drawable.ic_default_leg)
                .into(mBinding.ivLeg);
    }

    private void showTeams(List<Team> teams) {
        if (teamAdapter == null) {
            int span;
            switch (teams.size()) {
                case 7:
                case 8:
                    span = 4;
                    break;
                case 9:
                case 10:
                    span = 5;
                    break;
                default:
                    span = 6;
                    break;
            }
            mBinding.rvTeams.setLayoutManager(new GridLayoutManager(this, span));

            teamAdapter = new LegTeamAdapter();
            teamAdapter.setList(teams);
            teamAdapter.setOnItemClickListener((view, position, data) -> {
                mModel.updateTeam(pageAdapter.getSelectedRank(), data);
                pageAdapter.notifySelectionChanged();
            });
            mBinding.rvTeams.setAdapter(teamAdapter);
        }
        else {
            teamAdapter.setList(teams);
            teamAdapter.notifyDataSetChanged();
        }
    }

    private void showRanks(List<Object> items) {
        if (pageAdapter == null) {
            pageAdapter = new LegComplexAdapter();
            pageAdapter.setList(items);
            pageAdapter.setOnEditRankItemListener(new RankAdapter.OnEditRankItemListener() {
                @Override
                public void onEditRankItem(LegTeam legTeam, int position) {
                    editRankDesc(legTeam, position);
                }

                @Override
                public void onLongClickItem(LegTeam bean, int position) {
                    showConfirmCancelMessage("Delete this item?"
                            , (dialog, which) -> mModel.deleteItem(bean)
                            , null);
                }
            });
            pageAdapter.setOnEditLegDescListener((leg, position) -> editLegDesc(leg, position));
            mBinding.rvRank.setAdapter(pageAdapter);
        }
        else {
            pageAdapter.setList(items);
            pageAdapter.notifyDataSetChanged();
        }
    }

    private void editLegDesc(Leg leg, int position) {
        SimpleDialogs dialogs = new SimpleDialogs();
        dialogs.openInputDialog(this, "Description", leg.getDescription(), text -> {
            leg.setDescription(text);
            leg.update();
            pageAdapter.notifyItemChanged(position);
        });
    }

    private void editRankDesc(LegTeam legTeam, int position) {
        SimpleDialogs dialogs = new SimpleDialogs();
        dialogs.openInputDialog(this, "Description", legTeam.getDescription(), text -> {
            legTeam.setDescription(text);
            legTeam.update();
            pageAdapter.notifyItemChanged(position);
        });
    }
}
