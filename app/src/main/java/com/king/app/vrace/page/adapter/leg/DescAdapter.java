package com.king.app.vrace.page.adapter.leg;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseBindingAdapter;
import com.king.app.vrace.base.BaseRecyclerAdapter;
import com.king.app.vrace.databinding.AdapterLegPageDescBinding;
import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.LegSpecial;
import com.king.app.vrace.page.adapter.LegSpecialAdapter;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/25 16:01
 */
public class DescAdapter {

    private ComplexHolder holder;

    public OnEditLegDescListener onEditLegDescListener;

    private LegSpecialAdapter specialAdapter;

    public DescAdapter(ComplexHolder holder) {
        this.holder = holder;
    }

    public RecyclerView.ViewHolder createViewHolder(ViewGroup parent) {
        AdapterLegPageDescBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.adapter_leg_page_desc, parent, false);
        BaseBindingAdapter.BindingHolder holder = new BaseBindingAdapter.BindingHolder(binding.getRoot());
        return holder;
    }

    public void onBindView(AdapterLegPageDescBinding binding, int position, Leg leg) {
        binding.etDesc.setText(leg.getDescription());
        binding.ivEditDesc.setOnClickListener(v -> {
            if (onEditLegDescListener != null) {
                onEditLegDescListener.onEditLegDesc(leg, position);
            }
        });
        binding.rvSpecial.setLayoutManager(new LinearLayoutManager(binding.rvSpecial.getContext(), LinearLayoutManager.HORIZONTAL, false));

        if (specialAdapter == null) {
            specialAdapter = new LegSpecialAdapter();
            specialAdapter.setOnItemClickListener((view, position1, data) -> {
                if (onEditLegDescListener != null) {
                    if (data == null) {
                        onEditLegDescListener.onAddSpecial();
                    }
                }
            });
            specialAdapter.setOnLongClickItemListener((position12, bean) -> {
                if (onEditLegDescListener != null) {
                    onEditLegDescListener.onRemoveSpecial(bean);
                }
            });
            specialAdapter.setList(leg.getSpecialList());
            binding.rvSpecial.setAdapter(specialAdapter);
        }
        else {
            specialAdapter.setList(leg.getSpecialList());
            specialAdapter.notifyDataSetChanged();
        }
    }

    public void setOnEditLegDescListener(OnEditLegDescListener onEditLegDescListener) {
        this.onEditLegDescListener = onEditLegDescListener;
    }

    public interface OnEditLegDescListener {
        void onEditLegDesc(Leg leg, int position);
        void onAddSpecial();
        void onRemoveSpecial(LegSpecial legSpecial);
    }
}
