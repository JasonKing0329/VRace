package com.king.app.vrace.page.adapter;

import android.view.View;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseBindingAdapter;
import com.king.app.vrace.databinding.AdapterTagBinding;
import com.king.app.vrace.model.entity.PersonTag;

import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:16
 */
public class TagListAdapter extends BaseBindingAdapter<AdapterTagBinding, PersonTag> {

    private Map<Long, Boolean> checkMap;

    private boolean isSelectMode;

    private OnEditItemListener onEditItemListener;

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_tag;
    }

    public void setOnEditItemListener(OnEditItemListener onEditItemListener) {
        this.onEditItemListener = onEditItemListener;
    }

    @Override
    protected void onBindItem(AdapterTagBinding binding, int position, PersonTag bean) {
        binding.setBean(bean);

        binding.cbCheck.setVisibility(isSelectMode ? View.VISIBLE:View.GONE);
        if (isSelectMode) {
            if (checkMap.get(bean.getId()) == null) {
                binding.cbCheck.setChecked(false);
            }
            else {
                binding.cbCheck.setChecked(checkMap.get(bean.getId()));
            }
        }

        binding.ivEdit.setOnClickListener(v -> {
            if (onEditItemListener != null) {
                onEditItemListener.onEditItem(bean);
            }
        });
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

    public interface OnEditItemListener {
        void onEditItem(PersonTag tag);
    }
}
