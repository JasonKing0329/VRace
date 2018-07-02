package com.king.app.vrace.page;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.king.app.vrace.R;
import com.king.app.vrace.base.MvvmActivity;
import com.king.app.vrace.databinding.ActivityStatisticWinnerBinding;
import com.king.app.vrace.page.adapter.WinnerSeasonAdapter;
import com.king.app.vrace.utils.ScreenUtils;
import com.king.app.vrace.viewmodel.StatisticWinnerModel;

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
    }

    @Override
    protected StatisticWinnerModel createViewModel() {
        return ViewModelProviders.of(this).get(StatisticWinnerModel.class);
    }

    @Override
    protected void initData() {
        mModel.dataObserver.observe(this, list -> showData(list));

        mModel.loadData();
    }

    private void showData(List<Object> list) {
        if (adapter == null) {
            adapter = new WinnerSeasonAdapter();
            adapter.setList(list);
            mBinding.rvPlaces.setAdapter(adapter);
        }
        else {
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
    }

}
