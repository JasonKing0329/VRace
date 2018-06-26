package com.king.app.vrace.view.dialog.content;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;

import com.king.app.vrace.R;
import com.king.app.vrace.base.IFragmentHolder;
import com.king.app.vrace.databinding.FragmentColorPickerRichBinding;
import com.king.app.vrace.utils.ColorUtil;
import com.king.app.vrace.view.dialog.DraggableContentFragment;

/**
 * @desc 覆盖argb所有色值的颜色选择控件，改良后支持编辑色值，和PhotoShop的颜色选择器一样
 * @auth 景阳
 * @time 2018/6/24 0024 14:57
 */
public class RichColorPicker extends DraggableContentFragment<FragmentColorPickerRichBinding> {

    private OnColorSelectedListener onColorSelectedListener;

    private Integer mColor = null;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_color_picker_rich;
    }

    @Override
    protected void initView() {

        if (mColor != null) {
            updateColor(mColor);
            mBinding.etColorValue.setText(ColorUtil.formatColor(mColor));
        }

        mBinding.colorPicker.setOnColorChangedListener(newColor -> {
            mColor = newColor;
            mBinding.tvColorBg.setBackgroundColor(newColor);
            mBinding.etColorValue.setText(ColorUtil.formatColor(newColor));
        });

        mBinding.tvConfirm.setOnClickListener(view -> {
            if (mColor == null) {
                showMessageShort("Color is not selected");
                return;
            }
            if (onColorSelectedListener != null) {
                onColorSelectedListener.onColorSelected(mColor);
            }
            dismissAllowingStateLoss();
        });

        mBinding.etColorValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6 || s.length() == 8) {
                    try {
                        mColor = Color.parseColor("#" + s);
                        updateColor(mColor);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mBinding.etColorValue.setError("Wrong color value");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateColor(Integer color) {
        mBinding.colorPicker.setColor(color);
        mBinding.tvColorBg.setBackgroundColor(color);
    }

    public void setInitColor(Integer mColor) {
        this.mColor = mColor;
    }

    public void setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
        this.onColorSelectedListener = onColorSelectedListener;
    }

    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }
}
