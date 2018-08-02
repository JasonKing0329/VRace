package com.king.app.vrace.page;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.king.app.vrace.GlideApp;
import com.king.app.vrace.R;
import com.king.app.vrace.base.MvvmActivity;
import com.king.app.vrace.databinding.ActivityCountryBinding;
import com.king.app.vrace.model.ImageProvider;
import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.Season;
import com.king.app.vrace.model.palette.RacePalette;
import com.king.app.vrace.model.palette.RacePaletteUtil;
import com.king.app.vrace.page.adapter.CountryAdapter;
import com.king.app.vrace.utils.ScreenUtils;
import com.king.app.vrace.viewmodel.CountryViewModel;
import com.king.app.vrace.viewmodel.bean.CountryItem;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/1 17:55
 */
public class CountryActivity extends MvvmActivity<ActivityCountryBinding, CountryViewModel> {

    public static final String EXTRA_NAME = "extra_name";

    private CountryAdapter adapter;

    private Palette.Swatch mSwatch;

    @Override
    protected boolean updateStatusBarColor() {
        return false;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_country;
    }

    @Override
    protected void initView() {
        mBinding.actionbar.setOnBackListener(() -> finish());

        mBinding.rvSeasons.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvSeasons.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = ScreenUtils.dp2px(10);
            }
        });
    }

    @Override
    protected CountryViewModel createViewModel() {
        return ViewModelProviders.of(this).get(CountryViewModel.class);
    }

    @Override
    protected void initData() {
        String country = getIntent().getStringExtra(EXTRA_NAME);

        mModel.countryObserver.observe(this, list -> showData(list));
        mModel.loadData(country);

        showCountry(country);
    }

    private void showCountry(String country) {
        mBinding.actionbar.setTitle(country);
        mBinding.tvPlace.setText(country);
        GlideApp.with(this)
                .asBitmap()
                .load(ImageProvider.getCountryImagePath(country))
                .error(R.drawable.ic_default_leg)
                .listener(new RacePalette(getLifecycle()) {
                    @Override
                    protected void onPaletteCreated(Palette palette) {
                        updateColors(palette);
                    }
                })
                .into(mBinding.ivLeg);
    }

    private void updateColors(Palette palette) {
        mSwatch = RacePaletteUtil.getDefaultSwatch(palette);
        if (adapter != null) {
            adapter.setColorSwatch(mSwatch);
            adapter.notifyDataSetChanged();
        }
    }

    private void showData(List<CountryItem> list) {
        if (adapter == null) {
            adapter = new CountryAdapter();
            adapter.setList(list);
            adapter.setColorSwatch(mSwatch);
            adapter.setOnLegListener(new CountryAdapter.OnLegListener() {
                @Override
                public void onClickSeason(Season season) {
                    goToSeason(season);
                }

                @Override
                public void onClickLeg(Leg leg) {
                    if (leg != null) {
                        String country = leg.getPlaceList().get(0).getCountry();
                        showCountry(country);
                        mModel.loadData(country);
                    }
                }
            });
            mBinding.rvSeasons.setAdapter(adapter);
        }
        else {
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
    }

    private void goToSeason(Season season) {
        Intent intent = new Intent();
        intent.setClass(this, SeasonActivity.class);
        intent.putExtra(SeasonActivity.EXTRA_SEASON_ID, season.getId());
        startActivity(intent);
    }
}
