package com.psyclone.fan.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.psyclone.fan.helpers.DateTimeHelper;
import com.psyclone.fan.helpers.ChannelHelper;

public class FanContentProvider extends ContentProvider {
    public static final String AUTHORITY_NAME = "com.psyclone.fan.provider";

    public static final String DB_NAME = "FanDB.sqlite";
    public static final int DB_VERSION = 1;

    public static final String TABLE_TV_GUIDE = "tvguide";
    public static final String URI_TV_GUIDE = "content://com.psyclone.fan.provider/tvguide";

    public static final String COL_TV_GUIDE_ID = "_id";
    public static final String COL_TV_GUIDE_CHANNEL_NAME = "channel_name";
    public static final String COL_TV_GUIDE_CHANNEL_CODE = "channel_code";
    public static final String COL_TV_GUIDE_CHANNEL_LOGO = "channel_logo";
    public static final String COL_TV_GUIDE_TODAY_DATE = "today_date";
    public static final String COL_TV_GUIDE_TODAY_GUIDE = "today_guide";
    public static final String COL_TV_GUIDE_TOMORROW_DATE = "tomorrow_date";
    public static final String COL_TV_GUIDE_TOMORROW_GUIDE = "tomorrow_guide";

    private static final String CREATE_TABLE_TV_GUIDE_QUERY = String.format
            ("CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT NOT NULL, %s INTEGER NOT NULL, %s TEXT NOT NULL, %s TEXT, %s TEXT, %s TEXT, %s TEXT);",
                    TABLE_TV_GUIDE, COL_TV_GUIDE_ID, COL_TV_GUIDE_CHANNEL_NAME, COL_TV_GUIDE_CHANNEL_CODE, COL_TV_GUIDE_CHANNEL_LOGO, COL_TV_GUIDE_TODAY_DATE, COL_TV_GUIDE_TODAY_GUIDE, COL_TV_GUIDE_TOMORROW_DATE, COL_TV_GUIDE_TOMORROW_GUIDE);


    public static final String TABLE_MY_SHOWS = "myshows";
    public static final String URI_MY_SHOWS = "content://com.psyclone.fan.provider/myshows";

    public static final String COL_MY_SHOWS_ID = "_id";
    public static final String COL_MY_SHOWS_CHANNEL = "channel";
    public static final String COL_MY_SHOWS_TIME = "time";
    public static final String COL_MY_SHOWS_DETAILS = "details";

    private static final String CREATE_TABLE_MY_SHOWS_QUERY = String.format
            ("CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL);",
                    TABLE_MY_SHOWS, COL_MY_SHOWS_ID, COL_MY_SHOWS_CHANNEL, COL_MY_SHOWS_TIME, COL_MY_SHOWS_DETAILS);

    private static final int CODE_TV_GUIDE_MATCH = 1;
    private static final int CODE_MY_SHOWS_MATCH = 2;

    private SQLiteDatabase db;
    private UriMatcher matcher;

    @Override
    public boolean onCreate() {
        FanDBConnectionHelper helper = new FanDBConnectionHelper(getContext());
        db = helper.getWritableDatabase();

        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY_NAME, TABLE_TV_GUIDE, CODE_TV_GUIDE_MATCH);
        matcher.addURI(AUTHORITY_NAME, TABLE_MY_SHOWS, CODE_MY_SHOWS_MATCH);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch(matcher.match(uri)) {
            case CODE_TV_GUIDE_MATCH:
                return db.query(TABLE_TV_GUIDE, projection, selection, selectionArgs, null, null, sortOrder);
            case CODE_MY_SHOWS_MATCH:
                return db.query(TABLE_MY_SHOWS, projection, selection, selectionArgs, null, null, sortOrder);
        }

        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri)
    {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch(matcher.match(uri)) {
            case CODE_TV_GUIDE_MATCH:
                if(db.insert(TABLE_TV_GUIDE, null, values) == -1)
                    throw new RuntimeException("Error while writing to database");
                break;
            case CODE_MY_SHOWS_MATCH:
                if(db.insert(TABLE_MY_SHOWS, null, values) == -1)
                    throw new RuntimeException("Error while writing to database");
                break;
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch(matcher.match(uri)) {
            case CODE_TV_GUIDE_MATCH:
                return db.delete(TABLE_TV_GUIDE, selection, selectionArgs);
            case CODE_MY_SHOWS_MATCH:
                return db.delete(TABLE_MY_SHOWS, selection, selectionArgs);
        }

        return -1;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch(matcher.match(uri)) {
            case CODE_TV_GUIDE_MATCH:
                return db.update(TABLE_TV_GUIDE, values, selection, selectionArgs);
            case CODE_MY_SHOWS_MATCH:
                return db.update(TABLE_MY_SHOWS, values, selection, selectionArgs);
        }

        return -1;
    }

    class FanDBConnectionHelper extends SQLiteOpenHelper {
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_TV_GUIDE_QUERY);
            db.execSQL(CREATE_TABLE_MY_SHOWS_QUERY);

            ContentValues cv = new ContentValues();

            for(int i=0; i<ChannelHelper.channels.length; i++) {
                cv.put(COL_TV_GUIDE_CHANNEL_NAME, ChannelHelper.channels[i].getName());
                cv.put(COL_TV_GUIDE_CHANNEL_CODE, ChannelHelper.channels[i].getCode());
                cv.put(COL_TV_GUIDE_CHANNEL_LOGO, ChannelHelper.channels[i].getLogo());
                db.insert(TABLE_TV_GUIDE, null, cv);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

        FanDBConnectionHelper(Context context)
        {
            super(context, DB_NAME, null, DB_VERSION);
        }
    }
}
