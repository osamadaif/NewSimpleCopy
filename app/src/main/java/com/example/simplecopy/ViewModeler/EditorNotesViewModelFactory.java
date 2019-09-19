package com.example.simplecopy.ViewModeler;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.simplecopy.data.AppDatabase;

public class EditorNotesViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mNotesId;

    public EditorNotesViewModelFactory(AppDatabase database, int NotesId) {
        mDb = database;
        mNotesId = NotesId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EditorNotesViewModel (mDb, mNotesId);
    }
}
