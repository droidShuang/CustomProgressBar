package com.junova.customprogressbar;

import android.content.res.Resources;

/**
 * Created by junova on 2016/12/6 0006.
 */

public class Util {
    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
