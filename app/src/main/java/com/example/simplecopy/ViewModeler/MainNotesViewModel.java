package com.example.simplecopy.ViewModeler;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.simplecopy.CopyRepository;
import com.example.simplecopy.NotesRepository;
import com.example.simplecopy.data.NotesData;
import com.example.simplecopy.data.Numbers;

import java.util.List;

public class MainNotesViewModel extends AndroidViewModel {

    private NotesRepository repository;
    private LiveData<List<NotesData>> notes;

    public MainNotesViewModel(@NonNull Application application) {
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
