package com.king.app.vrace.view.dialog.content;

import android.arch.lifecycle.ViewModelProviders;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.king.app.vrace.R;
import com.king.app.vrace.base.IFragmentHolder;
import com.king.app.vrace.databinding.FragmentEditorLegBinding;
import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.LegPlaces;
import com.king.app.vrace.model.entity.LegPlacesDao;
import com.king.app.vrace.view.dialog.DraggableContentFragment;
import com.king.app.vrace.viewmodel.SeasonViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/22 9:15
 */
public class LegEditor extends DraggableContentFragment<FragmentEditorLegBinding> {

    private Leg mLeg;

    private SeasonViewModel seasonViewModel;

    private long mSeasonId;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_editor_leg;
    }

    @Override
    protected void initView() {
        seasonViewModel = ViewModelProviders.of(getActivity()).get(SeasonViewModel.class);

        if (mLeg != null) {
            mBinding.etIndex.setText(String.valueOf(mLeg.getIndex()));
            mBinding.etPlayers.setText(String.valueOf(mLeg.getPlayerNumber()));
            mBinding.etDesc.setText(mLeg.getDescription());
            mBinding.spType.setSelection(mLeg.getType());
            if (mLeg.getPlaceList().size() > 0) {
                mBinding.etCountry1.setText(mLeg.getPlaceList().get(0).getCountry());
                mBinding.etCity1.setText(mLeg.getPlaceList().get(0).getCity());
            }
            if (mLeg.getPlaceList().size() > 1) {
                mBinding.llPlace2.setVisibility(View.VISIBLE);
                mBinding.etCountry2.setText(mLeg.getPlaceList().get(1).getCountry());
                mBinding.etCity2.setText(mLeg.getPlaceList().get(1).getCity());
            }
            if (mLeg.getPlaceList().size() > 2) {
                mBinding.llPlace3.setVisibility(View.VISIBLE);
                mBinding.etCountry3.setText(mLeg.getPlaceList().get(2).getCountry());
                mBinding.etCity3.setText(mLeg.getPlaceList().get(2).getCity());
            }
        }
        else {
            mLeg = new Leg();
        }

        mBinding.spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mLeg.setType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBinding.tvConfirm.setOnClickListener(v -> onConfirm());
        mBinding.ivAddPlace.setOnClickListener(v -> addPlace());
        mBinding.ivRemove2.setOnClickListener(v -> mBinding.llPlace2.setVisibility(View.GONE));
        mBinding.ivRemove3.setOnClickListener(v -> {
            mBinding.llPlace3.setVisibility(View.GONE);
            mBinding.ivRemove2.setVisibility(View.VISIBLE);
        });
    }

    private void addPlace() {
        if (mBinding.llPlace2.getVisibility() == View.GONE) {
            mBinding.ivRemove2.setVisibility(View.VISIBLE);
            mBinding.llPlace2.setVisibility(View.VISIBLE);
        }
        else {
            if (mBinding.llPlace3.getVisibility() == View.GONE) {
                mBinding.llPlace3.setVisibility(View.VISIBLE);
                mBinding.ivRemove2.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setSeasonId(long seasonId) {
        this.mSeasonId = seasonId;
    }

    public void setLeg(Leg mLeg) {
        this.mLeg = mLeg;
    }

    private void onConfirm() {
        try {
            mLeg.setIndex(Integer.parseInt(mBinding.etIndex.getText().toString()));
        } catch (Exception e) {
            showMessageShort("Wrong index");
            return;
        }
        try {
            mLeg.setPlayerNumber(Integer.parseInt(mBinding.etPlayers.getText().toString()));
        } catch (Exception e) {
            showMessageShort("Wrong player number");
            return;
        }
        List<LegPlaces> places = new ArrayList<>();
        String country1 = mBinding.etCountry1.getText().toString();
        if (TextUtils.isEmpty(country1)) {
            showMessageShort("Empty country");
            return;
        }
        String city1 = mBinding.etCity1.getText().toString();
        if (TextUtils.isEmpty(city1)) {
            showMessageShort("Empty city");
            return;
        }
        LegPlaces place = new LegPlaces();
        place.setCity(city1);
        place.setCountry(country1);
        place.setSeq(1);
        places.add(place);
        if (mBinding.llPlace2.getVisibility() == View.VISIBLE) {
            String country = mBinding.etCountry2.getText().toString();
            if (TextUtils.isEmpty(country)) {
                showMessageShort("Empty country");
                return;
            }
            String city = mBinding.etCity2.getText().toString();
            if (TextUtils.isEmpty(city)) {
                showMessageShort("Empty city");
                return;
            }
            place = new LegPlaces();
            place.setCity(city);
            place.setCountry(country);
            place.setSeq(2);
            places.add(place);
        }
        if (mBinding.llPlace3.getVisibility() == View.VISIBLE) {
            String country = mBinding.etCountry3.getText().toString();
            if (TextUtils.isEmpty(country)) {
                showMessageShort("Empty country");
                return;
            }
            String city = mBinding.etCity2.getText().toString();
            if (TextUtils.isEmpty(country)) {
                showMessageShort("Empty city");
                return;
            }
            place = new LegPlaces();
            place.setCity(city);
            place.setCountry(country);
            place.setSeq(3);
            places.add(place);
        }

        if (mLeg.getId() != null) {
            // insert places
            getDaoSession().getLegPlacesDao().queryBuilder()
                    .where(LegPlacesDao.Properties.LegId.eq(mLeg.getId()))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
        }
        mLeg.setSeasonId(mSeasonId);
        mLeg.setType(mBinding.spType.getSelectedItemPosition());

        getDaoSession().getLegDao().insertOrReplace(mLeg);
        getDaoSession().getLegDao().detachAll();

        for (LegPlaces p:places) {
            p.setLegId(mLeg.getId());
            getDaoSession().getLegPlacesDao().insert(p);
        }

        dismissAllowingStateLoss();
        seasonViewModel.loadLegs(mSeasonId);
    }
}
