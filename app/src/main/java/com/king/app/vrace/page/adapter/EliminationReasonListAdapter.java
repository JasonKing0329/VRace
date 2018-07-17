package com.king.app.vrace.page.adapter;

import android.view.View;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseBindingAdapter;
import com.king.app.vrace.databinding.AdapterElimReasonsBinding;
import com.king.app.vrace.model.entity.EliminationReason;
import com.king.app.vrace.utils.ScreenUtils;
import com.king.app.vrace.viewmodel.bean.EliminationItem;

import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:16
 */
public class EliminationReasonListAdapter extends BaseBindingAdapter<AdapterElimReasonsBinding, EliminationItem> {

    private Map<Long, Boolean> checkMap;

    private boolean isSelectMode;

    private OnReasonListener onReasonListener;

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_elim_reasons;
    }

    @Override
    protected void onBindItem(AdapterElimReasonsBinding binding, int position, EliminationItem bean) {

        binding.tvName.setText(bean.getName() + "(" + bean.getNumber() + ")");
        int paddingLeft = getPaddingLeft(bean.getBean());
        binding.tvName.setPadding(paddingLeft, 0, ScreenUtils.dp2px(16), 0);

        binding.cbCheck.setVisibility(isSelectMode ? View.VISIBLE:View.GONE);
        if (isSelectMode) {
            if (checkMap.get(bean.getBean().getId()) == null) {
                binding.cbCheck.setChecked(false);
            }
            else {
                binding.cbCheck.setChecked(checkMap.get(bean.getBean().getId()));
            }
        }

        binding.ivDetail.setOnClickListener(view -> {
            if (onReasonListener != null) {
                onReasonListener.onClickDetail(bean.getBean());
            }
        });

        if (bean.getBean().getParent() == null) {
            binding.ivAdd.setVisibility(View.VISIBLE);
            binding.ivAdd.setOnClickListener(view -> {
                if (onReasonListener != null) {
                    onReasonListener.onClickAdd(bean.getBean());
                }
            });
        }
        else {
            binding.ivAdd.setVisibility(View.INVISIBLE);
        }
    }

    public void setOnReasonListener(OnReasonListener onReasonListener) {
        this.onReasonListener = onReasonListener;
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

    public interface OnReasonListener {
        void onClickAdd(EliminationReason parent);
        void onClickDetail(EliminationReason parent);
    }
}
