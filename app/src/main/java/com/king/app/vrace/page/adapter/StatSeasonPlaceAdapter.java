package com.king.app.vrace.page.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.king.app.vrace.GlideApp;
import com.king.app.vrace.R;
import com.king.app.vrace.databinding.AdapterStatisticPlaceBinding;
import com.king.app.vrace.databinding.AdapterStatisticPlaceSeasonTitleBinding;
import com.king.app.vrace.viewmodel.bean.StatisticPlaceItem;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/31 16:31
 */
public class StatSeasonPlaceAdapter extends RecyclerView.Adapter {
    private final int TYPE_HEAD = 1;
    private final int TYPE_ITEM = 0;

    private List<Object> list;

    private OnItemClickListener onItemClickListener;

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof StatisticPlaceItem) {
            return TYPE_ITEM;
        }
        return TYPE_HEAD;
    }

    public void setList(List<Object> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (TYPE_HEAD == viewType) {
            AdapterStatisticPlaceSeasonTitleBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.adapter_statistic_place_season_title, parent, false);
            BindingHolder holder = new BindingHolder(binding.getRoot());
            return holder;
        }
        else {
            AdapterStatisticPlaceBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.adapter_statistic_place, parent, false);
            BindingHolder holder = new BindingHolder(binding.getRoot());
            holder.itemView.setOnClickListener(v -> {
                int position = holder.getLayoutPosition();
                onClickItem(v, position, (StatisticPlaceItem) list.get(position));
            });
            return holder;
        }
    }

    private void onClickItem(View v, int position, StatisticPlaceItem item) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(position, item);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEAD) {
            AdapterStatisticPlaceSeasonTitleBinding binding = DataBindingUtil.getBinding(holder.itemView);
            onBindTitle(binding, position, (String) list.get(position));
            binding.executePendingBindings();
        }
        else {
            AdapterStatisticPlaceBinding binding = DataBindingUtil.getBinding(holder.itemView);
            onBindItem(binding, position, (StatisticPlaceItem) list.get(position));
            binding.executePendingBindings();
        }
    }

    private void onBindTitle(AdapterStatisticPlaceSeasonTitleBinding binding, int position, String title) {
        binding.tvTitle.setText(title);
    }

    private void onBindItem(AdapterStatisticPlaceBinding binding, int position, StatisticPlaceItem bean) {
        binding.setBean(bean);
        binding.tvCountry.setText(bean.getPlace() + "(" + bean.getCount() + ")");

        GlideApp.with(binding.ivFlag.getContext())
                .load(bean.getImgPath())
                .error(R.drawable.ic_default_leg)
                .into(binding.ivFlag);
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

    public interface OnItemClickListener {
        void onItemClick(int position, StatisticPlaceItem item);
    }

}
