package com.example.xmlviewer.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.xmlviewer.model.XmlFile;

@Database(entities = {XmlFile.class}, version = 1)
public abstract class XmlFileDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "xmlFile.db";
    public static XmlFileDatabase instance;

    public static synchronized XmlFileDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), XmlFileDatabase.class,
                    DATABASE_NAME).allowMainThreadQueries().build();
        }
        return instance;
    }

    public abstract XmlFileDAO xmlFileDAO();
}
