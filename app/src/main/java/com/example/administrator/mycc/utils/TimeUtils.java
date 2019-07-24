package com.example.administrator.mycc.utils;

/**
 * @Author: obc
 * @Date: 2019/3/18 14:30
 * @Version 1.0
 */

import java.util.Date;

/**
 * 时间格式各种转换
 */
public class TimeUtils {

    /**
     * timestamp --> 今天 15:00
     * @param timestamp
     * @return
     */
    public static String timestampToChatTitleTime(long timestamp) {
        Date date = new Date(timestamp);
        Date nowDate = new Date();

        int today = nowDate.getDay();
        int msgDay = date.getDay();
        if (today == msgDay) {
            return "今天 "+ date.getHours() + ":" + date.getMinutes();
        } else if (today - msgDay == 1) {
            return "昨天 "+ date.getHours() + ":" + date.getMinutes();
        } else if (today - msgDay == 2) {
            return "前天 "+ date.getHours() + ":" + date.getMinutes();
        } else  {
            return (today - msgDay) + "天前";
        }
    }

    public static String timestampToMessageTime(long timestamp) {
        Date date = new Date(timestamp);
        Date nowDate = new Date();

        int today = nowDate.getDay();
        int msgDay = date.getDay();
        if (today == msgDay) {
            return date.getHours() + ":" + date.getMinutes();
        } else if (today - msgDay == 1) {
            return "昨天 "+ date.getHours() + ":" + date.getMinutes();
        } else if (today - msgDay == 2) {
            return "前天 "+ date.getHours() + ":" + date.getMinutes();
        } else  {
            return date.getYear() + "年" + date.getMonth() + "月" + date.getDay() + "日 "
                    + date.getHours() + ":" + date.getMinutes();
        }
    }
}
