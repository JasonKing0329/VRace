package com.king.app.vrace.view.dialog.content;

import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseBindingAdapter;
import com.king.app.vrace.base.IFragmentHolder;
import com.king.app.vrace.conf.AppConfig;
import com.king.app.vrace.databinding.AdapterItemLoadfromBinding;
import com.king.app.vrace.databinding.FragmentDialogLoadFromBinding;
import com.king.app.vrace.utils.FileUtil;
import com.king.app.vrace.view.dialog.AlertDialogFragment;
import com.king.app.vrace.view.dialog.DraggableContentFragment;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/22 19:29
 */
public class LoadFromSelector extends DraggableContentFragment<FragmentDialogLoadFromBinding> {

    private List<File> list;

    private ItemAdapter itemAdapter;

    private OnDatabaseChangedListener onDatabaseChangedListener;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_dialog_load_from;
    }

    @Override
    protected void initView() {

        mBinding.tvConfirm.setOnClickListener(view -> onConfirm());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvList.setLayoutManager(manager);

        loadData();
    }

    private void loadData() {
        File file = new File(AppConfig.HISTORY_BASE);
        list = Arrays.asList(file.listFiles());

        itemAdapter = new ItemAdapter();
        itemAdapter.setList(list);
        mBinding.rvList.setAdapter(itemAdapter);
    }

    public void setOnDatabaseChangedListener(OnDatabaseChangedListener onDatabaseChangedListener) {
        this.onDatabaseChangedListener = onDatabaseChangedListener;
    }

    private void onConfirm() {
        if (itemAdapter.getSelection() != -1) {
            final File file = list.get(itemAdapter.getSelection());
            new AlertDialogFragment()
                    .setMessage("This operation will replace database of application with selected database, continue?")
                    .setPositiveText(getString(R.string.ok))
                    .setPositiveListener((dialogInterface, i) -> {
                        FileUtil.replaceDatabase(file);
                        dismissAllowingStateLoss();
                        if (onDatabaseChangedListener != null) {
                            onDatabaseChangedListener.onDatabaseChanged();
                        }
                    })
                    .setNegativeText(getString(R.string.cancel))
                    .show(getChildFragmentManager(), "AlertDialogFragment");
        }
    }

    private class ItemAdapter extends BaseBindingAdapter<AdapterItemLoadfromBinding, File> {

        private int selection = -1;

        @Override
        protected int getItemLayoutRes() {
            return R.layout.adapter_item_loadfrom;
        }

        @Override
        protected void onBindItem(AdapterItemLoadfromBinding binding, int position, File bean) {
            binding.tvName.setText(bean.getName());
            if (position == selection) {
                binding.getRoot().setBackgroundColor(getResources().getColor(R.color.blue_00a5c4));
            }
            else {
                binding.getRoot().setBackgroundColor(getResources().getColor(R.color.white));
            }
        }

        @Override
        protected void onClickItem(View v, int position) {
            int lastPosition = selection;
            selection = position;
            if (lastPosition != -1) {
                notifyItemChanged(lastPosition);
            }
            notifyItemChanged(selection);
        }

        public int getSelection() {
            return selection;
        }
    }

    public interface OnDatabaseChangedListener {
        void onDatabaseChanged();
    }
}
