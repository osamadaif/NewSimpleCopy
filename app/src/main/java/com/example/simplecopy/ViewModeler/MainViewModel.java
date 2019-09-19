package com.example.simplecopy.ViewModeler;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.simplecopy.CopyRepository;
import com.example.simplecopy.data.Numbers;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private CopyRepository repository;
    private LiveData<List<Numbers>> numbers;
    private LiveData<List<Numbers>> numbersDaily;

    public MainViewModel(@NonNull Application application) {
        super (application);

        repository = new CopyRepository (application);
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
