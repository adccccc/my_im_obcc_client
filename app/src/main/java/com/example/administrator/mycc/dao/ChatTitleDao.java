package com.example.administrator.mycc.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.administrator.mycc.model.ChatTitle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: obc
 * @Date: 2019/3/18 13:43
 * @Version 1.0
 */
public class ChatTitleDao extends SQLiteOpenHelper {

    private static final String DB_NAME = "chats.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "chat_title";
    private static final String SQL_CREATE_TABLE = "create table if not exists "+ TABLE_NAME
            + " (_id integer primary key autoincrement,"
            + " username text not null,"
            + " chat_type int not null,"
            + " f_g_id text not null,"
            + " last_time integer not null,"
            + " content text not null,"
            + " msg_count int);";
    private static final String SQL_CREATE_INDEX = "create unique index if not exists `idx_username_f_g_id` on " + TABLE_NAME
            + "(username, f_g_id);";

    private static volatile ChatTitleDao instance = null;
    private static volatile SQLiteDatabase db = null;
    private static AtomicInteger dbCount = new AtomicInteger(0);

    public static ChatTitleDao getInstance(Context context) {
        if (instance == null) {
            synchronized (ChatTitleDao.class) {
                if (instance == null) {
                    instance = new ChatTitleDao(context);
                }
            }
        }
        return instance;
    }

    public ChatTitleDao(Context context) {
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

    public long insertOrUpdateChatTitle(String username, ChatTitle chatTitle) {
        SQLiteDatabase db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("chat_type", chatTitle.getChatType());
        values.put("f_g_id", chatTitle.getId());
        values.put("last_time", chatTitle.getTimestamp());
        values.put("content", chatTitle.getContent());
        values.put("msg_count", chatTitle.getMsgCount());

        long rid = db.replace(TABLE_NAME, null, values);
        closeDB();
        return rid;
    }


    /**
     * 批量更新(无则插入)
     * @param username
     * @param chatTitleList
     */
    public void insertOrUpdateChatTitleList(String username, List<ChatTitle> chatTitleList) {
        SQLiteDatabase db = getWritableDB();
        for (ChatTitle chatTitle : chatTitleList) {
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("chat_type", chatTitle.getChatType());
            values.put("f_g_id", chatTitle.getId());
            values.put("last_time", chatTitle.getTimestamp());
            values.put("content", chatTitle.getContent());
            values.put("msg_count", chatTitle.getMsgCount());
            db.replace(TABLE_NAME, null, values);
        }
        closeDB();
        return;
    }

    public List<ChatTitle> getAllChatTitles(String username) {
        List<ChatTitle> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDB();
        String selection = "username = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
        if (!cursor.moveToFirst()) return list;
        while (cursor.moveToNext()) {
            list.add(new ChatTitle(
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getLong(4),
                    cursor.getString(5),
                    cursor.getInt(6)
            ));
        }
        cursor.close();
        closeDB();
        return list;
    }

}
