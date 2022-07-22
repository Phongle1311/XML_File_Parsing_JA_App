package com.example.xmlviewer.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.xmlviewer.model.XmlFile;

import java.util.List;

@Dao
public interface XmlFileDAO {
    @Insert
    void insertFile(XmlFile file);

    @Query("SELECT * FROM xmlFile")
    List<XmlFile> getAllFiles();

    @Query("SELECT * FROM xmlFile WHERE instanceId = :instanceId")
    List<XmlFile> getByInstanceId(String instanceId);

    @Query("UPDATE xmlFile SET name = :name WHERE instanceId = :instanceId")
    void updateByInstanceId(String instanceId, String name);
}
