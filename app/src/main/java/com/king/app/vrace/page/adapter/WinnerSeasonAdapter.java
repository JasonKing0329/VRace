package com.king.app.vrace.page.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.king.app.vrace.R;
import com.king.app.vrace.databinding.AdapterStatisticTitleBinding;
import com.king.app.vrace.databinding.AdapterStatisticWinnerBinding;
import com.king.app.vrace.viewmodel.bean.StatisticWinnerItem;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/2 17:33
 */
public class WinnerSeasonAdapter extends RecyclerView.Adapter {

    private final int TYPE_HEAD = 1;
    private final int TYPE_ITEM = 0;

    private List<Object> list;

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof StatisticWinnerItem) {
            return TYPE_ITEM;
        }
        return TYPE_HEAD;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (TYPE_HEAD == viewType) {
            AdapterStatisticTitleBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.adapter_statistic_title, parent, false);
            BindingHolder holder = new BindingHolder(binding.getRoot());
            return holder;
        }
        else {
            AdapterStatisticWinnerBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.adapter_statistic_winner, parent, false);
            BindingHolder holder = new BindingHolder(binding.getRoot());
            holder.itemView.setOnClickListener(v -> {
                int position = holder.getLayoutPosition();
                onClickItem(v, position, (StatisticWinnerItem) list.get(position));
            });
        }
        return null;
    }

    private void onClickItem(View v, int position, StatisticWinnerItem item) {
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEAD) {
            AdapterStatisticTitleBinding binding = DataBindingUtil.getBinding(holder.itemView);
            onBindTitle(binding, position, (String) list.get(position));
            binding.executePendingBindings();
        }
        else {
            AdapterStatisticWinnerBinding binding = DataBindingUtil.getBinding(holder.itemView);
            onBindItem(binding, position, (StatisticWinnerItem) list.get(position));
            binding.executePendingBindings();
        }
    }

    private void onBindTitle(AdapterStatisticTitleBinding binding, int position, String title) {
        binding.tvTitle.setText(title);
    }

    private void onBindItem(AdapterStatisticWinnerBinding binding, int position, StatisticWinnerItem item) {

    }

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {

        public BindingHolder(View itemView) {
            super(itemView);
        }
    }
}
