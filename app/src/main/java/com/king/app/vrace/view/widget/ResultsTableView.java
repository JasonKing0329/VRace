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

    private int titleHeight = ScreenUtils.dp2px(40);

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
        private List<String> legTitleList;
        private List<String> teamList;
        private List<String> relationshipList;
        private String[][] legResults;

        public Builder setLegResults(String[][] legResults) {
            this.legResults = legResults;
            return this;
        }

        public Builder setLegTitleList(List<String> legTitleList) {
            this.legTitleList = legTitleList;
            return this;
        }

        public Builder setTeamList(List<String> teamList) {
            this.teamList = teamList;
            return this;
        }

        public Builder setRelationshipList(List<String> relationshipList) {
            this.relationshipList = relationshipList;
            return this;
        }

        public List<String> getLegTitleList() {
            return legTitleList;
        }

        public List<String> getTeamList() {
            return teamList;
        }

        public List<String> getRelationshipList() {
            return relationshipList;
        }

        public String[][] getLegResults() {
            return legResults;
        }

        public void build() {
            createTable();
        }
    }

    private void createTable() {
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
            team.setText(builder.getTeamList().get(i));
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
            team.setText(builder.getRelationshipList().get(i));
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

            TextView team = new TextView(getContext());
            team.setGravity(Gravity.CENTER);
            team.setText(builder.getLegTitleList().get(col));
            params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, titleHeight);
            llTeam.addView(team, params);

            for (int row = 0; row < builder.getRelationshipList().size(); row ++) {
                team = new TextView(getContext());
                team.setGravity(Gravity.CENTER);
                if (TextUtils.isEmpty(builder.getLegResults()[row][col])) {
                    team.setBackgroundColor(Color.parseColor("#a9a9a9"));
                }
                else {
                    team.setBackgroundColor(Color.WHITE);
                    team.setText(builder.getLegResults()[row][col]);
                }
                params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, rowHeight);
                llTeam.addView(team, params);

            }
        }
    }
}
