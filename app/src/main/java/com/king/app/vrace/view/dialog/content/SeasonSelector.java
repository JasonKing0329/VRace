package com.king.app.vrace.view.dialog.content;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.king.app.vrace.R;
import com.king.app.vrace.base.IFragmentHolder;
import com.king.app.vrace.databinding.FragmentSelectorSeasonBinding;
import com.king.app.vrace.model.entity.Season;
import com.king.app.vrace.page.adapter.SeasonSelectorAdapter;
import com.king.app.vrace.utils.DebugLog;
import com.king.app.vrace.utils.ScreenUtils;
import com.king.app.vrace.view.dialog.DraggableContentFragment;
import com.king.app.vrace.viewmodel.SeasonListViewModel;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/22 9:15
 */
public class SeasonSelector extends DraggableContentFragment<FragmentSelectorSeasonBinding> {

    private SeasonListViewModel listViewModel;

    private SeasonSelectorAdapter adapter;

    private OnSeasonSelectedListener onSeasonSelectedListener;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_selector_season;
    }

    @Override
    protected void initView() {
        listViewModel = ViewModelProviders.of(getActivity()).get(SeasonListViewModel.class);

        mBinding.rvSeasons.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mBinding.rvSeasons.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = ScreenUtils.dp2px(8);
                outRect.left = ScreenUtils.dp2px(8);
                outRect.right = ScreenUtils.dp2px(8);
                outRect.bottom = ScreenUtils.dp2px(8);
            }
        });

        listViewModel.seasonsObserver.observe(this, seasons -> {
            DebugLog.e("");
            showSeasons(seasons);
        });
        listViewModel.loadSeasons();
    }

    public void setOnSeasonSelectedListener(OnSeasonSelectedListener onSeasonSelectedListener) {
        this.onSeasonSelectedListener = onSeasonSelectedListener;
    }

    private void showSeasons(List<Season> seasons) {
        if (adapter == null) {
            adapter = new SeasonSelectorAdapter();
            adapter.setList(seasons);
            adapter.setOnItemClickListener((view, position, data) -> {
                if (onSeasonSelectedListener != null) {
                    onSeasonSelectedListener.onSelectSeason(data);
                    dismissAllowingStateLoss();
                }
            });
            mBinding.rvSeasons.setAdapter(adapter);
        }
        else {
            adapter.setList(seasons);
            adapter.notifyDataSetChanged();
        }
    }

    public interface OnSeasonSelectedListener {
        void onSelectSeason(Season season);
    }

}
