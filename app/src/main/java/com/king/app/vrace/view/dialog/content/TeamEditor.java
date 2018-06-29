package com.king.app.vrace.view.dialog.content;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
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
import com.king.app.vrace.model.ParcelableTags;
import com.king.app.vrace.model.entity.PersonTag;
import com.king.app.vrace.model.entity.PersonTagDao;
import com.king.app.vrace.model.entity.Player;
import com.king.app.vrace.model.entity.PlayerDao;
import com.king.app.vrace.model.entity.Relationship;
import com.king.app.vrace.model.entity.RelationshipDao;
import com.king.app.vrace.model.entity.Team;
import com.king.app.vrace.model.entity.TeamPlayers;
import com.king.app.vrace.model.entity.TeamPlayersDao;
import com.king.app.vrace.model.entity.TeamTag;
import com.king.app.vrace.model.entity.TeamTagDao;
import com.king.app.vrace.page.PlayerListActivity;
import com.king.app.vrace.page.RelationshipListActivity;
import com.king.app.vrace.page.TagListActivity;
import com.king.app.vrace.utils.ColorUtil;
import com.king.app.vrace.utils.ListUtil;
import com.king.app.vrace.utils.ScreenUtils;
import com.king.app.vrace.view.dialog.DraggableContentFragment;
import com.king.app.vrace.view.dialog.DraggableDialogFragment;
import com.king.app.vrace.viewmodel.TeamListViewModel;

import java.util.ArrayList;
import java.util.List;

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

    private TeamListViewModel listViewModel;

    private int mTeamColor;

    private List<PersonTag> mTags;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_editor_team;
    }

    @Override
    protected void initView() {

        listViewModel = ViewModelProviders.of(getActivity()).get(TeamListViewModel.class);

        if (mTeam != null) {
            mTeamColor = mTeam.getSpecialColor();
            mBinding.etCode.setText(mTeam.getCode());
            mBinding.etProvince.setText(mTeam.getProvince());
            mBinding.etCity.setText(mTeam.getCity());
            if (mTeam.getPlayerList().size() > 0) {
                mPlayer1 = mTeam.getPlayerList().get(0);
                updatePlayerTextView(mBinding.tvPlayer1, mPlayer1);
            }
            if (mTeam.getPlayerList().size() > 1) {
                mPlayer2 = mTeam.getPlayerList().get(1);
                updatePlayerTextView(mBinding.tvPlayer2, mPlayer2);
            }
            if (mTeam.getRelationship() != null) {
                mRelationship = mTeam.getRelationship();
                mBinding.btnRelationship.setText(mRelationship.getName());
            }
            if (mTeam.getSpecialColor() != 0) {
                mBinding.tvColor.setBackgroundColor(mTeam.getSpecialColor());
            }
            mTags = mTeam.getTagList();
            showTags();
        }
        else {
            mTeam = new Team();
        }

        mBinding.tvPlayer1.setOnClickListener(view -> selectPlayer(REQUEST_PLAYER1));
        mBinding.tvPlayer2.setOnClickListener(view -> selectPlayer(REQUEST_PLAYER2));
        mBinding.btnRelationship.setOnClickListener(view -> selectRelationship());
        mBinding.btnTag.setOnClickListener(view -> selectTag());
        mBinding.tvConfirm.setOnClickListener(view -> onConfirm());
        mBinding.tvColor.setOnClickListener(view -> selectColor());
    }

    private void showTags() {
        if (mTags.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < mTags.size(); i ++) {
                if (i == 0) {
                    buffer.append(mTags.get(i).getTag());
                }
                else {
                    buffer.append("; ").append(mTags.get(i).getTag());
                }
            }
            mBinding.tvTags.setText(buffer.toString());
        }
    }

    public void setTeam(Team mTeam) {
        this.mTeam = mTeam;
    }

    private void selectTag() {
        Intent intent = new Intent(getActivity(), TagListActivity.class);
        if (mTeam != null && mTeam.getId() != null) {
            intent.putExtra(TagListActivity.REQUEST_TEAM_ID, mTeam.getId());
        }
        startActivityForResult(intent, REQUEST_TAG);
    }

    private void selectRelationship() {
        Intent intent = new Intent(getActivity(), RelationshipListActivity.class);
        startActivityForResult(intent, REQUEST_RELATIONSHIP);
    }

    private void selectPlayer(int requestCode) {
        Intent intent = new Intent(getActivity(), PlayerListActivity.class);
        startActivityForResult(intent, requestCode);
    }

    private void selectColor() {
        RichColorPicker picker = new RichColorPicker();
        picker.setInitColor(mTeamColor);
        picker.setOnColorSelectedListener(color -> {
            mTeamColor = color;
            mBinding.tvColor.setBackgroundColor(color);
            mBinding.tvColor.setTextColor(ColorUtil.generateForgroundColorForBg(color));
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(picker);
        dialogFragment.setMaxHeight(ScreenUtils.getScreenHeight());
        dialogFragment.setTitle("Color picker");
        dialogFragment.show(getChildFragmentManager(), "RichColorPicker");
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
        if (mRelationship == null) {
            showMessageShort("Empty relationship");
            return;
        }
        mTeam.setCode(code);
        mTeam.setProvince(province);
        mTeam.setCity(city);
        mTeam.setSpecialColor(mTeamColor);
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
        mTeam.setRelationshipId(mRelationship.getId());

        if (mTeam.getId() != null) {
            // remove players
            getDaoSession().getTeamPlayersDao().queryBuilder()
                    .where(TeamPlayersDao.Properties.TeamId.eq(mTeam.getId()))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            // remove tags
            getDaoSession().getTeamTagDao().queryBuilder()
                    .where(TeamTagDao.Properties.TeamId.eq(mTeam.getId()))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
        }
        // insert team
        getDaoSession().getTeamDao().insertOrReplace(mTeam);

        // insert players
        TeamPlayers teamPlayers = new TeamPlayers();
        teamPlayers.setPlayerId(mPlayer1.getId());
        teamPlayers.setTeamId(mTeam.getId());
        getDaoSession().getTeamPlayersDao().insert(teamPlayers);
        teamPlayers = new TeamPlayers();
        teamPlayers.setPlayerId(mPlayer2.getId());
        teamPlayers.setTeamId(mTeam.getId());
        getDaoSession().getTeamPlayersDao().insert(teamPlayers);
        getDaoSession().getTeamPlayersDao().detachAll();

        // insert tags
        if (!ListUtil.isEmpty(mTags)) {
            List<TeamTag> ttList = new ArrayList<>();
            for (PersonTag tag:mTags) {
                TeamTag tt = new TeamTag();
                tt.setTeamId(mTeam.getId());
                tt.setTagId(tag.getId());
                ttList.add(tt);
            }
            getDaoSession().getTeamTagDao().insertInTx(ttList);
        }
        getDaoSession().getTeamTagDao().detachAll();

        getDaoSession().getTeamDao().detachAll();
        dismissAllowingStateLoss();
        listViewModel.loadTeams();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
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
                    mRelationship = getRelationship(data.getLongExtra(RelationshipListActivity.RESP_RELATIONSHIP_ID, -1));
                    mBinding.btnRelationship.setText(mRelationship.getName());
                    break;
                case REQUEST_TAG:
                    ParcelableTags tags = data.getParcelableExtra(TagListActivity.RESP_TAG_IDS);
                    mTags = loadTags(tags);
                    showTags();
                    break;
            }
        }
    }

    private List<PersonTag> loadTags(ParcelableTags tags) {
        List<PersonTag> list = new ArrayList<>();
        if (tags.getTagIdList() != null) {
            PersonTagDao dao = RaceApplication.getInstance().getDaoSession().getPersonTagDao();
            for (long id:tags.getTagIdList()) {
                PersonTag tag = dao.queryBuilder()
                        .where(PersonTagDao.Properties.Id.eq(id))
                        .unique();
                if (tag != null) {
                    list.add(tag);
                }
            }
        }
        return list;
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
        Player player = getDaoSession().getPlayerDao()
                .queryBuilder()
                .where(PlayerDao.Properties.Id.eq(playerId))
                .build().unique();
        return player;
    }

    private Relationship getRelationship(long relationshipId) {
        Relationship relationship = getDaoSession().getRelationshipDao()
                .queryBuilder()
                .where(RelationshipDao.Properties.Id.eq(relationshipId))
                .build().unique();
        return relationship;
    }
}
