package com.king.app.vrace.page.adapter;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseBindingAdapter;
import com.king.app.vrace.databinding.AdapterSelectorSeasonBinding;
import com.king.app.vrace.model.entity.Season;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:16
 */
public class SeasonSelectorAdapter extends BaseBindingAdapter<AdapterSelectorSeasonBinding, Season> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_selector_season;
    }

    @Override
    protected void onBindItem(AdapterSelectorSeasonBinding binding, int position, Season bean) {
        binding.tvSeason.setText("S" + bean.getIndex());
    }

}
