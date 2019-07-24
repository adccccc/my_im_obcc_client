package com.example.administrator.mycc.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.administrator.mycc.model.BaseMessage;
import com.example.administrator.mycc.model.SingleMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: obc
 * @Date: 2019/3/19 12:53
 * @Version 1.0
 */
public class MessageDao extends SQLiteOpenHelper {

    private static final String DB_NAME = "messages.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME_SINGLE = "single_message";
    private static final String SQL_CREATE_TABLE_SINGLE = "create table if not exists "+ TABLE_NAME_SINGLE
            + " (_id integer primary key autoincrement,"
            + " message_id text unique not null, "
            + " username text not null,"
            + " f_id text not null,"
            + " time integer not null,"
            + " message_type int not null,"
            + " sender_id text not null,"
            + " content text not null);";

    private static volatile MessageDao instance = null;
    private static volatile SQLiteDatabase db = null;
    private static AtomicInteger dbCount = new AtomicInteger(0);

    public static MessageDao getInstance(Context context) {
        if (instance == null) {
            synchronized (MessageDao.class) {
                if (instance == null) {
                    instance = new MessageDao(context);
                }
            }
        }
        return instance;
    }

    private  MessageDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_SINGLE);
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

    public long insertSingleMessage(SingleMessage message) {
        SQLiteDatabase db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put("message_id", message.getMessageId());
        values.put("username", message.getUsername());
        values.put("f_id", message.getFriendUsername());
        values.put("time", message.getTimestamp());
        values.put("message_type", message.getMessageType());
        values.put("sender_id", message.getSenderId());
        values.put("content", message.getContent());

        long rid = db.insert(TABLE_NAME_SINGLE, null, values);
        return rid;
    }

    public List<BaseMessage> getSingleMessageList(String username, String friendUsername) {
        List<BaseMessage> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDB();
        String selection = "username = ? and f_id = ?";
        String[] selectionArgs = {username, friendUsername};
        Cursor cursor = db.query(TABLE_NAME_SINGLE, null, selection, selectionArgs, null, null, null);
        if (!cursor.moveToFirst()) {
            return list;
        }
        while (cursor.moveToNext()) {
            list.add(new SingleMessage(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getLong(4),
                    cursor.getInt(5),
                    cursor.getString(6),
                    cursor.getString(7)
            ));
        }
        cursor.close();
        closeDB();
        return list;
    }

}
