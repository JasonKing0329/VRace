package com.king.app.vrace.model;

import android.text.TextUtils;

import com.king.app.vrace.conf.AppConfig;
import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.model.entity.LegPlaces;

import java.io.File;
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
     * @param place
     * @return
     */
    public static String getPlaceImagePath(String place) {
        File file = getFileByName(place);
        if (file != null) {
            return file.getPath();
        }
        return null;
    }

    public static String getLegImagePath(Leg leg) {
        String path = null;
        if (leg.getPlaceList().size() > 0) {
            LegPlaces place = leg.getPlaceList().get(0);
            // city first
            File file = getFileByName(place.getCity());
            if (file == null) {
                file = getFileByName(place.getCountry());
            }
            if (file != null) {
                path = file.getPath();
            }
        }
        return path;
    }

    private static File getFileByName(String city) {
        if (TextUtils.isEmpty(city)) {
            return null;
        }
        File target = null;
        // check folder
        File folder = new File(AppConfig.IMG_PLACE_BASE + "/" + city);
        if (folder.exists()) {
            File files[] = folder.listFiles();
            if (files.length > 0) {
                target = files[Math.abs(new Random().nextInt()) % files.length];
            }
        }
        return target;
    }
}
