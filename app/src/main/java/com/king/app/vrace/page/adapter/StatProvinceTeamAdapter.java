package com.king.app.vrace.page.adapter;

import android.support.annotation.NonNull;

import com.king.app.vrace.viewmodel.bean.StatProvinceItem;
import com.zaihuishou.expandablerecycleradapter.adapter.BaseExpandableAdapter;
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractAdapterItem;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/4/21 16:07
 */
public class StatProvinceTeamAdapter extends BaseExpandableAdapter {

    private final int ITEM_TYPE_PROVINCE = 1;
    private final int ITEM_TYPE_TEAM = 0;

    private StatTeamAdapter.OnTeamItemClickListener onTeamItemClickListener;

    public StatProvinceTeamAdapter(List<StatProvinceItem> data) {
        super(data);
    }

    public void setOnTeamItemClickListener(StatTeamAdapter.OnTeamItemClickListener onTeamItemClickListener) {
        this.onTeamItemClickListener = onTeamItemClickListener;
    }

    @NonNull
    @Override
    public AbstractAdapterItem<Object> getItemView(Object type) {
        int itemType = (int) type;
        switch (itemType) {
            case ITEM_TYPE_PROVINCE:
                return new StatProvinceAdapter();
            default:
                return new StatTeamAdapter(onTeamItemClickListener);
        }
    }

    @Override
    public Object getItemViewType(Object t) {
        if (t instanceof StatProvinceItem) {
            return ITEM_TYPE_PROVINCE;
        }
        else {
            return ITEM_TYPE_TEAM;
        }
    }

}
