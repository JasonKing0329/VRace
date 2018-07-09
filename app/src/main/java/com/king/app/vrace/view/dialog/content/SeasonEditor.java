package com.king.app.vrace.view.dialog.content;

import android.arch.lifecycle.ViewModelProviders;
import android.view.View;
import android.widget.AdapterView;

import com.king.app.vrace.R;
import com.king.app.vrace.base.IFragmentHolder;
import com.king.app.vrace.base.RaceApplication;
import com.king.app.vrace.databinding.FragmentEditorSeasonBinding;
import com.king.app.vrace.model.entity.Season;
import com.king.app.vrace.model.entity.SeasonDao;
import com.king.app.vrace.view.dialog.DatePickerFragment;
import com.king.app.vrace.view.dialog.DraggableContentFragment;
import com.king.app.vrace.viewmodel.SeasonListViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/22 9:15
 */
public class SeasonEditor extends DraggableContentFragment<FragmentEditorSeasonBinding> {

    private Season mSeason;

    private SimpleDateFormat dateFormat;

    private SeasonListViewModel listViewModel;

    private String mFilmingDate;

    private String mAirDate;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_editor_season;
    }

    @Override
    protected void initView() {
        listViewModel = ViewModelProviders.of(getActivity()).get(SeasonListViewModel.class);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (mSeason != null) {
            mBinding.etTheme.setText(mSeason.getTheme());
            mBinding.etIndex.setText(String.valueOf(mSeason.getIndex()));
            mBinding.etLegs.setText(String.valueOf(mSeason.getLeg()));
            mBinding.etTeams.setText(String.valueOf(mSeason.getTeam()));
            mBinding.spSeasonType.setSelection(mSeason.getType());
            mBinding.spTeamType.setSelection(mSeason.getTeamType());
            mFilmingDate = dateFormat.format(new Date(mSeason.getDateFilming()));
            mAirDate = dateFormat.format(new Date(mSeason.getDateAir()));
            mBinding.btnAir.setText("Air\n" + mAirDate);
            mBinding.btnFilming.setText("Filming\n" + mFilmingDate);
        }
        else {
            mSeason = new Season();
        }

        mBinding.spTeamType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSeason.setTeamType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBinding.spSeasonType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSeason.setType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBinding.btnFilming.setOnClickListener(v -> selectFilmingDate());

        mBinding.btnAir.setOnClickListener(v -> selectAirDate());

        mBinding.tvConfirm.setOnClickListener(v -> onConfirm());
    }

    private void selectAirDate() {
        DatePickerFragment picker = new DatePickerFragment();
        picker.setDate(mAirDate);
        picker.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mAirDate = dateFormat.format(calendar.getTime());
            mSeason.setDateAir(calendar.getTimeInMillis());
            mBinding.btnAir.setText("Air\n" + mAirDate);
        });
        picker.show(getChildFragmentManager(), "DatePickerFragment");
    }

    private void selectFilmingDate() {
        DatePickerFragment picker = new DatePickerFragment();
        picker.setDate(mFilmingDate);
        picker.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mFilmingDate = dateFormat.format(calendar.getTime());
            mSeason.setDateFilming(calendar.getTimeInMillis());
            mBinding.btnFilming.setText("Filming\n" + mFilmingDate);
        });
        picker.show(getChildFragmentManager(), "DatePickerFragment");
    }

    public void setSeason(Season season) {
        this.mSeason = season;
    }

    private void onConfirm() {
        try {
            mSeason.setIndex(Integer.parseInt(mBinding.etIndex.getText().toString()));
        } catch (Exception e) {
            showMessageShort("Wrong index");
            return;
        }
        try {
            mSeason.setLeg(Integer.parseInt(mBinding.etLegs.getText().toString()));
        } catch (Exception e) {
            showMessageShort("Wrong legs");
            return;
        }
        try {
            mSeason.setTeam(Integer.parseInt(mBinding.etTeams.getText().toString()));
        } catch (Exception e) {
            showMessageShort("Wrong teams");
            return;
        }
        try {
            mSeason.setDateFilming(dateFormat.parse(mFilmingDate).getTime());
        } catch (Exception e) {
            showMessageShort("Please select filming date");
            return;
        }
        try {
            mSeason.setDateAir(dateFormat.parse(mAirDate).getTime());
        } catch (Exception e) {
            showMessageShort("Please select air date");
            return;
        }
        mSeason.setType(mBinding.spSeasonType.getSelectedItemPosition());
        mSeason.setTeamType(mBinding.spTeamType.getSelectedItemPosition());
        mSeason.setTheme(mBinding.etTheme.getText().toString());
        SeasonDao dao = RaceApplication.getInstance().getDaoSession().getSeasonDao();
        dao.insertOrReplace(mSeason);
        dao.detachAll();

        dismissAllowingStateLoss();
        listViewModel.loadSeasons();
    }
}
