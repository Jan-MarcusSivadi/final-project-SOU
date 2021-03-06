package com.example.workoutappgroupproject.UserDB;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private final String name;
    private final float height;
    private final float weight;
    private final int age;
    private final String units;

    public User(String name, float height, float weight, int age, String units) {
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.units = units;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getHeight() {
        return height;
    }

    public float getWeight() {
        return weight;
    }

    public int getAge() {
        return age;
    }

    public String getUnits() {
        return units;
    }
}
