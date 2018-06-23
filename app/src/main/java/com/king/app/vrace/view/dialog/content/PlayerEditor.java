package com.king.app.vrace.view.dialog.content;

import android.arch.lifecycle.ViewModelProviders;
import android.view.View;
import android.widget.AdapterView;

import com.king.app.vrace.R;
import com.king.app.vrace.base.IFragmentHolder;
import com.king.app.vrace.base.RaceApplication;
import com.king.app.vrace.databinding.FragmentEditorPlayerBinding;
import com.king.app.vrace.model.entity.Player;
import com.king.app.vrace.model.entity.PlayerDao;
import com.king.app.vrace.view.dialog.DatePickerFragment;
import com.king.app.vrace.view.dialog.DraggableContentFragment;
import com.king.app.vrace.view.dialog.DraggableDialogFragment;
import com.king.app.vrace.viewmodel.PlayerListViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/22 9:15
 */
public class PlayerEditor extends DraggableContentFragment<FragmentEditorPlayerBinding> {

    private Player mPlayer;

    private SimpleDateFormat dateFormat;

    private String mBirthday;

    private long mDebutSeasonId;

    private PlayerListViewModel listViewModel;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_editor_player;
    }

    @Override
    protected void initView() {

        listViewModel = ViewModelProviders.of(getActivity()).get(PlayerListViewModel.class);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (mPlayer != null) {
            mBinding.etName.setText(mPlayer.getName());
            mBinding.etAge.setText(String.valueOf(mPlayer.getAge()));
            mBinding.etProvince.setText(mPlayer.getProvince());
            mBinding.etCity.setText(mPlayer.getCity());
            mBinding.etOccupy.setText(mPlayer.getOccupy());
            mBinding.etDesc.setText(mPlayer.getDescription());
            mBirthday = mPlayer.getBirthday();
            if (mBirthday != null) {
                mBinding.btnBirthday.setText(mPlayer.getBirthday());
            }
            mDebutSeasonId = mPlayer.getDebutSeasonId();
            mBinding.btnDebut.setText("S" + mPlayer.getDebutSeason().getIndex());
            mBinding.spGender.setSelection(mPlayer.getGender());
        }
        else {
            mPlayer = new Player();
        }

        mBinding.spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPlayer.setGender(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBinding.btnDebut.setOnClickListener(v -> selectSeason());

        mBinding.btnBirthday.setOnClickListener(v -> selectBirthday());

        mBinding.tvConfirm.setOnClickListener(v -> onConfirm());
    }

    private void selectBirthday() {
        DatePickerFragment picker = new DatePickerFragment();
        picker.setDate(mBirthday);
        picker.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mBirthday = dateFormat.format(calendar.getTime());
            mPlayer.setBirthday(mBirthday);
            mBinding.btnBirthday.setText(mBirthday);
        });
        picker.show(getChildFragmentManager(), "DatePickerFragment");
    }

    private void selectSeason() {
        SeasonSelector selector = new SeasonSelector();
        selector.setOnSeasonSelectedListener(season -> {
            mDebutSeasonId = season.getId();
            mBinding.btnDebut.setText("S" + season.getIndex());
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setTitle("Select season");
        dialogFragment.setContentFragment(selector);
        dialogFragment.show(getChildFragmentManager(), "SeasonSelector");
    }

    public void setPlayer(Player mPlayer) {
        this.mPlayer = mPlayer;
    }

    private void onConfirm() {
        try {
            mPlayer.setAge(Integer.parseInt(mBinding.etAge.getText().toString()));
        } catch (Exception e) {
            showMessageShort("Wrong age");
            return;
        }
        if (mDebutSeasonId == 0) {
            showMessageShort("Please select debut season");
            return;
        }
        mPlayer.setDebutSeasonId(mDebutSeasonId);
        mPlayer.setName(mBinding.etName.getText().toString());
        mPlayer.setProvince(mBinding.etProvince.getText().toString());
        mPlayer.setCity(mBinding.etCity.getText().toString());
        mPlayer.setOccupy(mBinding.etOccupy.getText().toString());
        mPlayer.setDescription(mBinding.etDesc.getText().toString());
        mPlayer.setGender(mBinding.spGender.getSelectedItemPosition());
        mPlayer.setBirthday(mBirthday);
        PlayerDao dao = RaceApplication.getInstance().getDaoSession().getPlayerDao();
        dao.insertOrReplace(mPlayer);
        dao.detachAll();

        dismissAllowingStateLoss();
        listViewModel.loadPlayers();
    }
}
