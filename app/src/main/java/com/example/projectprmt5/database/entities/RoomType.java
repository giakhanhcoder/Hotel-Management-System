package com.example.projectprmt5.database.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "room_types",
        indices = {@Index(value = "name", unique = true)})
public class RoomType {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;

    public RoomType(String name) {
        this.name = name;
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
}
