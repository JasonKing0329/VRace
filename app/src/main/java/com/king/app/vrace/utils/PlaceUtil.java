package com.king.app.vrace.utils;

import android.text.TextUtils;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/11 13:56
 */
public class PlaceUtil {
    
    public static String getCombinePlace(String province, String city) {
        String result = "";
        if (TextUtils.isEmpty(province)) {
            if (!TextUtils.isEmpty(city)) {
                result = city;
            }
        }
        else {
            if (TextUtils.isEmpty(city)) {
                result = province;
            }
            else {
                if (city.equals(province)) {
                    result = city;
                }
                else {
                    result = province + "/" + city;
                }
            }
        }
        return result;
    }
}
