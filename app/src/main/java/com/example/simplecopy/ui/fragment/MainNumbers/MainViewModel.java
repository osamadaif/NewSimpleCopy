package com.example.simplecopy.ui.fragment.MainNumbers;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.simplecopy.data.local.database.dao.NumbersDao;
import com.example.simplecopy.data.model.Numbers;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.SaveData;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = "MainViewModel";
    private NumbersDao numbersDao;
    private Numbers numbers;
    private MainRepository repository;
    private LiveData<List<Numbers>> loadAllNumbers;
    public LiveData<List<Numbers>> SearchNumbers;
    private LiveData<List<Numbers>> loadAllDaily;
    public LiveData<List<Numbers>> SearchDaily;
    public MutableLiveData<String> filterTextAll = new MutableLiveData<> ( );

    public MainViewModel(@NonNull Application application) {
        super (application);
        repository = new MainRepository (application);

        loadAllNumbers = repository.getAllNumbers ( );
        loadAllDaily = repository.getDailyFirst ( );
        SearchDaily = Transformations.switchMap (filterTextAll, input -> {
            if (input.isEmpty ( )) {
                return getAllDaily ( );
            }
            return repository.searchQueryByDaily (input);
        });

        loadAllNumbers = repository.getAllNumbers ( );
        SearchNumbers = Transformations.switchMap (filterTextAll, input -> {
            if (input.isEmpty ( )) {
                return getLoadAllNumbers ( );
            }
            return repository.searchQueryForNumbers (input);
        });
    }

    public LiveData<List<Numbers>> getSearchQueryForNumber() {
        return SearchNumbers;
    }

    public LiveData<List<Numbers>> getSearchQueryByDaily() {
        return SearchDaily;
    }

    public void setFilter(String filter) {
        filterTextAll.setValue (filter);
    }

    public LiveData<List<Numbers>> getLoadAllNumbers() {
        return loadAllNumbers;
    }

    public LiveData<List<Numbers>> getAllDaily() {
        return loadAllDaily;
    }

    public LiveData<Integer> getTotalOFDailyVM() {
        return repository.getTotalOFDailyRep ();
    }

    public Long insert(Numbers numbers) throws ExecutionException, InterruptedException {
        return repository.insert (numbers);
    }

    public void insertt(Numbers numbers) {
        repository.insertt (numbers);
    }

    public void update(Numbers numbers) {
        repository.update (numbers);
    }

    public void delete(Numbers numbers) {
        repository.delete (numbers);
    }

    public void deleteAllNumbers() {
        repository.deleteAllNumbers ( );
    }

}
