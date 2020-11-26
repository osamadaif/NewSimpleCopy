package com.example.simplecopy.ui.fragment.Notes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.simplecopy.data.model.NotesData;
import com.example.simplecopy.data.model.Numbers;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class NotesViewModel extends AndroidViewModel {

    private NotesRepository repository;
    private LiveData<List<NotesData>> loadAllNotes;
    public LiveData<List<NotesData>> searchNotes;
    public MutableLiveData<String> filterTextAll = new MutableLiveData<>();

    public NotesViewModel(@NonNull Application application) {
        super (application);
        repository = new NotesRepository (application);
        loadAllNotes = repository.getAllNotes ();
        searchNotes = Transformations.switchMap (filterTextAll, input -> {
            if (input.isEmpty ()){
                return  getLoadAllNotes ();
            }
            return repository.searchQueryForNotes (input);});
    }

    public LiveData<List<NotesData>> getsearchQueryByNote(){
        return searchNotes;
    }

    public void setFilter(String filter){
        filterTextAll.setValue (filter);
    }

    public LiveData<List<NotesData>> getLoadAllNotes() {
        return loadAllNotes;
    }


    public Long insert(NotesData notes) throws ExecutionException, InterruptedException{
        return repository.insert (notes);
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
