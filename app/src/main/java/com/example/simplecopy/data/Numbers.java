package com.example.simplecopy.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "numbers")
public class Numbers {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private long number;
    private String note;
    private int favorite;

    @Ignore
    public Numbers(String title, long number, String note) {
        this.title = title;
        this.number = number;
        this.note = note;
    }

    public Numbers(int id, String title, long number, String note, int favorite) {
        this.id = id;
        this.title = title;
        this.number = number;
        this.note = note;
        this.favorite = favorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getNote() {
        return note;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public void setNote(String note) {
        this.note = note;
    }


}
