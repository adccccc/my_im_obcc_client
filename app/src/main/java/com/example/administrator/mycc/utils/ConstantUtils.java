package com.example.administrator.mycc.utils;

/**
 * @Author: obc
 * @Date: 2019/3/17 22:16
 * @Version 1.0
 */
public class ConstantUtils {

    public static String HTTP_HOST = "192.168.31.166";

    public static int HTTP_PORT = 17080;

    public static String HTTP_BASE_URL = "http://" + HTTP_HOST + ":" + HTTP_PORT + "/";

    public static String TCP_HOST = "192.168.31.166";

    public static int TCP_PORT = 17081;

    // HTTP连接超时 (s)
    public static int HTTP_CONNECT_TIME_OUT = 5;

    // TCP连接超时 (ms)
    public static int TCP_CONNECT_TIME_OUT = 5000;

    // 心跳包间隔频率 (s)
    public static int HEART_BEAT_INTERVAL = 18;

}
