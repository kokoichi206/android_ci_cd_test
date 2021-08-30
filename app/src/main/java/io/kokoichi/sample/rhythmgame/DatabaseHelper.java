package io.kokoichi.sample.rhythmgame;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version!!
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RhythmGame.db";

    public static final String TABLE_NAME = "PlayRecords";
    public static final String _ID = "id";
    public static final String COLUMN_MUSIC_NAME = "name";
    public static final String COLUMN_COMBO = "combo";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DatabaseHelper.TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_MUSIC_NAME + " TEXT," +
                    COLUMN_COMBO + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
