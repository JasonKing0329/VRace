package com.king.app.vrace.page.adapter;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseBindingAdapter;
import com.king.app.vrace.databinding.AdapterTeamResultBinding;
import com.king.app.vrace.viewmodel.bean.TeamSeasonItem;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:16
 */
public class TeamResultAdapter extends BaseBindingAdapter<AdapterTeamResultBinding, TeamSeasonItem> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_team_result;
    }

    @Override
    protected void onBindItem(AdapterTeamResultBinding binding, int position, TeamSeasonItem bean) {
        binding.setBean(bean);

    }
}
