package com.example.simplecopy.ui.fragment.Notes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.simplecopy.data.model.NotesData;

import java.util.List;

public class NotesViewModel extends AndroidViewModel {

    private NotesRepository repository;
    private LiveData<List<NotesData>> notes;

    public NotesViewModel(@NonNull Application application) {
        super (application);

        repository = new NotesRepository (application);
        notes = repository.getAllNumbers ();
    }

    public LiveData<List<NotesData>> searchQuery(String query){
        return repository.searchQuery (query);
    }

    public LiveData<List<NotesData>> getNotes() {
        return notes;
    }

//    public int insertFavorite (int value, int id){
//        return repository.insertFavorite (value ,id);
//    }

    public void insert(NotesData notes){
        repository.insert (notes);
    }

    public void update(NotesData notes){
        repository.update (notes);
    }

    public void delete(NotesData notes){
         repository.delete (notes);
    }

    public void deleteAllNumbers(){
        repository.deleteAllNumbers ();
    }

}
