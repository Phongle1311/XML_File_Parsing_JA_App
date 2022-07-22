package com.example.xmlviewer.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "xmlFile")
public class XmlFile {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String instanceId;
    private Boolean isSelected;

    public XmlFile(String name, String instanceId) {
        this.name = name;
        this.instanceId = instanceId;
        isSelected = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
