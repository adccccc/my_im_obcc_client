package com.example.administrator.mycc.utils;


import com.example.administrator.mycc.model.UserInfo;

import static android.content.Context.MODE_PRIVATE;

/**
 * @Author: obc
 * @Date: 2019/3/15 13:14
 * @Version 1.0
 */

public class SharedPreferenceUtils {

    public static int MODE = MODE_PRIVATE;

    public static final String PREFERENCE_NAME = "MyCC_AccountData";

    public static final String TOKEN_PREFIX = "Token_";

    public static final String LAST_USER_KEY = "LastUser";

    public static final String LAST_PASSWORD_KEY = "LastPassword";

}
