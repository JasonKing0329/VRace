package com.king.app.vrace.page.adapter;

import android.support.annotation.NonNull;

import com.king.app.vrace.viewmodel.bean.StatContinentItem;
import com.zaihuishou.expandablerecycleradapter.adapter.BaseExpandableAdapter;
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractAdapterItem;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/4/21 16:07
 */
public class StatPlaceAdapter extends BaseExpandableAdapter {

    private final int ITEM_TYPE_HEAD = 1;
    private final int ITEM_TYPE_COUNTRY = 0;

    private StatCountryAdapter.OnCountryItemClickListener onCountryItemClickListener;

    public StatPlaceAdapter(List<StatContinentItem> data) {
        super(data);
    }

    public void setOnCountryItemClickListener(StatCountryAdapter.OnCountryItemClickListener onCountryItemClickListener) {
        this.onCountryItemClickListener = onCountryItemClickListener;
    }

    @NonNull
    @Override
    public AbstractAdapterItem<Object> getItemView(Object type) {
        int itemType = (int) type;
        switch (itemType) {
            case ITEM_TYPE_HEAD:
                return new StatContinentAdapter();
            default:
                return new StatCountryAdapter(onCountryItemClickListener);
        }
    }

    @Override
    public Object getItemViewType(Object t) {
        if (t instanceof StatContinentItem) {
            return ITEM_TYPE_HEAD;
        }
        else {
            return ITEM_TYPE_COUNTRY;
        }
    }

}
