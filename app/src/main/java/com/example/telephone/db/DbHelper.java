package com.example.telephone.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.telephone.db.Contact.ContactEntry;


public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "contact.db";
    public static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // 初始化数据库的表结构，执行一条建表的SQL语句
        String SQL_TABLE = "CREATE TABLE " + ContactEntry.TABLE_NAME + " ("
                + ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," // 主键自增
                + ContactEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + ContactEntry.COLUMN_TELEPHONE + " TEXT NOT NULL, "
                + ContactEntry.COLUMN_WORKPLACE + " TEXT NOT NULL, "
                + ContactEntry.COLUMN_ADDRESS + " TEXT NOT NULL, "
                + ContactEntry.COLUMN_TIME + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(SQL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
