package com.example.administrator.mycc.utils;

import com.example.administrator.mycc.R;

/**
 * @Author: obc
 * @Date: 2019/3/15 16:45
 * @Version 1.0
 */
public class ImageUtils {

    public static int getIconId(int icon) {
        switch (icon) {
            case 1:
                return R.drawable.icon_1;
            case 2:
                return R.drawable.icon_2;
            case 3:
                return R.drawable.icon_3;
            case 4:
                return R.drawable.icon_4;
            case 5:
                return R.drawable.icon_5;
            case 6:
                return R.drawable.icon_6;
            case 7:
                return R.drawable.icon_7;
            case 8:
                return R.drawable.icon_8;
            default:
                return R.drawable.icon_default;
        }
    }
}
