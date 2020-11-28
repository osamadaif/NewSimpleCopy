package com.example.simplecopy.data.local.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.simplecopy.data.model.NotesData;

import java.util.List;

@Dao
public interface NotesDao {
    @Query("SELECT * FROM notes ORDER BY favorite DESC")
    LiveData<List<NotesData>> loadAllTasks();

    @Query ("SELECT * FROM notes WHERE title LIKE :searchQuery ORDER BY favorite DESC")
    LiveData<List<NotesData>> searchFor (String searchQuery);

    @Insert
    long insertTask(NotesData notesData);

    @Insert
    void insertTaskk(NotesData notesData);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(NotesData notesData);

    @Delete
    void deleteTask(NotesData notesData);

    @Query ("DELETE FROM notes")
    void deleteAllNotes();

    @Query ("DELETE FROM notes WHERE id IN (:ids)")
    void deleteItemByIds(List<Integer> ids);

//    @Query ("DELETE FROM notes WHERE id = :itemId")
//    void deleteItemById(int itemId);

    @Query ("SELECT * FROM notes WHERE id = :id")
    LiveData<NotesData> loadNoteById(int id);

    @Query("UPDATE notes SET favorite= :value WHERE id = :itemId")
    void insertFavorite(int value, int itemId);

    @Query("SELECT * FROM notes WHERE title = :userName")
    boolean isNameExist(String userName);

    @Query("SELECT * FROM notes WHERE id = :uid")
    boolean isIdExist(int uid);

}



