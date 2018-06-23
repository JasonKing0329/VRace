package com.king.app.vrace.view.dialog.content;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.widget.TextView;

import com.king.app.vrace.R;
import com.king.app.vrace.base.IFragmentHolder;
import com.king.app.vrace.base.RaceApplication;
import com.king.app.vrace.conf.Gender;
import com.king.app.vrace.conf.GenderType;
import com.king.app.vrace.databinding.FragmentEditorTeamBinding;
import com.king.app.vrace.model.entity.Player;
import com.king.app.vrace.model.entity.PlayerDao;
import com.king.app.vrace.model.entity.Relationship;
import com.king.app.vrace.model.entity.Team;
import com.king.app.vrace.page.PlayerListActivity;
import com.king.app.vrace.view.dialog.DraggableContentFragment;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/22 19:29
 */
public class TeamEditor extends DraggableContentFragment<FragmentEditorTeamBinding> {

    private final int REQUEST_PLAYER1 = 501;
    private final int REQUEST_PLAYER2 = 502;
    private final int REQUEST_RELATIONSHIP = 503;
    private final int REQUEST_TAG = 504;

    private Team mTeam;

    private Player mPlayer1;

    private Player mPlayer2;

    private Relationship mRelationship;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_editor_team;
    }

    @Override
    protected void initView() {
        if (mTeam != null) {
            mBinding.etCode.setText(mTeam.getCode());
            mBinding.etProvince.setText(mTeam.getProvince());
            mBinding.etCity.setText(mTeam.getCity());
            if (mTeam.getPlayerList().size() > 0) {
                mPlayer1 = mTeam.getPlayerList().get(0);
                updatePlayerTextView(mBinding.tvPlayer1, mPlayer1);
            }
            if (mTeam.getPlayerList().size() > 1) {
                mPlayer1 = mTeam.getPlayerList().get(1);
                updatePlayerTextView(mBinding.tvPlayer2, mPlayer2);
            }
            if (mTeam.getRelationship() != null) {
                mRelationship = mTeam.getRelationship();
                mBinding.btnRelationship.setText(mRelationship.getName());
            }
        }
        else {
            mTeam = new Team();
        }

        mBinding.tvPlayer1.setOnClickListener(view -> selectPlayer(REQUEST_PLAYER1));
        mBinding.tvPlayer2.setOnClickListener(view -> selectPlayer(REQUEST_PLAYER2));
        mBinding.btnRelationship.setOnClickListener(view -> selectRelationship());
        mBinding.btnTag.setOnClickListener(view -> selectTag());
        mBinding.tvConfirm.setOnClickListener(view -> onConfirm());
    }

    public void setTeam(Team mTeam) {
        this.mTeam = mTeam;
    }

    private void selectTag() {
    }

    private void selectRelationship() {
    }

    private void selectPlayer(int requestCode) {
        Intent intent = new Intent(getActivity(), PlayerListActivity.class);
        startActivityForResult(intent, requestCode);
    }

    private void onConfirm() {
        String code = mBinding.etCode.getText().toString();
        if (TextUtils.isEmpty(code)) {
            showMessageShort("Empty code");
            return;
        }
        String province = mBinding.etProvince.getText().toString();
        if (TextUtils.isEmpty(province)) {
            showMessageShort("Empty province");
            return;
        }
        String city = mBinding.etCity.getText().toString();
        if (TextUtils.isEmpty(city)) {
            showMessageShort("Empty city");
            return;
        }
        if (mPlayer1 == null) {
            showMessageShort("Empty player1");
            return;
        }
        if (mPlayer2 == null) {
            showMessageShort("Empty player2");
            return;
        }
        mTeam.setCode(code);
        mTeam.setProvince(province);
        mTeam.setCode(city);
        if (mPlayer1.getGender() == Gender.MALE.ordinal()) {
            if (mPlayer2.getGender() == Gender.MALE.ordinal()) {
                mTeam.setGenderType(GenderType.MM.ordinal());
            }
            else {
                mTeam.setGenderType(GenderType.MW.ordinal());
            }
        }
        else {
            if (mPlayer2.getGender() == Gender.MALE.ordinal()) {
                mTeam.setGenderType(GenderType.MW.ordinal());
            }
            else {
                mTeam.setGenderType(GenderType.WW.ordinal());
            }
        }


        // insert players

        // insert tags
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PLAYER1:
                mPlayer1 = getPlayer(data.getLongExtra(PlayerListActivity.RESP_PLAYER_ID, -1));
                updatePlayerTextView(mBinding.tvPlayer1, mPlayer1);
                break;
            case REQUEST_PLAYER2:
                mPlayer2 = getPlayer(data.getLongExtra(PlayerListActivity.RESP_PLAYER_ID, -1));
                updatePlayerTextView(mBinding.tvPlayer2, mPlayer2);
                break;
            case REQUEST_RELATIONSHIP:
                break;
            case REQUEST_TAG:
                break;
        }
    }

    private void updatePlayerTextView(TextView tv, Player player) {
        tv.setText(player.getName() + "\n" + player.getOccupy());
        GradientDrawable drawable = (GradientDrawable) tv.getBackground();
        if (player.getGender() == Gender.FEMALE.ordinal()) {
            drawable.setColor(getResources().getColor(R.color.colorAccent));
        }
        else {
            drawable.setColor(getResources().getColor(R.color.colorPrimary));
        }
        tv.setBackground(drawable);
    }

    private Player getPlayer(long playerId) {
        Player player = RaceApplication.getInstance().getDaoSession().getPlayerDao()
                .queryBuilder()
                .where(PlayerDao.Properties.Id.eq(playerId))
                .build().unique();
        return player;
    }
}
