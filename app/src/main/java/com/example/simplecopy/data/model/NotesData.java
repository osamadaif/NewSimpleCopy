package com.example.simplecopy.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class NotesData {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String note;
    private int favorite;

    @Ignore
    public NotesData(String title, String note) {
        this.title = title;
        this.note = note;
    }

    @Ignore
    public NotesData(String title, String note,int favorite) {
        this.title = title;
        this.note = note;
        this.favorite = favorite;
    }


    public NotesData(int id, String title, String note, int favorite) {
        this.id = id;
        this.title = title;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }
}


