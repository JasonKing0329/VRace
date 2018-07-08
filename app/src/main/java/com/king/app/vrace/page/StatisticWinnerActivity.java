package com.king.app.vrace.page;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.king.app.vrace.R;
import com.king.app.vrace.base.MvvmActivity;
import com.king.app.vrace.databinding.ActivityStatisticWinnerBinding;
import com.king.app.vrace.model.entity.Season;
import com.king.app.vrace.page.adapter.WinnerSeasonAdapter;
import com.king.app.vrace.utils.ScreenUtils;
import com.king.app.vrace.view.dialog.AlertDialogFragment;
import com.king.app.vrace.viewmodel.StatisticWinnerModel;
import com.king.app.vrace.viewmodel.bean.StatisticWinnerItem;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:31
 */
public class StatisticWinnerActivity extends MvvmActivity<ActivityStatisticWinnerBinding, StatisticWinnerModel> {

    private WinnerSeasonAdapter adapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_statistic_winner;
    }

    @Override
    protected void initView() {
        mBinding.rvPlaces.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvPlaces.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = ScreenUtils.dp2px(10);
            }
        });

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_other_rank:
                    if (mBinding.llRank.getVisibility() == View.VISIBLE) {
                        mBinding.llRank.setVisibility(View.GONE);
                    }
                    else {
                        mBinding.llRank.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        });

        mBinding.llRank.setVisibility(View.GONE);
        mBinding.ivLast.setVisibility(View.INVISIBLE);
    }

    @Override
    protected StatisticWinnerModel createViewModel() {
        return ViewModelProviders.of(this).get(StatisticWinnerModel.class);
    }

    @Override
    protected void initData() {
        mBinding.setModel(mModel);
        mModel.dataObserver.observe(this, list -> showData(list));

        mModel.loadData();
    }

    private void showData(List<Object> list) {
        if (adapter == null) {
            adapter = new WinnerSeasonAdapter();
            adapter.setList(list);
            adapter.setOnItemClickListener((position, item) -> popupItem(item));
            mBinding.rvPlaces.setAdapter(adapter);
        }
        else {
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
    }

    private void popupItem(StatisticWinnerItem item) {
        String[] textList = mModel.convertToTextList(item.getSeasonList());
        new AlertDialogFragment()
                .setTitle(null)
                .setItems(textList, (dialog, which) -> goToSeason(item.getSeasonList().get(which)))
                .show(getSupportFragmentManager(), "AlertDialogFragment");
    }

    private void goToSeason(Season season) {
        Intent intent = new Intent();
        intent.setClass(this, SeasonActivity.class);
        intent.putExtra(SeasonActivity.EXTRA_SEASON_ID, season.getId());
        startActivity(intent);
    }

}
