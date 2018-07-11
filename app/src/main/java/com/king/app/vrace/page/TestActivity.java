package com.king.app.vrace.page;

import com.king.app.vrace.R;
import com.king.app.vrace.base.BaseViewModel;
import com.king.app.vrace.base.MvvmActivity;
import com.king.app.vrace.databinding.ActivityTestBinding;
import com.king.app.vrace.view.widget.RankChartAdapter;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/3 16:39
 */
public class TestActivity extends MvvmActivity<ActivityTestBinding, BaseViewModel> {

    private int[] lineColors;

    private Integer[][] lineValues = new Integer[][]{
            {null, 1, 10, 3, 5, 1, 7, 5, 3, 5, 3, 2, 1},
            {null, 2, 6, 4, 8, 3, 3, 1, 1, 2, 2, 1, 3}
    };

    @Override
    protected BaseViewModel createViewModel() {
        return null;
    }

    @Override
    protected void initData() {
        lineColors = new int[] {
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary)
        };
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_test;
    }

    @Override
    protected void initView() {
        mBinding.chartScroll.setDrawDashGrid(true);
        mBinding.chartScroll.setAdapter(new RankChartAdapter() {
            @Override
            public int getXAxisCount() {
                return 13;
            }

            @Override
            public String getXAxisName(int position) {
                return "L" + position;
            }

            @Override
            public int getYAxisCount() {
                return 13;
            }

            @Override
            public String getYAxisName(int position) {
                return String.valueOf(13 - position);
            }

            @Override
            public Integer getYAxisValue(int i) {
                if (i == 0) {
                    return null;
                }
                else {
                    return 13 - i;
                }
            }

            @Override
            public int getLineCount() {
                return lineColors.length;
            }

            @Override
            public int getLineColor(int position) {
                return lineColors[position];
            }

            @Override
            public String getText(int lineIndex, int xIndex) {
                if (xIndex == 12) {
                    return "Line " + lineIndex;
                }
                return null;
            }

            @Override
            public Integer getValue(int lineIndex, int xIndex) {
                return lineValues[lineIndex][xIndex];
            }
        });


        mBinding.chart.setDrawDashGrid(true);
        mBinding.chart.setAdapter(new RankChartAdapter() {
            @Override
            public int getXAxisCount() {
                return 13;
            }

            @Override
            public String getXAxisName(int position) {
                return "L" + position;
            }

            @Override
            public int getYAxisCount() {
                return 13;
            }

            @Override
            public String getYAxisName(int position) {
                return String.valueOf(13 - position);
            }

            @Override
            public Integer getYAxisValue(int i) {
                if (i == 0) {
                    return null;
                }
                else {
                    return 13 - i;
                }
            }

            @Override
            public int getLineCount() {
                return lineColors.length;
            }

            @Override
            public int getLineColor(int position) {
                return lineColors[position];
            }

            @Override
            public String getText(int lineIndex, int xIndex) {
                if (xIndex == 12) {
                    return "Line " + lineIndex;
                }
                return null;
            }

            @Override
            public Integer getValue(int lineIndex, int xIndex) {
                return lineValues[lineIndex][xIndex];
            }
        });
    }
}
