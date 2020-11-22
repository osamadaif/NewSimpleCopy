package com.example.simplecopy.ui.fragment.MainNumbers;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.simplecopy.data.local.database.dao.NumbersDao;
import com.example.simplecopy.data.model.Numbers;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private NumbersDao numbersDao ;
    private MainRepository repository;
    private LiveData<List<Numbers>> loadAllNumbers;
    public LiveData<List<Numbers>> SearchNumbers;
    private LiveData<List<Numbers>> loadAllDaily;
    public LiveData<List<Numbers>> SearchDaily;
    public MutableLiveData<String> filterTextAll = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super (application);
        repository = new MainRepository (application);
        loadAllNumbers = repository.getAllNumbers ();
        loadAllDaily = repository.getDailyFirst ();
        SearchDaily = Transformations.switchMap (filterTextAll,input -> {
            if (input.isEmpty ()){
                return  getAllDaily ();
            }
           return repository.searchQueryByDaily (input);});

        loadAllNumbers = repository.getAllNumbers ();
        SearchNumbers = Transformations.switchMap (filterTextAll,input -> {
            if (input.isEmpty ()){
                return  getLoadAllNumbers ();
            }
            return repository.searchQueryForNumbers (input);});
    }

    public LiveData<List<Numbers>> getsearchQueryForNumber(){
        return SearchNumbers;
    }

    public LiveData<List<Numbers>> getsearchQueryByDaily(){
        return SearchDaily;
    }

    public void setFilter(String filter){
        filterTextAll.setValue (filter);
    }


    public LiveData<List<Numbers>> getLoadAllNumbers() {
        return loadAllNumbers;
    }

    public LiveData<List<Numbers>> getAllDaily() {
        return loadAllDaily;
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
