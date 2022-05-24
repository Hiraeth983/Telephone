package com.example.telephone.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyContentProvider extends ContentProvider {

    // DbHelper
    public DbHelper mDbHelper;

    public static final int CONTACTS = 1;
    public static final int CONTACT = 2;
    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(Contact.CONTENT_AUTHORITY, Contact.PATH_CONTACTS, CONTACTS);
        uriMatcher.addURI(Contact.CONTENT_AUTHORITY, Contact.PATH_CONTACTS + "/#", CONTACT); // 代表列名的#
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
//        SQLiteDatabase db = mDbHelper.getWritableDatabase();
//        db.execSQL("delete from contact");
//        db.execSQL("insert into contact values(1,'Carson','17861579080','米哈游','浙江省杭州市','2022-05-18');");
//        db.execSQL("insert into contact values(2,'Kobe','17865579080','米哈游','浙江省杭州市','2022-05-19');");
        return true;
    }

    /**
     * @param uri      Table
     * @param strings  columns
     * @param s        where column = ?
     * @param strings1 value
     * @param s1       order by column
     * @return
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        // 查询获取只读
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // 初始化cursor
        Cursor cursor;
        Log.i("URI", uri + "");
        // 查询可能会查到多个结果或单个结果
        int match = uriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                cursor = db.query(Contact.ContactEntry.TABLE_NAME, strings, s, strings1, null, null, s1);
                break;

            case CONTACT:
                s = Contact.ContactEntry._ID + " = ?";
                strings1 = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(Contact.ContactEntry.TABLE_NAME, strings, s, strings1, null, null, s1);
                break;

            default:
                throw new IllegalArgumentException("Cant Query" + uri);
        }

        //
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        // 验证数据合法性（不为空）
        String name = contentValues.getAsString(Contact.ContactEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Name is required");
        }
        String telephone = contentValues.getAsString(Contact.ContactEntry.COLUMN_TELEPHONE);
        if (telephone == null) {
            throw new IllegalArgumentException("Telephone is required");
        }
        String workplace = contentValues.getAsString(Contact.ContactEntry.COLUMN_WORKPLACE);
        if (workplace == null) {
            throw new IllegalArgumentException("Workplace is required");
        }
        String address = contentValues.getAsString(Contact.ContactEntry.COLUMN_ADDRESS);
        if (address == null) {
            throw new IllegalArgumentException("Address is required");
        }
        String time = contentValues.getAsString(Contact.ContactEntry.COLUMN_TIME);
        if (time == null) {
            throw new IllegalArgumentException("Time is required");
        }

        // 插入获取可写的数据库
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(Contact.ContactEntry.TABLE_NAME, null, contentValues);

        // 插入失败报错
        if (id == -1) {
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * @param uri     table
     * @param s       where column = ?
     * @param strings value
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int result;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                result = db.delete(Contact.ContactEntry.TABLE_NAME, s, strings);
                break;

            case CONTACT:
                s = Contact.ContactEntry._ID + " = ?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                result = db.delete(Contact.ContactEntry.TABLE_NAME, s, strings);
                break;

            default:
                throw new IllegalArgumentException("Can't execute delete" + uri);
        }
        if (result != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    /**
     * @param uri           table
     * @param contentValues values
     * @param s             where column = ?
     * @param strings       value
     * @return
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        // 验证数据的合法性
        if (contentValues.containsKey(Contact.ContactEntry.COLUMN_NAME)) {
            String name = contentValues.getAsString(Contact.ContactEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Name is required");
            }
        }

        if (contentValues.containsKey(Contact.ContactEntry.COLUMN_TELEPHONE)) {
            String telephone = contentValues.getAsString(Contact.ContactEntry.COLUMN_TELEPHONE);
            if (telephone == null) {
                throw new IllegalArgumentException("Telephone is required");
            }
        }

        if (contentValues.containsKey(Contact.ContactEntry.COLUMN_WORKPLACE)) {
            String workplace = contentValues.getAsString(Contact.ContactEntry.COLUMN_WORKPLACE);
            if (workplace == null) {
                throw new IllegalArgumentException("Workplace is required");
            }
        }

        if (contentValues.containsKey(Contact.ContactEntry.COLUMN_WORKPLACE)) {
            String address = contentValues.getAsString(Contact.ContactEntry.COLUMN_WORKPLACE);
            if (address == null) {
                throw new IllegalArgumentException("Address is required");
            }
        }

        if (contentValues.containsKey(Contact.ContactEntry.COLUMN_WORKPLACE)) {
            String time = contentValues.getAsString(Contact.ContactEntry.COLUMN_WORKPLACE);
            if (time == null) {
                throw new IllegalArgumentException("Time is required");
            }
        }

        if (contentValues.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();


        int match = uriMatcher.match(uri);
        int result;
        switch (match) {
            case CONTACTS:
                result = database.update(Contact.ContactEntry.TABLE_NAME, contentValues, s, strings);
                if (result != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return result;
            case CONTACT:
                s = Contact.ContactEntry._ID + " = ?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                result = database.update(Contact.ContactEntry.TABLE_NAME, contentValues, s, strings);
                if (result != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return result;

            default:
                throw new IllegalArgumentException("Can't update the contact");
        }
    }
}
