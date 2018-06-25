package com.king.app.vrace.page.adapter;

import android.view.View;
import android.widget.TextView;

import com.king.app.vrace.GlideApp;
import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseBindingAdapter;
import com.king.app.vrace.conf.LegType;
import com.king.app.vrace.databinding.AdapterLegBinding;
import com.king.app.vrace.viewmodel.bean.LegItem;

import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:16
 */
public class LegAdapter extends BaseBindingAdapter<AdapterLegBinding, LegItem> {

    private Map<Long, Boolean> checkMap;

    private boolean isSelectMode;

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_leg;
    }

    @Override
    protected void onBindItem(AdapterLegBinding binding, int position, LegItem bean) {
        binding.setBean(bean);
        binding.cbCheck.setVisibility(isSelectMode ? View.VISIBLE:View.GONE);
        if (isSelectMode) {
            if (checkMap.get(bean.getBean().getId()) == null) {
                binding.cbCheck.setChecked(false);
            }
            else {
                binding.cbCheck.setChecked(checkMap.get(bean.getBean().getId()));
            }
        }
        setLegType(binding.tvType, bean.getType());

        GlideApp.with(binding.ivLeg.getContext())
                .load(bean.getImagePath())
                .error(R.drawable.ic_default_leg)
                .into(binding.ivLeg);
    }

    private void setLegType(TextView tvType, LegType type) {
        tvType.setVisibility(View.VISIBLE);
        switch (type) {
            case NEL:
                tvType.setText("Not Elimination Leg");
                break;
            case FINAL:
                tvType.setText("Final Leg");
                break;
            case DLL:
                tvType.setText("Double Length Leg");
                break;
            case DEL:
                tvType.setText("Double Elimination Leg");
                break;
            default:
                // start line and normal elimination leg
                tvType.setVisibility(View.GONE);
                break;
        }
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
