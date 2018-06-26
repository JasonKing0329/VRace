package com.king.app.vrace.page.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseBindingAdapter;
import com.king.app.vrace.conf.AppConstants;
import com.king.app.vrace.conf.GenderType;
import com.king.app.vrace.databinding.AdapterLegTeamBinding;
import com.king.app.vrace.model.entity.Team;
import com.king.app.vrace.utils.ColorUtil;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:16
 */
public class LegTeamAdapter extends BaseBindingAdapter<AdapterLegTeamBinding, Team> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_leg_team;
    }

    @Override
    protected void onBindItem(AdapterLegTeamBinding binding, int position, Team bean) {
        binding.tvName.setText(bean.getCode());
        binding.tvGender.setText(AppConstants.getGenderText(GenderType.values()[bean.getGenderType()]));
        if (bean.getSpecialColor() == 0) {
            binding.tvName.setTextColor(binding.tvName.getResources().getColor(R.color.colorAccent));
            binding.tvGender.setTextColor(binding.tvName.getResources().getColor(R.color.white));
        }
        else {
            binding.tvName.setTextColor(ColorUtil.generateForgroundColorForBg(bean.getSpecialColor()));
            binding.tvGender.setTextColor(ColorUtil.generateForgroundColorForBg(bean.getSpecialColor()));
        }
        updateNameBg(binding.groupTeam, bean);
    }

    private void updateNameBg(View view, Team bean) {
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        if (bean.getSpecialColor() != 0) {
            drawable.setColor(bean.getSpecialColor());
        }
        else {
            drawable.setColor(view.getResources().getColor(R.color.colorAccent));
        }
        view.setBackground(drawable);
    }

}
