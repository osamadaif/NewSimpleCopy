package com.example.simplecopy.ui.fragment.MainNumbers;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.simplecopy.data.local.database.AppDatabase;
import com.example.simplecopy.data.model.Numbers;
import com.example.simplecopy.data.local.database.dao.NumbersDao;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainRepository {
    private NumbersDao numbersDao;
    private LiveData<List<Numbers>> numbers;
    private LiveData<List<Numbers>> numbersDaily;

    public MainRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance (context);
        numbersDao = database.numbersDao ( );
        numbers = numbersDao.loadAllTasks ( );
        numbersDaily = numbersDao.loadAllByDaily ( );
    }

    public LiveData<List<Numbers>> getAllNumbers() {
        return numbers;
    }

    public LiveData<List<Numbers>> getDailyFirst() {
        return numbersDaily;
    }

    public LiveData<List<Numbers>> searchQueryForNumbers(String query) {
        return numbersDao.searchFor ("%" + query + "%");
    }

    public LiveData<List<Numbers>> searchQueryByDaily(String query) {
        return numbersDao.searchForByDaily ("%" + query + "%");
    }

    public long insert(Numbers numbers) throws ExecutionException, InterruptedException {
        return new insertNumberAsyncTask (numbersDao).execute (numbers).get ( );
    }

    public void insertt(Numbers numbers) {
        new insertNumberAsyncTaskk (numbersDao).execute (numbers);
    }


    public void update(Numbers numbers) {
        new updateNumberAsyncTask (numbersDao).execute (numbers);
    }

    public void delete(Numbers numbers) {
        new deleteNumberAsyncTask (numbersDao).execute (numbers);

    }

    public void deleteAllNumbers() {
        new deleteAllNumberAsyncTask (numbersDao).execute ( );
    }


    private static class insertNumberAsyncTask extends AsyncTask<Numbers, Void, Long> {
        private NumbersDao numbersDao;

        public insertNumberAsyncTask(NumbersDao numbersDao) {
            this.numbersDao = numbersDao;
        }

        @Override
        protected Long doInBackground(Numbers... numbers) {
            long id = numbersDao.insertTask (numbers[0]);
            return id;
        }
    }

    private static class insertNumberAsyncTaskk extends AsyncTask<Numbers, Void, Void> {
        private NumbersDao numbersDao;

        public insertNumberAsyncTaskk(NumbersDao numbersDao) {
            this.numbersDao = numbersDao;
        }

        @Override
        protected Void doInBackground(Numbers... numbers) {
            numbersDao.insertTaskk (numbers[0]);
            return null;
        }
    }

    private static class updateNumberAsyncTask extends AsyncTask<Numbers, Void, Void> {
        private NumbersDao numbersDao;

        public updateNumberAsyncTask(NumbersDao numbersDao) {
            this.numbersDao = numbersDao;
        }

        @Override
        protected Void doInBackground(Numbers... numbers) {
            numbersDao.updateTask (numbers[0]);
            return null;
        }
    }

    private static class deleteNumberAsyncTask extends AsyncTask<Numbers, Void, Void> {
        private NumbersDao numbersDao;

        public deleteNumberAsyncTask(NumbersDao numbersDao) {
            this.numbersDao = numbersDao;
        }

        @Override
        protected Void doInBackground(Numbers... numbers) {
            numbersDao.deleteTask (numbers[0]);
            return null;
        }
    }

    private static class deleteAllNumberAsyncTask extends AsyncTask<Void, Void, Void> {
        private NumbersDao numbersDao;

        public deleteAllNumberAsyncTask(NumbersDao numbersDao) {
            this.numbersDao = numbersDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            numbersDao.deleteAllNumbers ( );
            return null;
        }
    }
}
