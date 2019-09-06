package com.example.simplecopy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.simplecopy.data.AppDatabase;
import com.example.simplecopy.data.Numbers;


public class EditorViewModel extends ViewModel {

    private LiveData<Numbers> numbers;

    public EditorViewModel(AppDatabase database, int numberId) {
        numbers = database.numbersDao ().loadNumberById (numberId);
    }

    public LiveData<Numbers> getNumbers() {
        return numbers;
    }
}
