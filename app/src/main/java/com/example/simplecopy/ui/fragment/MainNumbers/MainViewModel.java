package com.example.simplecopy.ui.fragment.MainNumbers;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.simplecopy.data.model.Numbers;
import com.example.simplecopy.ui.fragment.MainNumbers.MainRepository;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private MainRepository repository;
    private LiveData<List<Numbers>> numbers;
    private LiveData<List<Numbers>> numbersDaily;

    public MainViewModel(@NonNull Application application) {
        super (application);

        repository = new MainRepository (application);
        numbers = repository.getAllNumbers ();
        numbersDaily = repository.getDailyFirst ();
    }

    public LiveData<List<Numbers>> searchQuery(String query){
        return repository.searchQuery (query);
    }

    public LiveData<List<Numbers>> searchQueryByDaily(String query){
        return repository.searchQueryByDaily (query);
    }

    public LiveData<List<Numbers>> getNumbers() {
        return numbers;
    }

    public LiveData<List<Numbers>> getDailyFirst() {
        return numbersDaily;
    }


    public void insert(Numbers numbers){
        repository.insert (numbers);
    }

    public void update(Numbers numbers){
        repository.update (numbers);
    }

    public void delete(Numbers numbers){
         repository.delete (numbers);
    }

    public void deleteAllNumbers(){
        repository.deleteAllNumbers ();
    }

}
