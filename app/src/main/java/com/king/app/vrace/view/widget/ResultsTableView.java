package com.king.app.vrace.view.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.king.app.vrace.utils.ScreenUtils;

import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/6/26 0026 20:03
 */

public class ResultsTableView extends LinearLayout {

    private LinearLayout scrollRoot;

    private Builder builder;

    private int rowHeight = ScreenUtils.dp2px(20);

    private int titleHeight = ScreenUtils.dp2px(60);

    private int cellWidth = ScreenUtils.dp2px(40);

    public ResultsTableView(Context context) {
        super(context);
        init();
    }

    public ResultsTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);

        builder = new Builder();
    }

    public Builder getBuilder() {
        return builder;
    }

    public class Builder {
        private List<CellData> legTitleList;
        private List<CellData> teamList;
        private List<CellData> relationshipList;
        private CellData[][] legResults;
        private int columnTeamColor;
        private int titleBgColor;

        public Builder setLegResults(CellData[][] legResults) {
            this.legResults = legResults;
            return this;
        }

        public Builder setLegTitleList(List<CellData> legTitleList) {
            this.legTitleList = legTitleList;
            return this;
        }

        public Builder setTeamList(List<CellData> teamList) {
            this.teamList = teamList;
            return this;
        }

        public Builder setRelationshipList(List<CellData> relationshipList) {
            this.relationshipList = relationshipList;
            return this;
        }

        public Builder setColumnTeamColor(int columnTeamColor) {
            this.columnTeamColor = columnTeamColor;
            return this;
        }

        public List<CellData> getLegTitleList() {
            return legTitleList;
        }

        public List<CellData> getTeamList() {
            return teamList;
        }

        public List<CellData> getRelationshipList() {
            return relationshipList;
        }

        public CellData[][] getLegResults() {
            return legResults;
        }

        public int getColumnTeamColor() {
            return columnTeamColor;
        }

        public void build() {
            createTable();
        }

        public int getTitleBgColor() {
            return titleBgColor;
        }

        public Builder setTitleBgColor(int titleBgColor) {
            this.titleBgColor = titleBgColor;
            return this;
        }
    }

    private void createTable() {
        removeAllViews();
        addTeamColumn();
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        HorizontalScrollView scrollView = new HorizontalScrollView(getContext());
        scrollView.setHorizontalScrollBarEnabled(false);
        addView(scrollView, params);

        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        scrollRoot = new LinearLayout(getContext());
        scrollRoot.setOrientation(LinearLayout.HORIZONTAL);
        scrollView.addView(scrollRoot, params);

        addRelationshipColumn();
        addResultsColumns();
    }

    private void addTeamColumn() {
        LinearLayout llTeam = new LinearLayout(getContext());
        llTeam.setOrientation(LinearLayout.VERTICAL);
        llTeam.setBackgroundColor(getBuilder().getColumnTeamColor());
        llTeam.setPadding(ScreenUtils.dp2px(10), 0, ScreenUtils.dp2px(10), 0);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(llTeam, params);

        TextView team = new TextView(getContext());
        team.setGravity(Gravity.CENTER);
        team.setText("Team");
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, titleHeight);
        llTeam.addView(team, params);

        for (int i = 0; i < builder.getTeamList().size(); i ++) {
            team = new TextView(getContext());
            team.setGravity(Gravity.CENTER);
            team.setText(builder.getTeamList().get(i).text);
            if (builder.getTeamList().get(i).background != 0) {
                team.setBackgroundColor(builder.getTeamList().get(i).background);
            }
            team.setTextColor(builder.getTeamList().get(i).textColor);
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, rowHeight);
            llTeam.addView(team, params);

        }
    }

    private void addRelationshipColumn() {
        LinearLayout llTeam = new LinearLayout(getContext());
        llTeam.setOrientation(LinearLayout.VERTICAL);
        llTeam.setPadding(ScreenUtils.dp2px(10), 0, ScreenUtils.dp2px(10), 0);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        scrollRoot.addView(llTeam, params);

        TextView team = new TextView(getContext());
        team.setGravity(Gravity.CENTER);
        team.setText("Relationship");
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, titleHeight);
        llTeam.addView(team, params);

        for (int i = 0; i < builder.getRelationshipList().size(); i ++) {
            team = new TextView(getContext());
            team.setGravity(Gravity.CENTER);
            team.setText(builder.getRelationshipList().get(i).text);
            team.setTextColor(builder.getRelationshipList().get(i).textColor);
            if (builder.getRelationshipList().get(i).background != 0) {
                team.setBackgroundColor(builder.getRelationshipList().get(i).background);
            }
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, rowHeight);
            llTeam.addView(team, params);

        }
    }

    private void addResultsColumns() {
        for (int col = 0; col < builder.getLegTitleList().size(); col ++) {
            LinearLayout llTeam = new LinearLayout(getContext());
            llTeam.setOrientation(LinearLayout.VERTICAL);
            LayoutParams params = new LayoutParams(cellWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            scrollRoot.addView(llTeam, params);

            TextView view = new TextView(getContext());
            view.setMaxLines(3);
            view.setEllipsize(TextUtils.TruncateAt.END);
            view.setGravity(Gravity.CENTER);
            view.setText(builder.getLegTitleList().get(col).text);
            view.setTextColor(builder.getLegTitleList().get(col).textColor);
            view.setBackgroundColor(builder.getLegTitleList().get(col).background);
            params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, titleHeight);
            llTeam.addView(view, params);

            for (int row = 0; row < builder.getRelationshipList().size(); row ++) {
                view = new TextView(getContext());
                view.setGravity(Gravity.CENTER);
                if (builder.getLegResults()[row][col] == null) {
                    view.setBackgroundColor(Color.parseColor("#a9a9a9"));
                }
                else {
                    view.setBackgroundColor(Color.WHITE);
                    view.setText(builder.getLegResults()[row][col].text);
                    view.setTextColor(builder.getLegResults()[row][col].textColor);
                    view.setBackgroundColor(builder.getLegResults()[row][col].background);
                }
                params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, rowHeight);
                llTeam.addView(view, params);

            }
        }
    }

    public static class CellData {
        public String text;
        public int textColor;
        public int background;

        public CellData() {
        }

        public CellData(String text, int textColor, int background) {
            this.text = text;
            this.textColor = textColor;
            this.background = background;
        }

    }
}
