package com.example.simplecopy.ui.activity.NoteEditor;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.simplecopy.data.local.database.AppDatabase;

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
