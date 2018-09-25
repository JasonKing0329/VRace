package com.king.app.vrace.conf;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/23 15:46
 */
public class AppConfig {
    public static final String DB_NAME = "race.db";
    public static final String DB_CBS_NAME = "race_cbs.db";

    public static final String HTML_COUNTRY_ENG_CHN = "country_eng_chn.html";

    public static final String SDCARD = Environment.getExternalStorageDirectory().getPath();

    public static final String DEF_CONTENT = SDCARD + "/race";

    public static final String EXPORT_BASE = DEF_CONTENT + "/export";
    public static final String HISTORY_BASE = DEF_CONTENT + "/history";
    public static final String HTML_BASE = DEF_CONTENT + "/html";
    public static final String IMG_BASE = DEF_CONTENT + "/img";
    public static final String IMG_COUNTRY_BASE = IMG_BASE + "/country";
    public static final String IMG_CITY_BASE = IMG_BASE + "/city";
    public static final String IMG_FLAG_BASE = IMG_BASE + "/flag";

    public static final String FILE_HTML_CONTESTANTS = HTML_BASE + "/contestants.html";

    public static final String[] DIRS = new String[] {
            DEF_CONTENT, EXPORT_BASE, HISTORY_BASE, HTML_BASE, IMG_BASE, IMG_FLAG_BASE, IMG_COUNTRY_BASE, IMG_CITY_BASE
    };

    /**
     * 遍历程序所有图片目录，创建.nomedia文件
     */
    public static void createNoMedia() {
        File file = new File(IMG_BASE);
        createNoMedia(file);
    }

    /**
     * 遍历file下所有目录，创建.nomedia文件
     * @param file
     */
    public static void createNoMedia(File file) {
        File[] files = file.listFiles();
        for (File f:files) {
            if (f.isDirectory()) {
                createNoMedia(f);
            }
        }
        File nomediaFile = new File(file.getPath() + "/.nomedia");
        if (!nomediaFile.exists()) {
            try {
                new File(file.getPath(), ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
