package com.king.app.vrace.conf;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/22 18:41
 */
public class AppConstants {

    public static final int DATABASE_VIRTUAL = 0;

    public static final int DATABASE_REAL = 1;

    public static final int MAP_ITEM_PATH = 0;

    public static final int MAP_ITEM_BG = 1;

    public static final long MAP_ID_WORLD = 1001;

    public static final String MAP_ID_WORLD_NAME = "World Map";

    public static final int STAT_PLACE_GROUP_NONE = 0;

    public static final int STAT_PLACE_GROUP_BY_CONT = 1;

    public static final int STAT_PLACE_GROUP_BY_SEASON = 2;

    public static final int STAT_LOCAL_DEPART = 0;

    public static final int STAT_LOCAL_FINAL = 1;

    public static final int TEAM_SORT_NONE = 0;

    public static final int TEAM_SORT_SEASON = 1;

    public static final int TEAM_SORT_POINT = 2;

    public static final int TEAM_SORT_CHAMPIONS = 3;

    public static final int TEAM_SORT_PROVINCE = 4;

    public static String getGenderText(GenderType type) {
        switch (type) {
            case MW:
                return "男女";
            case WW:
                return "女女";
            default:
                return "男男";
        }
    }

    public static final String CONTESTANTS_URL = "https://en.m.wikipedia.org/wiki/List_of_The_Amazing_Race_(U.S._TV_series)_contestants";

}
