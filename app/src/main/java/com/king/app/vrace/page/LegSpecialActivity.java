package com.king.app.vrace.page;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.view.View;

import com.king.app.vrace.R;
import com.king.app.vrace.base.MvvmActivity;
import com.king.app.vrace.conf.LegSpecialType;
import com.king.app.vrace.databinding.ActivityLegSpecialBinding;
import com.king.app.vrace.viewmodel.LegSpecialViewModel;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/5 11:03
 */
public class LegSpecialActivity extends MvvmActivity<ActivityLegSpecialBinding, LegSpecialViewModel> implements View.OnClickListener {

    public static final String RESP_SPEACIAL_TYPE = "special_type";

    @Override
    protected LegSpecialViewModel createViewModel() {
        return ViewModelProviders.of(this).get(LegSpecialViewModel.class);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_leg_special;
    }

    @Override
    protected void initView() {
        mBinding.ivUturn.setTag(LegSpecialType.U_TURN);
        mBinding.ivSpeedBump.setTag(LegSpecialType.SPEED_BUMP);
        mBinding.ivFf.setTag(LegSpecialType.FAST_FORWARD);
        mBinding.ivSpecify.setTag(LegSpecialType.SPECIFY);
        mBinding.ivEp.setTag(LegSpecialType.EP);
        mBinding.ivSafe.setTag(LegSpecialType.SAFE);
        mBinding.ivFfCard.setTag(LegSpecialType.FAST_FORWARD_CARD);
        mBinding.ivIntersection.setTag(LegSpecialType.INTERSECTION);
        mBinding.ivEasterEgg.setTag(LegSpecialType.EASTER_EGG);
        mBinding.ivUturn.setOnClickListener(this);
        mBinding.ivSpeedBump.setOnClickListener(this);
        mBinding.ivFf.setOnClickListener(this);
        mBinding.ivSpecify.setOnClickListener(this);
        mBinding.ivEp.setOnClickListener(this);
        mBinding.ivSafe.setOnClickListener(this);
        mBinding.ivFfCard.setOnClickListener(this);
        mBinding.ivIntersection.setOnClickListener(this);
        mBinding.ivEasterEgg.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        LegSpecialType type = (LegSpecialType) v.getTag();
        Intent intent = new Intent();
        intent.putExtra(RESP_SPEACIAL_TYPE, type.ordinal());
        setResult(RESULT_OK, intent);
        finish();
    }
}
