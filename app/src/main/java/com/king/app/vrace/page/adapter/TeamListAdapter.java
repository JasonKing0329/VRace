package com.king.app.vrace.page.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseBindingAdapter;
import com.king.app.vrace.databinding.AdapterTeamBinding;
import com.king.app.vrace.model.entity.Team;
import com.king.app.vrace.viewmodel.bean.TeamListItem;

import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:16
 */
public class TeamListAdapter extends BaseBindingAdapter<AdapterTeamBinding, TeamListItem> {

    private Map<Long, Boolean> checkMap;

    private boolean isSelectMode;

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_team;
    }

    @Override
    protected void onBindItem(AdapterTeamBinding binding, int position, TeamListItem bean) {
        binding.setBean(bean);
        updateNameBg(binding.tvName, bean.getBean());
        binding.cbCheck.setVisibility(isSelectMode ? View.VISIBLE:View.GONE);
        if (isSelectMode) {
            if (checkMap.get(bean.getBean().getId()) == null) {
                binding.cbCheck.setChecked(false);
            }
            else {
                binding.cbCheck.setChecked(checkMap.get(bean.getBean().getId()));
            }
        }
    }

    private void updateNameBg(TextView tvName, Team bean) {
        GradientDrawable drawable = (GradientDrawable) tvName.getBackground();
        drawable.setColor(tvName.getResources().getColor(R.color.colorAccent));
        tvName.setBackground(drawable);
    }

    @Override
    protected void onClickItem(View v, int position) {
        if (isSelectMode) {
            long id = list.get(position).getBean().getId();
            if (checkMap.get(id) == null) {
                checkMap.put(id, true);
            }
            else {
                checkMap.remove(id);
            }
            notifyItemChanged(position);
        }
        else {
            super.onClickItem(v, position);
        }
    }

    public void setCheckMap(Map<Long, Boolean> checkMap) {
        this.checkMap = checkMap;
    }

    public void setSelectMode(boolean selectMode) {
        isSelectMode = selectMode;
    }

}
