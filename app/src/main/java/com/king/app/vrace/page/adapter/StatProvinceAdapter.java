package com.king.app.vrace.page.adapter;

import android.view.View;
import android.widget.TextView;

import com.king.app.vrace.R;
import com.king.app.vrace.viewmodel.bean.StatProvinceItem;
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractExpandableAdapterItem;

/**
 * @desc
 * @auth 景阳
 * @time 2018/6/28 0028 21:02
 */

public class StatProvinceAdapter extends AbstractExpandableAdapterItem {

    private TextView tvTitle;

    private StatProvinceItem mItem;

    @Override
    public void onExpansionToggled(boolean expanded) {

    }

    @Override
    public int getLayoutResId() {
        return R.layout.adapter_statistic_title;
    }

    @Override
    public void onBindViews(View root) {
        tvTitle = root.findViewById(R.id.tv_title);

        root.setOnClickListener(view -> doExpandOrUnexpand());
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(Object model, int position) {
        super.onUpdateViews(model, position);
        mItem = (StatProvinceItem) model;
        if (mItem.getChildItemList() != null) {
            for (int i = 0; i < mItem.getChildItemList().size(); i ++) {
                mItem.getChildItemList().get(i).setHeaderPosition(position);
            }
        }

        tvTitle.setText(mItem.getProvince() + "(" + mItem.getCount() + ")");
    }

}
