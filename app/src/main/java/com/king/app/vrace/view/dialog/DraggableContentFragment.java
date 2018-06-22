package com.king.app.vrace.view.dialog;

import android.databinding.ViewDataBinding;

import com.king.app.vrace.base.BaseBindingFragment;
import com.king.app.vrace.view.dialog.DraggableHolder;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/7 10:05
 */
public abstract class DraggableContentFragment<T extends ViewDataBinding> extends BaseBindingFragment<T> {

    private DraggableHolder dialogHolder;

    public void setDialogHolder(DraggableHolder dialogHolder) {
        this.dialogHolder = dialogHolder;
    }

    protected void dismiss() {
        dialogHolder.dismiss();
    }

    protected void dismissAllowingStateLoss() {
        dialogHolder.dismissAllowingStateLoss();
    }
}
