package com.king.app.vrace.conf;

import android.os.Environment;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/23 15:46
 */
public class AppConfig {
    public static final String DB_NAME = "race.db";

    public static final String SDCARD = Environment.getExternalStorageDirectory().getPath();

    public static final String DEF_CONTENT = SDCARD + "/race";

    public static final String EXPORT_BASE = DEF_CONTENT + "/export";
    public static final String HISTORY_BASE = DEF_CONTENT + "/history";
    public static final String IMG_BASE = DEF_CONTENT + "/img";
    public static final String IMG_PLACE_BASE = IMG_BASE + "/place";

    public static final String[] DIRS = new String[] {
            DEF_CONTENT, EXPORT_BASE, HISTORY_BASE, IMG_BASE, IMG_PLACE_BASE
    };
}
