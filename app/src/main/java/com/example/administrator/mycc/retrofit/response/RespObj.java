package com.example.administrator.mycc.retrofit.response;

/**
 * @Author: obc
 * @Date: 2019/3/11 19:10
 * @Version 1.0
 */

import java.util.HashMap;
import java.util.Map;

/**
 * http响应对象
 */
public class RespObj {

    private int code;
    private Map<String, Object> data;

    public RespObj(int code, Map<String, Object> data) {
        this.code = code;
        this.data = data;
    }

    public RespObj() {
        code = 0;
        data = new HashMap<>();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
