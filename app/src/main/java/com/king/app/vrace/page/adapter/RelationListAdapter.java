package com.king.app.vrace.page.adapter;

import android.view.View;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseBindingAdapter;
import com.king.app.vrace.databinding.AdapterRelationshipBinding;
import com.king.app.vrace.model.entity.Relationship;
import com.king.app.vrace.utils.ScreenUtils;

import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/21 20:16
 */
public class RelationListAdapter extends BaseBindingAdapter<AdapterRelationshipBinding, Relationship> {

    private Map<Long, Boolean> checkMap;

    private boolean isSelectMode;

    private OnAddSubRelationListener onAddSubRelationListener;

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_relationship;
    }

    @Override
    protected void onBindItem(AdapterRelationshipBinding binding, int position, Relationship bean) {

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
                if (onAddSubRelationListener != null) {
                    onAddSubRelationListener.onClickAdd(bean);
                }
            });
        }
        else {
            binding.ivAdd.setVisibility(View.INVISIBLE);
        }
    }

    public void setOnAddSubRelationListener(OnAddSubRelationListener onAddSubRelationListener) {
        this.onAddSubRelationListener = onAddSubRelationListener;
    }

    private int getPaddingLeft(Relationship bean) {
        int left = 0;
        Relationship parent = bean.getParent();
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

    public interface OnAddSubRelationListener {
        void onClickAdd(Relationship parent);
    }
}
