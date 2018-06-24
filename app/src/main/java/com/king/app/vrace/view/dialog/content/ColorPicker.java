package com.king.app.vrace.view.dialog.content;

import com.king.app.vrace.R;
import com.king.app.vrace.base.IFragmentHolder;
import com.king.app.vrace.databinding.FragmentColorPickerBinding;
import com.king.app.vrace.view.dialog.DraggableContentFragment;

/**
 * @desc
 * @auth 景阳
 * @time 2018/6/24 0024 14:57
 */

public class ColorPicker extends DraggableContentFragment<FragmentColorPickerBinding> {

    private OnColorSelectedListener onColorSelectedListener;

    private int mColor;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_color_picker;
    }

    @Override
    protected void initView() {

        mBinding.colorPicker.setColorListener(color -> {
            mColor = color;
            mBinding.etColorValue.setText("#" + mBinding.colorPicker.getColorHtml());
            mBinding.vColorBg.setBackgroundColor(color);
        });

        mBinding.tvConfirm.setOnClickListener(view -> {
            if (onColorSelectedListener != null) {
                onColorSelectedListener.onColorSelected(mColor);
            }
            dismissAllowingStateLoss();
        });
    }

    public void setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
        this.onColorSelectedListener = onColorSelectedListener;
    }

    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }
}
