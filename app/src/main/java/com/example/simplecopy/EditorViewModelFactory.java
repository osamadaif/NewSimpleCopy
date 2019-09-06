package com.example.simplecopy;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.simplecopy.data.AppDatabase;

public class EditorViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mNumberId;

    public EditorViewModelFactory(AppDatabase database, int NumberId) {
        mDb = database;
        mNumberId = NumberId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new  EditorViewModel(mDb, mNumberId);
    }
}
