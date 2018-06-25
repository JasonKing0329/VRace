package com.king.app.vrace.page.adapter.leg;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/25 16:05
 */
public interface ComplexHolder {
    void notifyDataSetChanged();

    List<Object> getList();

    void notifyItemChanged(int selection);
}
