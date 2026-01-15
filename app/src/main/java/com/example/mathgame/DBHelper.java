package com.example.mathgame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "MathGameDB";
    private static final int DB_VERSION = 1;
    private static final String TABLE_SCORE = "BestScore";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SCORE = "score";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_SCORE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SCORE + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORE);
        onCreate(db);
    }

    // Insert score if it's higher than current best
    public void updateBestScore(int score) {
        int best = getBestScore();
        if (score > best) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_SCORE, null, null); // clear old score
            ContentValues values = new ContentValues();
            values.put(COLUMN_SCORE, score);
            db.insert(TABLE_SCORE, null, values);
            db.close();
        }
    }

    // Get current best score
    public int getBestScore() {
        int best = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCORE + " ORDER BY " + COLUMN_SCORE + " DESC LIMIT 1", null);
        if (cursor.moveToFirst()) {
            best = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE));
        }
        cursor.close();
        db.close();
        return best;
    }
}
