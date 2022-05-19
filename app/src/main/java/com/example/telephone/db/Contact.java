package com.example.telephone.db;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Contact {

    public Contact() {

    }

    public static final String CONTENT_AUTHORITY = "com.example.telephone";

    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CONTACTS = "contact";

    public static abstract class ContactEntry implements BaseColumns {

        // content://com.example.telephone/contact
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_CONTACTS);

        public static final String TABLE_NAME = "contact";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TELEPHONE = "telephone";
        public static final String COLUMN_WORKPLACE = "workplace";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_TIME = "time";
    }
}
