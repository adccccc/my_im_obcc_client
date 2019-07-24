package com.example.administrator.mycc.utils;

/**
 * @Author: obc
 * @Date: 2019/3/4 22:21
 * @Version 1.0
 */

import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * 消息id工具类
 */
public class MsgIdUtils {

    /**
     * 生成一个初始报文序号(客户端使用)
     */
    private static int randomNum = (int)(new Date().getTime() % 100000000);

    /**
     * 判断当前Msg是否为最新报文(服务端使用)
     * 取msgID前8位进行比较
     * @param lastId
     * @param curId
     * @return
     */
    public static boolean isLatest(String lastId, String curId) {
        String id1 = lastId.substring(0, 8);
        String id2 = curId.substring(0, 8);
        return id1.compareTo(id2) > 0;
    }

    /**
     * 生成ID格式: xxxxxxxx-UUID,前8位用于排序,后面部分随机生成
     * @return
     */
    public static String getUUID() {
        randomNum++;
        String preStr = String.valueOf(randomNum);
        StringBuilder builder = new StringBuilder();
        if (preStr.length() < 8) {
            for (int i = preStr.length(); i < 8; i++) {
                builder.append("0");
            }
        }
        builder.append(preStr).append("-").append(UUID.randomUUID().toString());

        return builder.toString();
    }
}
