package com.example.administrator.mycc.dao;

/**
 * @Author: obc
 * @Date: 2019/3/22 14:38
 * @Version 1.0
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.administrator.mycc.model.ChatTitle;
import com.example.administrator.mycc.model.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 保存所有接收到的用户信息
 * 所有账户共用
 */
public class UserInfoDao extends SQLiteOpenHelper {

    private static final String DB_NAME = "users.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "user_info";
    private static final String SQL_CREATE_TABLE = "create table if not exists "+ TABLE_NAME
            + " (_id integer primary key autoincrement,"
            + " username text unique not null, "
            + " nickname text not null,"
            + " icon integer not null);";
    private static final String SQL_CREATE_INDEX = "create unique index if not exists `idx_username` on " + TABLE_NAME
            + "(username);";

    private static volatile UserInfoDao instance = null;
    private static volatile SQLiteDatabase db = null;
    private static AtomicInteger dbCount = new AtomicInteger(0);

    public static UserInfoDao getInstance(Context context) {
        if (instance == null) {
            synchronized (UserInfoDao.class) {
                if (instance == null) {
                    instance = new UserInfoDao(context);
                }
            }
        }
        return instance;
    }

    private  UserInfoDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_INDEX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * 插入UserInfo,已存在则更新
     * @param info
     * @return
     */
    public long insertOrUpdateUserInfo(UserInfo info) {
        SQLiteDatabase db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put("username", info.getUsername());
        values.put("nickname", info.getNickname());
        values.put("icon", info.getIcon());

        long rid = db.replace(TABLE_NAME, null, values);
        closeDB();
        return rid;
    }

    public List<UserInfo> getUserInfoList() {
        SQLiteDatabase db = getReadableDB();
        List<UserInfo> list = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (!cursor.moveToFirst()) return list;
        while (cursor.moveToNext()) {
            list.add(new UserInfo(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3)
                    )
            );
        }
        cursor.close();
        closeDB();
        return list;
    }


    private synchronized SQLiteDatabase getWritableDB() {
        if (dbCount.incrementAndGet() == 1) {
            db = getWritableDatabase();
        }
        return db;
    }

    private synchronized SQLiteDatabase getReadableDB() {
        if (dbCount.incrementAndGet() == 1) {
            db = getReadableDatabase();
        }
        return db;
    }

    private synchronized void closeDB() {
        if (dbCount.decrementAndGet() == 0) {
            db.close();
        }
    }
}
