package com.example.simplecopy.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "numbers")
public class Numbers {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String number;
    private String note;
    private int favorite;
    private int daily;
    private int done;


    @Ignore
    public Numbers(String title, String number, String note) {
        this.title = title;
        this.number = number;
        this.note = note;
    }

    @Ignore
    public Numbers(String title, String number, String note, int favorite, int done, int daily) {
        this.title = title;
        this.number = number;
        this.note = note;
        this.favorite = favorite;
        this.done = done;
        this.daily = daily;
    }

    @Ignore
    public Numbers(int done, int daily) {
        this.done = done;
        this.daily = daily;
    }

    public Numbers(int id, String title, String number, String note, int favorite) {
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
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

    public int getDaily() {
        return daily;
    }

    public void setDaily(int daily) {
        this.daily = daily;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }
}
