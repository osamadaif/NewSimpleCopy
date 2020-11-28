package com.example.simplecopy.ui.fragment.Notes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.simplecopy.data.model.NotesData;
import com.example.simplecopy.data.model.Numbers;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.simplecopy.utils.Constants.DAILY;
import static com.example.simplecopy.utils.Constants.DONE;
import static com.example.simplecopy.utils.Constants.FAVORITE;
import static com.example.simplecopy.utils.Constants.FAVORITE_NOTE;
import static com.example.simplecopy.utils.Constants.NOTE;
import static com.example.simplecopy.utils.Constants.NOTE_NOTE;
import static com.example.simplecopy.utils.Constants.NUMBER;
import static com.example.simplecopy.utils.Constants.TITLE;
import static com.example.simplecopy.utils.Constants.TITLE_NOTE;
import static com.example.simplecopy.utils.Constants.UID;
import static com.example.simplecopy.utils.Constants.UID_NOTE;

public class NotesViewModel extends AndroidViewModel {

    private NotesRepository repository;
    private NotesData notesData;
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

    public void insertt(NotesData notes) {
        repository.insertt (notes);
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
