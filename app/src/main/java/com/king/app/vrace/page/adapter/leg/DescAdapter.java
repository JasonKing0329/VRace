package com.king.app.vrace.page.adapter.leg;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseBindingAdapter;
import com.king.app.vrace.databinding.AdapterLegPageDescBinding;
import com.king.app.vrace.model.entity.Leg;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/25 16:01
 */
public class DescAdapter {

    private ComplexHolder holder;

    public OnEditLegDescListener onEditLegDescListener;

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
    }

    public void setOnEditLegDescListener(OnEditLegDescListener onEditLegDescListener) {
        this.onEditLegDescListener = onEditLegDescListener;
    }

    public interface OnEditLegDescListener {
        void onEditLegDesc(Leg leg, int position);
    }
}
