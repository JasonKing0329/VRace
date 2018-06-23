package com.king.app.vrace.page.adapter;

import android.text.TextUtils;
import android.view.View;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseBindingAdapter;
import com.king.app.vrace.databinding.AdapterPlayerBinding;
import com.king.app.vrace.model.entity.Player;

import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:16
 */
public class PlayerListAdapter extends BaseBindingAdapter<AdapterPlayerBinding, Player> {

    private Map<Long, Boolean> checkMap;

    private boolean isSelectMode;

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_player;
    }

    @Override
    protected void onBindItem(AdapterPlayerBinding binding, int position, Player bean) {
        binding.setBean(bean);

        binding.tvGender.setText(bean.getGender() == 0 ? "男":"女");
        binding.tvSeason.setText("S" + bean.getDebutSeason().getIndex());
        binding.tvPlace.setText(bean.getProvince() + "/" + bean.getCity());
        binding.tvOccupy.setVisibility(TextUtils.isEmpty(bean.getOccupy()) ? View.GONE:View.VISIBLE);

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

}
