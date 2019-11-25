package com.example.musicplayerproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DB_NAME = "musicDB";

    public MyDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    private int id;
    private String title;
    private String artist;
    private String genre;
    private int countClicked;
    private String albumArt;
    private String dataPath;
    private long duration;

    @Override
    public void onCreate(SQLiteDatabase db) {
        String str = "CREATE TABLE musicTBL (" +
                "title CHAR(60) PRIMARY KEY, singer CHAR(20), genre CHAR(20), countClicked Integer, albumArt CHAR(20));";
//                "id Integer PRIMARY KEY AUTOINCREMENT, title CHAR(60) PRIMARY KEY, singer CHAR(20), genre CHAR(20), countClicked Integer, albumArt CHAR(20));";
        db.execSQL(str);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS musicTBL");
        onCreate(db);
    }
}
