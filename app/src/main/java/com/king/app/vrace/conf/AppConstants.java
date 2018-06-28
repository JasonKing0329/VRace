package com.king.app.vrace.conf;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/22 18:41
 */
public class AppConstants {

    public static final int STAT_PLACE_GROUP_NONE = 0;

    public static final int STAT_PLACE_GROUP_BY_CONT = 1;

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
}
