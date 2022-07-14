package com.example.xmlviewer.model;

public class XmlFile {
    private String name;
    private String id;
    private Boolean isSelected;

    public XmlFile(String name) {
        this.name = name;
        this.id = "";
        this.isSelected = false;
    }

    public XmlFile(String name, String id) {
        this.name = name;
        this.id = id;
        isSelected = false;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
