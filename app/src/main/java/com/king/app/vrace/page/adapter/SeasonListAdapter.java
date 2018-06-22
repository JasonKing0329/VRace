package com.king.app.vrace.page.adapter;

import android.view.View;

import com.king.app.vrace.GlideApp;
import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseBindingAdapter;
import com.king.app.vrace.conf.SeasonType;
import com.king.app.vrace.databinding.AdapterSeasonBinding;
import com.king.app.vrace.model.entity.Season;

import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:16
 */
public class SeasonListAdapter extends BaseBindingAdapter<AdapterSeasonBinding, Season> {

    private Map<Long, Boolean> checkMap;

    private boolean isSelectMode;

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_season;
    }

    @Override
    protected void onBindItem(AdapterSeasonBinding binding, int position, Season bean) {
        GlideApp.with(binding.ivHead.getContext())
                .load(bean.getCoverUrl())
                .error(getDefaultCover(bean.getIndex(), bean.getType()))
                .into(binding.ivHead);
        binding.tvSeason.setText("S" + bean.getIndex());
        binding.cbCheck.setVisibility(isSelectMode ? View.VISIBLE:View.GONE);
        if (isSelectMode) {
            if (checkMap.get(bean.getId()) == null) {
                binding.cbCheck.setChecked(false);
            }
            else {
                binding.cbCheck.setChecked(checkMap.get(bean.getId()));
            }
        }
    }

    @Override
    protected void onClickItem(View v, int position) {
        if (isSelectMode) {
            long id = list.get(position).getId();
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

    private int getDefaultCover(int index, int type) {
        if (type == SeasonType.ALL_STAR.ordinal()) {
            return R.drawable.ic_default_allstar;
        }
        else {
            if (index < 7) {
                return R.drawable.ic_default5;
            }
            else if (index < 14) {
                return R.drawable.ic_default4;
            }
            else if (index < 22) {
                return R.drawable.ic_default1;
            }
            else if (index < 28) {
                return R.drawable.ic_default3;
            }
            else {
                return R.drawable.ic_default2;
            }
        }
    }
}
