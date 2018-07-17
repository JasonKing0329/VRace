package com.king.app.vrace.page.adapter;

import android.view.View;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseBindingAdapter;
import com.king.app.vrace.databinding.AdapterElimReasonsBinding;
import com.king.app.vrace.model.entity.EliminationReason;
import com.king.app.vrace.utils.ScreenUtils;

import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:16
 */
public class EliminationReasonListAdapter extends BaseBindingAdapter<AdapterElimReasonsBinding, EliminationReason> {

    private Map<Long, Boolean> checkMap;

    private boolean isSelectMode;

    private OnAddSubReasonListener onAddSubReasonListener;

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_elim_reasons;
    }

    @Override
    protected void onBindItem(AdapterElimReasonsBinding binding, int position, EliminationReason bean) {

        binding.tvName.setText(bean.getName());
        int paddingLeft = getPaddingLeft(bean);
        binding.tvName.setPadding(paddingLeft, 0, ScreenUtils.dp2px(16), 0);

        binding.cbCheck.setVisibility(isSelectMode ? View.VISIBLE:View.GONE);
        if (isSelectMode) {
            if (checkMap.get(bean.getId()) == null) {
                binding.cbCheck.setChecked(false);
            }
            else {
                binding.cbCheck.setChecked(checkMap.get(bean.getId()));
            }
        }

        if (bean.getParent() == null) {
            binding.ivAdd.setVisibility(View.VISIBLE);
            binding.ivAdd.setOnClickListener(view -> {
                if (onAddSubReasonListener != null) {
                    onAddSubReasonListener.onClickAdd(bean);
                }
            });
        }
        else {
            binding.ivAdd.setVisibility(View.INVISIBLE);
        }
    }

    public void setOnAddSubReasonListener(OnAddSubReasonListener onAddSubReasonListener) {
        this.onAddSubReasonListener = onAddSubReasonListener;
    }

    private int getPaddingLeft(EliminationReason bean) {
        int left = 0;
        EliminationReason parent = bean.getParent();
        while (parent != null) {
            left += ScreenUtils.dp2px(40);
            parent = parent.getParent();
        }
        return left;
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

    public interface OnAddSubReasonListener {
        void onClickAdd(EliminationReason parent);
    }
}
