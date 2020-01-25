package com.example.simplecopy.ui.activity.NumberEditor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.simplecopy.data.local.database.AppDatabase;
import com.example.simplecopy.data.model.Numbers;


public class EditorViewModel extends ViewModel {

    private LiveData<Numbers> numbers;

    public EditorViewModel(AppDatabase database, int numberId) {
        numbers = database.numbersDao ().loadNumberById (numberId);
    }

    public LiveData<Numbers> getNumbers() {
        return numbers;
    }
}
