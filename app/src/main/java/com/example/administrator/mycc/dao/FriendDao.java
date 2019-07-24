package com.example.administrator.mycc.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.administrator.mycc.model.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: obc
 * @Date: 2019/3/17 11:36
 * @Version 1.0
 */

public class FriendDao extends SQLiteOpenHelper {

    private static final String DB_NAME = "friends.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "friend";
    private static final String SQL_CREATE_TABLE = "create table if not exists "+ TABLE_NAME
            + " (_id integer primary key autoincrement,"
            + " username text not null,"
            + " f_username text not null);";
    private static final String SQL_CREATE_INDEX = "create unique index if not exists `idx_uid_fid` on " + TABLE_NAME
            + "(username, f_username);";


    private static volatile FriendDao instance = null;
    private static volatile SQLiteDatabase db = null;
    private static AtomicInteger dbCount = new AtomicInteger(0);

    public static FriendDao getInstance(Context context) {
        if (instance == null) {
            synchronized (FriendDao.class) {
                if (instance == null) {
                    instance = new FriendDao(context);
                }
            }
        }
        return instance;
    }

    private FriendDao(Context context) {
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

    public synchronized SQLiteDatabase getWritableDB() {
        if (dbCount.incrementAndGet() == 1) {
            db = getWritableDatabase();
        }
        return db;
    }

    public synchronized SQLiteDatabase getReadableDB() {
        if (dbCount.incrementAndGet() == 1) {
            db = getReadableDatabase();
        }
        return db;
    }

    public synchronized void closeDB() {
        if (dbCount.decrementAndGet() == 0) {
            db.close();
        }
    }

    public long insertFriend(String username, String friend) {
        SQLiteDatabase db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("f_username", friend);

         long rid = db.insert(TABLE_NAME, null, values);
        closeDB();
        return rid;
    }

    public int deleteFriendByUsername(String username, String friend) {
        SQLiteDatabase db = getWritableDB();
        String whereClause = "username = ? and f_username = ?";
        String[] whereArgs = {username,friend};
        int row = db.delete(TABLE_NAME, whereClause, whereArgs);
        closeDB();
        return row;
    }

    public List<String> getAllFriends(String username) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDB();
        String selection = "username = ?";
        String[] selectionArgs = {username};
        Cursor c = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
        if (!c.moveToFirst()) return list;
        while(c.moveToNext()) {
            list.add(c.getString(2));
        }
        c.close();
        closeDB();
        return list;
    }

}
