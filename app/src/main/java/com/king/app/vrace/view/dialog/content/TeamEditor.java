package com.king.app.vrace.view.dialog.content;

import android.content.Intent;

import com.king.app.vrace.R;
import com.king.app.vrace.base.IFragmentHolder;
import com.king.app.vrace.databinding.FragmentEditorTeamBinding;
import com.king.app.vrace.model.entity.Player;
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PLAYER1:
                break;
            case REQUEST_PLAYER2:
                break;
            case REQUEST_RELATIONSHIP:
                break;
            case REQUEST_TAG:
                break;
        }
    }
}
