package com.king.app.vrace.model;

import android.text.TextUtils;

import com.king.app.vrace.conf.AppConfig;
import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.LegPlaces;

import java.io.File;
import java.io.FileFilter;
import java.util.Random;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/25 9:15
 */
public class ImageProvider {

    /**
     * city or country
     * @param country
     * @return
     */
    public static String getCountryFlagPath(String country) {
        String extras[] = new String[]{".jpg", ".jpeg", ".png", ".bmp", ".gif"};
        String result = null;
        for (String extra:extras) {
            String path = AppConfig.IMG_FLAG_BASE + "/" + country + extra;
            File file = new File(path);
            if (file.exists()) {
                result = path;
                break;
            }
        }
        if (result == null) {
            File file = getRandomFile(AppConfig.IMG_COUNTRY_BASE, country);
            if (file != null) {
                result = file.getPath();
            }
        }
        return result;
    }

    public static String getCountryImagePath(String country) {
        String result = null;
        File file = getRandomFile(AppConfig.IMG_COUNTRY_BASE, country);
        if (file != null) {
            result = file.getPath();
        }
        return result;
    }

    public static String getLegImagePath(Leg leg) {
        String path = null;
        if (leg.getPlaceList().size() > 0) {
            LegPlaces place = leg.getPlaceList().get(0);
            // city first
            File file = getRandomFile(AppConfig.IMG_CITY_BASE, place.getCity());
            if (file == null) {
                file = getRandomFile(AppConfig.IMG_COUNTRY_BASE, place.getCountry());
            }
            if (file != null) {
                path = file.getPath();
            }
        }
        return path;
    }

    private static File getRandomFile(String parent, String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        File target = null;
        // check folder
        File folder = new File(parent + "/" + name);
        if (folder.exists()) {
            File files[] = folder.listFiles(new ImageFilter());
            if (files.length > 0) {
                target = files[Math.abs(new Random().nextInt()) % files.length];
            }
        }
        return target;
    }

    public static class ImageFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            String extras[] = new String[]{".jpg", ".jpeg", ".png", ".bmp", ".gif"};
            for (String extra:extras) {
                if (file.getName().endsWith(extra)) {
                    return true;
                }
            }
            return false;
        }
    }
}
