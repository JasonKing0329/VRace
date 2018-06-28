package com.king.app.vrace.page.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.app.vrace.GlideApp;
import com.king.app.vrace.R;
import com.king.app.vrace.viewmodel.bean.StatCountryItem;
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractExpandableAdapterItem;

/**
 * @desc
 * @auth 景阳
 * @time 2018/6/28 0028 21:02
 */

public class StatCountryAdapter extends AbstractExpandableAdapterItem {

    private ImageView ivFlag;
    private TextView tvCountry;
    private TextView tvSeasons;

    private StatCountryItem mItem;

    public StatCountryAdapter(OnCountryItemClickListener onCountryItemClickListener) {
        this.onCountryItemClickListener = onCountryItemClickListener;
    }

    public OnCountryItemClickListener onCountryItemClickListener;

    @Override
    public void onExpansionToggled(boolean expanded) {

    }

    @Override
    public int getLayoutResId() {
        return R.layout.adapter_statistic_place;
    }

    @Override
    public void onBindViews(View root) {
        ivFlag = root.findViewById(R.id.iv_flag);
        tvCountry = root.findViewById(R.id.tv_country);
        tvSeasons = root.findViewById(R.id.tv_seasons);

        root.setOnClickListener(view -> {
            if (onCountryItemClickListener != null) {
                onCountryItemClickListener.onClickCountry(mItem);
            }
        });
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(Object model, int position) {
        super.onUpdateViews(model, position);
        mItem = (StatCountryItem) model;
        mItem.setItemPosition(position);

        tvSeasons.setText(mItem.getBean().getSeasons());
        tvCountry.setText(mItem.getBean().getCountry() + "(" + mItem.getBean().getCount() + ")");

        GlideApp.with(ivFlag.getContext())
                .load(mItem.getBean().getFlagPath())
                .error(R.drawable.ic_default_leg)
                .into(ivFlag);
    }

    public interface OnCountryItemClickListener {
        void onClickCountry(StatCountryItem item);
    }
}
