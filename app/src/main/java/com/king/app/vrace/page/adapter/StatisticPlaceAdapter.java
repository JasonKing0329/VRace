package com.king.app.vrace.page.adapter;

import com.king.app.vrace.GlideApp;
import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseBindingAdapter;
import com.king.app.vrace.databinding.AdapterStatisticPlaceBinding;
import com.king.app.vrace.viewmodel.bean.StatisticPlaceItem;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:16
 */
public class StatisticPlaceAdapter extends BaseBindingAdapter<AdapterStatisticPlaceBinding, StatisticPlaceItem> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_statistic_place;
    }

    @Override
    protected void onBindItem(AdapterStatisticPlaceBinding binding, int position, StatisticPlaceItem bean) {
        binding.setBean(bean);

        binding.tvCountry.setText(bean.getPlace() + "(" + bean.getCount() + ")");

        GlideApp.with(binding.ivFlag.getContext())
                .load(bean.getImgPath())
                .error(R.drawable.ic_default_leg)
                .into(binding.ivFlag);
    }

}
