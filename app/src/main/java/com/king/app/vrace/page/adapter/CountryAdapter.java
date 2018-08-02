package com.king.app.vrace.page.adapter;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.graphics.Palette;
import android.view.View;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseBindingAdapter;
import com.king.app.vrace.conf.LegType;
import com.king.app.vrace.databinding.AdapterCountryItemBinding;
import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.Season;
import com.king.app.vrace.viewmodel.bean.CountryItem;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/1 17:53
 */
public class CountryAdapter extends BaseBindingAdapter<AdapterCountryItemBinding, CountryItem> {

    private OnLegListener onLegListener;
    private Palette.Swatch colorSwatch;

    public void setOnLegListener(OnLegListener onLegListener) {
        this.onLegListener = onLegListener;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_country_item;
    }

    @Override
    protected void onBindItem(AdapterCountryItemBinding binding, int position, CountryItem bean) {
        binding.setBean(bean);

        updateColors(binding);

        binding.llSeason.setOnClickListener(v -> {
            if (onLegListener != null) {
                onLegListener.onClickSeason(bean.getSeason());
            }
        });

        if (bean.getNextLeg().getType() == LegType.FINAL.ordinal()) {
            binding.ivNext.setVisibility(View.INVISIBLE);
            binding.llNext.setOnClickListener(null);
        }
        else {
            binding.ivNext.setVisibility(View.VISIBLE);
            binding.llNext.setOnClickListener(v -> {
                if (onLegListener != null) {
                    onLegListener.onClickLeg(bean.getNextLeg());
                }
            });
        }

        if (bean.getPreviousLeg().getIndex() == 0) {
            binding.ivPrevious.setVisibility(View.INVISIBLE);
            binding.llPrevious.setOnClickListener(null);
        }
        else {
            binding.ivPrevious.setVisibility(View.VISIBLE);
            binding.llPrevious.setOnClickListener(v -> {
                if (onLegListener != null) {
                    onLegListener.onClickLeg(bean.getPreviousLeg());
                }
            });
        }
    }

    private void updateColors(AdapterCountryItemBinding binding) {
        if (colorSwatch != null) {
            GradientDrawable drawable = (GradientDrawable) binding.llSeason.getBackground();
            drawable.setColor(colorSwatch.getRgb());
            binding.llSeason.setBackground(drawable);

            binding.tvSeason.setTextColor(colorSwatch.getTitleTextColor());
            binding.tvLeg.setTextColor(colorSwatch.getBodyTextColor());
        }
    }

    public void setColorSwatch(Palette.Swatch colorSwatch) {
        this.colorSwatch = colorSwatch;
    }

    public interface OnLegListener {
        void onClickSeason(Season season);
        void onClickLeg(Leg leg);
    }
}
