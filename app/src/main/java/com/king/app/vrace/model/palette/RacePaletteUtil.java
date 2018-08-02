package com.king.app.vrace.model.palette;

import android.support.v7.graphics.Palette;

import com.king.app.vrace.utils.ListUtil;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/2 16:24
 */
public class RacePaletteUtil {

    public static Palette.Swatch getDefaultSwatch(Palette palette) {
        if (palette == null) {
            return null;
        }
        // vibrant first
        Palette.Swatch swatch = palette.getVibrantSwatch();
        if (swatch == null) {
            // muted second
            swatch = palette.getMutedSwatch();
            if (swatch == null) {
                // then random one or nothing
                List<Palette.Swatch> swatches = palette.getSwatches();
                if (!ListUtil.isEmpty(swatches)) {
                    swatch = swatches.get(0);
                }
            }
        }
        return swatch;
    }
}
