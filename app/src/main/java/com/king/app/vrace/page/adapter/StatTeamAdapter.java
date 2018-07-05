package com.king.app.vrace.page.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.app.vrace.GlideApp;
import com.king.app.vrace.R;
import com.king.app.vrace.databinding.AdapterTeamBinding;
import com.king.app.vrace.model.entity.PersonTag;
import com.king.app.vrace.model.entity.Team;
import com.king.app.vrace.utils.ColorUtil;
import com.king.app.vrace.utils.FormatUtil;
import com.king.app.vrace.view.widget.flow.FlowLayout;
import com.king.app.vrace.viewmodel.bean.StatCountryItem;
import com.king.app.vrace.viewmodel.bean.StatTeamItem;
import com.king.app.vrace.viewmodel.bean.TeamListItem;
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractExpandableAdapterItem;

/**
 * @desc
 * @auth 景阳
 * @time 2018/6/28 0028 21:02
 */

public class StatTeamAdapter extends AbstractExpandableAdapterItem {

    private TextView tvName;
    private CheckBox cbCheck;
    private TextView tvGender;
    private TextView tvRelationship;
    private TextView tvPlace;
    private TextView tvOccupy;
    private FlowLayout flowTags;
    private TextView tvPoint;
    private TextView tvChampion;
    private TextView tvLegs;
    private TextView tvSeq;

    private StatTeamItem mItem;

    public StatTeamAdapter(OnTeamItemClickListener listener) {
        this.onTeamItemClickListener = listener;
    }

    public OnTeamItemClickListener onTeamItemClickListener;

    @Override
    public void onExpansionToggled(boolean expanded) {

    }

    @Override
    public int getLayoutResId() {
        return R.layout.adapter_team;
    }

    @Override
    public void onBindViews(View root) {
        tvName = root.findViewById(R.id.tv_name);
        cbCheck = root.findViewById(R.id.cb_check);
        tvGender = root.findViewById(R.id.tv_gender);
        tvRelationship = root.findViewById(R.id.tv_relationship);
        tvPlace = root.findViewById(R.id.tv_place);
        tvOccupy = root.findViewById(R.id.tv_occupy);
        flowTags = root.findViewById(R.id.flow_tags);
        tvPoint = root.findViewById(R.id.tv_point);
        tvChampion = root.findViewById(R.id.tv_champion);
        tvLegs = root.findViewById(R.id.tv_legs);
        tvSeq = root.findViewById(R.id.tv_seq);

        root.setOnClickListener(view -> {
            if (onTeamItemClickListener != null) {
                onTeamItemClickListener.onClickTeam(mItem);
            }
        });
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(Object model, int position) {
        super.onUpdateViews(model, position);
        mItem = (StatTeamItem) model;
        mItem.setItemPosition(position);

        TeamListItem bean = mItem.getBean();

        updateNameBg(tvName, bean.getBean());

        tvSeq.setText(String.valueOf(position - mItem.getHeaderPosition()));

        if (bean.getResult() == null) {
            tvPoint.setVisibility(View.GONE);
            tvChampion.setVisibility(View.GONE);
            tvLegs.setVisibility(View.GONE);
            tvLegs.setTextColor(tvLegs.getResources().getColor(R.color.text_sub));
        }
        else {
            tvLegs.setVisibility(View.VISIBLE);
            tvLegs.setText(bean.getResult().getEndRank());
            // winner
            if (bean.getResult().getEndPosition() == 1) {
                tvLegs.setTextColor(tvLegs.getResources().getColor(R.color.redC93437));
            }
            else if (bean.getResult().getEndPosition() == 2) {
                tvLegs.setTextColor(tvLegs.getResources().getColor(R.color.green34A350));
            }
            else if (bean.getResult().getEndPosition() == 3) {
                tvLegs.setTextColor(tvLegs.getResources().getColor(R.color.green34A350));
            }
            else {
                tvLegs.setTextColor(tvLegs.getResources().getColor(R.color.text_sub));
            }
            if (bean.getResult().getChampions() > 0) {
                tvChampion.setText("赛冠(" + bean.getResult().getChampions() + ")");
                tvChampion.setVisibility(View.VISIBLE);
            }
            else {
                tvChampion.setVisibility(View.GONE);
            }
            if (bean.getResult().getPoint() > 0) {
                tvPoint.setText(FormatUtil.pointZZ(bean.getResult().getPoint()));
                tvPoint.setVisibility(View.VISIBLE);
            }
            else {
                tvPoint.setVisibility(View.GONE);
            }
        }

        cbCheck.setVisibility(View.GONE);

        flowTags.removeAllViews();
        if (bean.getBean().getTagList().size() > 0) {
            showTags(bean.getBean());
            flowTags.setVisibility(View.VISIBLE);
        }
        else {
            flowTags.setVisibility(View.GONE);
        }

        tvGender.setText(bean.getGender());
        tvName.setText(bean.getName());
        tvRelationship.setText(bean.getRelationship());
        tvPlace.setText(bean.getPlace());
        tvOccupy.setText(bean.getOccupy());
    }

    private void showTags(Team bean) {
        SimpleTagAdapter<PersonTag> adapter = new SimpleTagAdapter<PersonTag>() {
            @Override
            protected String getText(PersonTag data) {
                return data.getTag();
            }

            @Override
            protected long getId(PersonTag data) {
                return data.getId();
            }

            @Override
            protected boolean isDisabled(PersonTag item) {
                return false;
            }
        };
        adapter.setData(bean.getTagList());
        int color;
        if (bean.getSpecialColor() != 0) {
            color = bean.getSpecialColor();
        }
        else {
            color = flowTags.getResources().getColor(R.color.colorAccent);
        }
        adapter.setTagColor(color);
        adapter.setTextColor(ColorUtil.generateForgroundColorForBg(color));
        adapter.bindFlowLayout(flowTags);
    }

    private void updateNameBg(TextView tvName, Team bean) {
        GradientDrawable drawable = (GradientDrawable) tvName.getBackground();
        if (bean.getSpecialColor() != 0) {
            drawable.setColor(bean.getSpecialColor());
            tvName.setTextColor(ColorUtil.generateForgroundColorForBg(bean.getSpecialColor()));
        }
        else {
            drawable.setColor(tvName.getResources().getColor(R.color.colorAccent));
            tvName.setTextColor(tvName.getResources().getColor(R.color.white));
        }
        tvName.setBackground(drawable);
    }

    public interface OnTeamItemClickListener {
        void onClickTeam(StatTeamItem item);
    }
}
