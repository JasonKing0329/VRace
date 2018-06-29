package com.king.app.vrace.view.dialog.content;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;

import com.king.app.vrace.R;
import com.king.app.vrace.base.IFragmentHolder;
import com.king.app.vrace.conf.AppConstants;
import com.king.app.vrace.conf.GenderType;
import com.king.app.vrace.databinding.FragmentEditorSeasonTeamBinding;
import com.king.app.vrace.model.entity.Team;
import com.king.app.vrace.model.entity.TeamDao;
import com.king.app.vrace.model.entity.TeamSeason;
import com.king.app.vrace.page.TeamListActivity;
import com.king.app.vrace.utils.ColorUtil;
import com.king.app.vrace.view.dialog.DraggableContentFragment;
import com.king.app.vrace.viewmodel.SeasonViewModel;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/22 19:29
 */
public class SeasonTeamEditor extends DraggableContentFragment<FragmentEditorSeasonTeamBinding> {

    private final int REQUEST_SEASON = 601;

    private TeamSeason mTeamSeason;

    private SeasonViewModel seasonViewModel;

    private Team mTeam;

    private long mSeasonId;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_editor_season_team;
    }

    @Override
    protected void initView() {

        seasonViewModel = ViewModelProviders.of(getActivity()).get(SeasonViewModel.class);

        if (mTeamSeason != null) {
            mBinding.etEpSeq.setText(String.valueOf(mTeamSeason.getEpisodeSeq()));
            if (mTeamSeason.getTeam() != null) {
                mTeam = mTeamSeason.getTeam();
                updateTeamGroup();
            }
        }
        else {
            mTeamSeason = new TeamSeason();
        }

        mBinding.btnTeam.setOnClickListener(view -> selectTeam());
        mBinding.tvConfirm.setOnClickListener(view -> onConfirm());
    }

    public void setTeam(TeamSeason mTeam) {
        this.mTeamSeason = mTeam;
    }

    public void setSeasonId(long mSeasonId) {
        this.mSeasonId = mSeasonId;
    }

    private void selectTeam() {
        Intent intent = new Intent(getActivity(), TeamListActivity.class);
        intent.putExtra(TeamListActivity.EXTRA_SELECT_MODE, true);
        startActivityForResult(intent, REQUEST_SEASON);
    }

    private void onConfirm() {
        try {
            mTeamSeason.setEpisodeSeq(Integer.parseInt(mBinding.etEpSeq.getText().toString()));
        } catch (Exception e) {
            showMessageShort("Wrong episode sequence");
            return;
        }
        if (mTeam == null) {
            showMessageShort("Empty team");
            return;
        }
        mTeamSeason.setTeamId(mTeam.getId());
        mTeamSeason.setSeasonId(mSeasonId);
        // insert team
        getDaoSession().getTeamSeasonDao().insertOrReplace(mTeamSeason);
        // 相关加载项要刷新
        getDaoSession().getTeamSeasonDao().detachAll();
        getDaoSession().getTeamDao().detachAll();

        dismissAllowingStateLoss();
        seasonViewModel.loadTeams(mSeasonId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SEASON:
                    mTeam = getTeam(data.getLongExtra(TeamListActivity.RESP_TEAM_ID, -1));
                    updateTeamGroup();
                    break;
            }
        }
    }

    private void updateTeamGroup() {
        mBinding.groupTeam.setVisibility(View.VISIBLE);
        mBinding.tvName.setText(mTeam.getCode());
        if (mTeam.getSpecialColor() != 0) {
            GradientDrawable drawable = (GradientDrawable) mBinding.tvName.getBackground();
            drawable.setColor(mTeam.getSpecialColor());
            mBinding.tvName.setBackground(drawable);
            mBinding.tvName.setTextColor(ColorUtil.generateForgroundColorForBg(mTeam.getSpecialColor()));
        }

        mBinding.tvGender.setText(AppConstants.getGenderText(GenderType.values()[mTeam.getGenderType()]));
        mBinding.tvRelationship.setText(mTeam.getRelationship().getName());
        if (TextUtils.isEmpty(mTeam.getProvince())) {
            if (!TextUtils.isEmpty(mTeam.getCity())) {
                mBinding.tvPlace.setText(mTeam.getCity());
            }
        }
        else {
            if (TextUtils.isEmpty(mTeam.getCity())) {
                mBinding.tvPlace.setText(mTeam.getProvince());
            }
            else {
                if (mTeam.getCity().equals(mTeam.getProvince())) {
                    mBinding.tvPlace.setText(mTeam.getCity());
                }
                else {
                    mBinding.tvPlace.setText(mTeam.getProvince() + "/" + mTeam.getCity());
                }
            }
        }
        String occupy = null;
        if (mTeam.getPlayerList().size() > 0) {
            String occupy1 = mTeam.getPlayerList().get(0).getOccupy();
            String occupy2 = mTeam.getPlayerList().get(1).getOccupy();
            if (occupy1.equals(occupy2)) {
                occupy = occupy1;
            }
            else {
                occupy = occupy1 + "；" + occupy2;
            }
        }
        mBinding.tvOccupy.setText(occupy);
    }

    private Team getTeam(long teamId) {
        Team team = getDaoSession().getTeamDao()
                .queryBuilder()
                .where(TeamDao.Properties.Id.eq(teamId))
                .build().unique();
        return team;
    }
}
