package com.king.app.vrace.page.adapter.leg;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseBindingAdapter;
import com.king.app.vrace.base.RaceApplication;
import com.king.app.vrace.conf.AppConstants;
import com.king.app.vrace.conf.GenderType;
import com.king.app.vrace.databinding.AdapterLegTeamRankBinding;
import com.king.app.vrace.model.entity.EliminationReason;
import com.king.app.vrace.model.entity.LegTeam;
import com.king.app.vrace.model.entity.PersonTag;
import com.king.app.vrace.model.entity.Team;
import com.king.app.vrace.model.entity.TeamElimination;
import com.king.app.vrace.model.entity.TeamEliminationDao;
import com.king.app.vrace.model.setting.SettingProperty;
import com.king.app.vrace.page.adapter.SimpleTagAdapter;
import com.king.app.vrace.utils.ColorUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/25 16:02
 */
public class RankAdapter implements View.OnClickListener {

    private int selection;

    private ComplexHolder holder;

    private OnEditRankItemListener onEditRankItemListener;

    public RankAdapter(ComplexHolder holder) {
        this.holder = holder;
        selection = -1;
    }

    public RecyclerView.ViewHolder createViewHolder(ViewGroup parent) {
        AdapterLegTeamRankBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.adapter_leg_team_rank, parent, false);
        BaseBindingAdapter.BindingHolder holder = new BaseBindingAdapter.BindingHolder(binding.getRoot());
        return holder;
    }

    public void onBindView(AdapterLegTeamRankBinding binding, int position, LegTeam bean) {
        updateRank(binding.tvRank, bean);
        if (bean.getTeamId() != 0) {
            binding.tvTeam.setVisibility(View.VISIBLE);
            if (AppConstants.DATABASE_REAL == SettingProperty.getDatabaseType()) {
                binding.tvTeam.setText(bean.getTeam().getPlayerList().get(0).getName() + "&\n" + bean.getTeam().getPlayerList().get(1).getName());
            }
            else {
                binding.tvTeam.setText(bean.getTeam().getCode() + "\n" + AppConstants.getGenderText(GenderType.values()[bean.getTeam().getGenderType()]));
            }
            updateNameBg(binding.tvTeam, bean.getTeam());
        }
        else {
            binding.tvTeam.setVisibility(View.INVISIBLE);
        }
        binding.getRoot().setSelected(position == selection);
        binding.tvDesc.setText(bean.getDescription());

        binding.flowTags.removeAllViews();
        if (bean.getEliminated()) {
            binding.ivAdd.setVisibility(View.VISIBLE);
            binding.ivAdd.setOnClickListener(v -> {
                if (onEditRankItemListener != null) {
                    onEditRankItemListener.onAddReason(bean, position);
                }
            });
            showReasons(binding, bean, position);
        }
        else {
            binding.ivAdd.setVisibility(View.GONE);
            binding.flowTags.setVisibility(View.GONE);
        }

        binding.getRoot().setTag(position);
        binding.getRoot().setOnClickListener(this);

        binding.ivEdit.setOnClickListener(v -> {
            if (onEditRankItemListener != null) {
                onEditRankItemListener.onEditRankItem(bean, position);
            }
        });
        binding.getRoot().setOnLongClickListener(v -> {
            if (onEditRankItemListener != null) {
                onEditRankItemListener.onLongClickItem(bean, position);
            }
            return true;
        });
    }

    private void showReasons(AdapterLegTeamRankBinding binding, LegTeam bean, int position) {
        List<EliminationReason> list = new ArrayList<>();
        List<TeamElimination> te = RaceApplication.getInstance().getDaoSession().getTeamEliminationDao()
                .queryBuilder()
                .where(TeamEliminationDao.Properties.LegId.eq(bean.getLegId()))
                .where(TeamEliminationDao.Properties.TeamId.eq(bean.getTeamId()))
                .build().list();
        for (TeamElimination elimination:te) {
            list.add(elimination.getReason());
        }

        SimpleTagAdapter<EliminationReason> adapter = new SimpleTagAdapter<EliminationReason>() {
            @Override
            protected String getText(EliminationReason data) {
                return data.getName();
            }

            @Override
            protected long getId(EliminationReason data) {
                return data.getId();
            }

            @Override
            protected boolean isDisabled(EliminationReason item) {
                return false;
            }
        };
        adapter.setOnItemLongClickListener(new SimpleTagAdapter.OnItemLongClickListener<EliminationReason>() {
            @Override
            public void onLongClickItem(EliminationReason data) {
                if (onEditRankItemListener != null) {
                    onEditRankItemListener.onLongClickEliminationReason(bean, data, position);
                }
            }
        });
        adapter.setData(list);
        adapter.setTagColor(binding.flowTags.getResources().getColor(R.color.divider));
        adapter.setTextColor(binding.flowTags.getResources().getColor(R.color.text_sub));
        adapter.bindFlowLayout(binding.flowTags);
    }

    private void updateRank(TextView view, LegTeam bean) {
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        if (bean.getPosition() == 1) {
            drawable.setColor(view.getResources().getColor(R.color.red_f1303d));
        }
        else if (bean.getEliminated()) {
            drawable.setColor(view.getResources().getColor(R.color.text_main));
        }
        else {
            drawable.setColor(view.getResources().getColor(R.color.blue_00a5c4));
        }
        view.setBackground(drawable);
        view.setText(String.valueOf(bean.getPosition()));
    }

    private void updateNameBg(TextView view, Team bean) {
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        if (bean.getSpecialColor() != 0) {
            drawable.setColor(bean.getSpecialColor());
            view.setTextColor(ColorUtil.generateForgroundColorForBg(bean.getSpecialColor()));
        }
        else {
            drawable.setColor(view.getResources().getColor(R.color.colorAccent));
            view.setTextColor(view.getResources().getColor(R.color.white));
        }
        view.setBackground(drawable);
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        onClickItem(v, position);
    }

    public void setOnEditRankItemListener(OnEditRankItemListener onEditRankItemListener) {
        this.onEditRankItemListener = onEditRankItemListener;
    }

    protected void onClickItem(View v, int position) {
        if (selection == position) {
            selection = -1;
        }
        else {
            selection = position;
        }
        holder.notifyDataSetChanged();
    }

    public LegTeam getSelectedItem() {
        if (selection != -1) {
            return (LegTeam) holder.getList().get(selection);
        }
        return null;
    }

    public void notifySelectionChanged() {
        if (selection != -1) {
            holder.notifyItemChanged(selection);
        }
    }

    public interface OnEditRankItemListener {
        void onAddReason(LegTeam legTeam, int position);

        void onEditRankItem(LegTeam legTeam, int position);

        void onLongClickItem(LegTeam bean, int position);

        void onLongClickEliminationReason(LegTeam legTeam, EliminationReason bean, int position);
    }
}
