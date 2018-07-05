package com.king.app.vrace.page.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseBindingAdapter;
import com.king.app.vrace.conf.LegSpecialType;
import com.king.app.vrace.databinding.AdapterLegSpecialHorBinding;
import com.king.app.vrace.model.entity.LegSpecial;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/5 11:22
 */
public class LegSpecialAdapter extends BaseBindingAdapter<AdapterLegSpecialHorBinding, LegSpecial> {

    private OnLongClickItemListener onLongClickItemListener;

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_leg_special_hor;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            onBindAdd(getBindingFromHolder(holder));
        }
        else {
            super.onBindViewHolder(holder, position);
        }
    }

    private void onBindAdd(AdapterLegSpecialHorBinding binding) {
        binding.ivContent.setImageResource(R.drawable.ic_add_circle_outline_red_a200_48dp);
    }

    public void setOnLongClickItemListener(OnLongClickItemListener onLongClickItemListener) {
        this.onLongClickItemListener = onLongClickItemListener;
    }

    @Override
    protected void onBindItem(AdapterLegSpecialHorBinding binding, int position, LegSpecial bean) {
        binding.ivContent.setImageResource(getSpecialRes(bean.getSpecialType()));
        binding.getRoot().setOnLongClickListener(v -> {
            if (onLongClickItemListener != null) {
                onLongClickItemListener.onLongClick(position, bean);
            }
            return true;
        });
    }

    @Override
    protected void onClickItem(View v, int position) {
        if (position == getItemCount() - 1) {
            if (onItemClickListener != null) {
                onItemClickListener.onClickItem(v, position, null);
            }
        }
        else {
            super.onClickItem(v, position);
        }
    }

    private int getSpecialRes(int specialType) {
        if (specialType == LegSpecialType.U_TURN.ordinal()) {
            return R.drawable.sp_uturn;
        }
        else if (specialType == LegSpecialType.SPEED_BUMP.ordinal()) {
            return R.drawable.sp_speed_bump;
        }
        else if (specialType == LegSpecialType.FAST_FORWARD.ordinal()) {
            return R.drawable.sp_ff;
        }
        else if (specialType == LegSpecialType.SPECIFY.ordinal()) {
            return R.drawable.sp_specify;
        }
        else if (specialType == LegSpecialType.EP.ordinal()) {
            return R.drawable.sp_ep;
        }
        else if (specialType == LegSpecialType.SAFE.ordinal()) {
            return R.drawable.sp_safe;
        }
        else if (specialType == LegSpecialType.FAST_FORWARD_CARD.ordinal()) {
            return R.drawable.sp_ff_card;
        }
        else if (specialType == LegSpecialType.INTERSECTION.ordinal()) {
            return R.drawable.sp_intersection;
        }
        else {
            return R.drawable.sp_easter_egg;
        }
    }

    public interface OnLongClickItemListener {
        void onLongClick(int position, LegSpecial bean);
    }
}
