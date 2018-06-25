package com.king.app.vrace.page.adapter.leg;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.king.app.vrace.databinding.AdapterLegPageDescBinding;
import com.king.app.vrace.databinding.AdapterLegTeamRankBinding;
import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.LegTeam;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/25 15:48
 */
public class LegComplexAdapter extends RecyclerView.Adapter implements ComplexHolder {

    private final int TYPE_RANK = 0;
    private final int TYPE_DESC = 1;

    private List<Object> list;

    private DescAdapter descAdapter;
    private RankAdapter rankAdapter;

    public LegComplexAdapter() {
        descAdapter = new DescAdapter(this);
        rankAdapter = new RankAdapter(this);
    }

    public void setList(List<Object> list) {
        this.list = list;
    }

    @Override
    public List<Object> getList() {
        return list;
    }

    @Override
    public int getItemViewType(int position) {

        if (list.get(position) instanceof Leg) {
            return TYPE_DESC;
        }
        else {
            return TYPE_RANK;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_DESC) {
            return descAdapter.createViewHolder(parent);
        }
        else {
            return rankAdapter.createViewHolder(parent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewDataBinding binding = DataBindingUtil.getBinding(holder.itemView);
        if (binding instanceof AdapterLegPageDescBinding) {
            descAdapter.onBindView((AdapterLegPageDescBinding) binding, position, (Leg) list.get(position));
        }
        else {
            rankAdapter.onBindView((AdapterLegTeamRankBinding) binding, position, (LegTeam) list.get(position));
        }
        binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    public LegTeam getSelectedRank() {
        return rankAdapter.getSelectedItem();
    }

    public void notifySelectionChanged() {
        rankAdapter.notifySelectionChanged();
    }

    public void setOnEditRankItemListener(RankAdapter.OnEditRankItemListener onEditRankItemListener) {
        rankAdapter.setOnEditRankItemListener(onEditRankItemListener);
    }

    public void setOnEditLegDescListener(DescAdapter.OnEditLegDescListener onEditLegDescListener) {
        descAdapter.setOnEditLegDescListener(onEditLegDescListener);
    }

}
