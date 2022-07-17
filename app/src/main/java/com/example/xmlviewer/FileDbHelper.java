package com.example.xmlviewer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileDbHelper extends SQLiteOpenHelper {
    private static final String TAG             = "FileDbHelper";
    private static final String DATABASE_NAME   = "myFile.db";
    private static final int DATABASE_VERSION   = 1;
    private static final String TABLE_FILE      = "file_location";

    public FileDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "Create table");
        String queryCreateTable = "CREATE TABLE " + TABLE_FILE +
                " ( id INTEGER PRIMARY KEY, name VARCHAR (255) NOT NULL )";
        sqLiteDatabase.execSQL(queryCreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FILE);
        onCreate(sqLiteDatabase);
    }

    public List<File> getAllFiles() {
        List<File> files = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name from product", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String fileId = cursor.getString(0);
            String fileName = cursor.getString(1);

            files.add(new File(fileId, fileName));
            cursor.moveToNext();
        }

        cursor.close();
        
        return files;
    }

    public String getFileNameById(String id) {
        String file = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name from file_location where id = ?",
                new String[]{id + ""});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            file = cursor.getString(1);
        }
        cursor.close();
        return file;
    }

    void updateFileById(String id, String fileName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE file_location SET name=? where id = ?",
                new String[]{fileName, id});
    }

    void insertFile(String id, String fileName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO file_location (id, name ) VALUES (?,?)",
                new String[]{id, fileName});
    }

    void deleteFileById(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM product where id = ?", new String[]{id});
    }
}
