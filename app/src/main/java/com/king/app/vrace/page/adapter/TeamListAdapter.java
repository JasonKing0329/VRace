package com.king.app.vrace.page.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseBindingAdapter;
import com.king.app.vrace.databinding.AdapterTeamBinding;
import com.king.app.vrace.model.entity.PersonTag;
import com.king.app.vrace.model.entity.Team;
import com.king.app.vrace.utils.ColorUtil;
import com.king.app.vrace.utils.FormatUtil;
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

        binding.tvSeq.setText(String.valueOf(position + 1));

        if (bean.getResult() == null) {
            binding.tvPoint.setVisibility(View.GONE);
            binding.tvChampion.setVisibility(View.GONE);
            binding.tvLegs.setVisibility(View.GONE);
            binding.tvLegs.setTextColor(binding.tvLegs.getResources().getColor(R.color.text_sub));
        }
        else {
            binding.tvLegs.setVisibility(View.VISIBLE);
            binding.tvLegs.setText(bean.getResult().getEndRank());
            // winner
            if (bean.getResult().getEndPosition() == 1) {
                binding.tvLegs.setTextColor(binding.tvLegs.getResources().getColor(R.color.redC93437));
            }
            else if (bean.getResult().getEndPosition() == 2) {
                binding.tvLegs.setTextColor(binding.tvLegs.getResources().getColor(R.color.orangeE78B39));
            }
            else if (bean.getResult().getEndPosition() == 3) {
                binding.tvLegs.setTextColor(binding.tvLegs.getResources().getColor(R.color.orangeE78B39));
            }
            else {
                binding.tvLegs.setTextColor(binding.tvLegs.getResources().getColor(R.color.text_sub));
            }
            if (bean.getResult().getChampions() > 0) {
                binding.tvChampion.setText("赛冠(" + bean.getResult().getChampions() + ")");
                binding.tvChampion.setVisibility(View.VISIBLE);
            }
            else {
                binding.tvChampion.setVisibility(View.GONE);
            }
            if (bean.getResult().getPoint() > 0) {
                binding.tvPoint.setText(FormatUtil.pointZZ(bean.getResult().getPoint()));
                binding.tvPoint.setVisibility(View.VISIBLE);
            }
            else {
                binding.tvPoint.setVisibility(View.GONE);
            }
        }

        binding.cbCheck.setVisibility(isSelectMode ? View.VISIBLE:View.GONE);
        if (isSelectMode) {
            if (checkMap.get(bean.getBean().getId()) == null) {
                binding.cbCheck.setChecked(false);
            }
            else {
                binding.cbCheck.setChecked(checkMap.get(bean.getBean().getId()));
            }
        }

        binding.flowTags.removeAllViews();
        if (bean.getBean().getTagList().size() > 0) {
            showTags(binding, bean.getBean());
            binding.flowTags.setVisibility(View.VISIBLE);
        }
        else {
            binding.flowTags.setVisibility(View.GONE);
        }
    }

    private void showTags(AdapterTeamBinding binding, Team bean) {
        SimpleTagAdapter<PersonTag> adapter = new SimpleTagAdapter<PersonTag>() {
            @Override
            protected String getText(PersonTag data) {
                return data.getTag();
            }

            @Override
            protected long getId(PersonTag data) {
                return data.getId();
            }

            @Override
            protected boolean isDisabled(PersonTag item) {
                return false;
            }
        };
        adapter.setData(bean.getTagList());
        int color;
        if (bean.getSpecialColor() != 0) {
            color = bean.getSpecialColor();
        }
        else {
            color = binding.flowTags.getResources().getColor(R.color.colorAccent);
        }
        adapter.setTagColor(color);
        adapter.setTextColor(ColorUtil.generateForgroundColorForBg(color));
        adapter.bindFlowLayout(binding.flowTags);
    }

    private void updateNameBg(TextView tvName, Team bean) {
        GradientDrawable drawable = (GradientDrawable) tvName.getBackground();
        if (bean.getSpecialColor() != 0) {
            drawable.setColor(bean.getSpecialColor());
            tvName.setTextColor(ColorUtil.generateForgroundColorForBg(bean.getSpecialColor()));
        }
        else {
            drawable.setColor(tvName.getResources().getColor(R.color.colorAccent));
            tvName.setTextColor(tvName.getResources().getColor(R.color.white));
        }
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
