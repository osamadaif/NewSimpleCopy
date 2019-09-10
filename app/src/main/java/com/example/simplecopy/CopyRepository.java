package com.example.simplecopy;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.simplecopy.data.AppDatabase;
import com.example.simplecopy.data.Numbers;
import com.example.simplecopy.data.NumbersDao;

import java.util.List;

public class CopyRepository {
    private NumbersDao numbersDao;
    private LiveData<List<Numbers>> numbers;

    public CopyRepository( Context context) {
        AppDatabase database = AppDatabase.getInstance (context);
        numbersDao = database.numbersDao ();
        numbers = numbersDao.loadAllTasks ();
    }

//    public int insertFavorite (int value, int id){
//       return numbersDao.insertFavorite (value, id);
//    }

    public LiveData<List<Numbers>> getAllNumbers(){
        return numbers;
    }

    public LiveData<List<Numbers>> searchQuery(String query){
        return numbersDao.searchFor ("%" + query + "%");
    }

    public void insert(Numbers numbers){
        new insertNumberAsyncTask (numbersDao).execute (numbers);
    }


    public void update(Numbers numbers){
        new updateNumberAsyncTask (numbersDao).execute (numbers);
    }

    public void delete(Numbers numbers){
        new deleteNumberAsyncTask (numbersDao).execute (numbers);

    }

    public void deleteAllNumbers(){
        new deleteAllNumberAsyncTask (numbersDao).execute ();
    }



    private static class insertNumberAsyncTask extends AsyncTask <Numbers, Void, Void>{
        private NumbersDao numbersDao;

        public insertNumberAsyncTask(NumbersDao numbersDao) {
            this.numbersDao = numbersDao;
        }

        @Override
        protected Void doInBackground(Numbers... numbers) {
            numbersDao.insertTask (numbers[0]);
            return null;
        }
    }

    private static class updateNumberAsyncTask extends AsyncTask <Numbers, Void, Void>{
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

        public  deleteNumberAsyncTask(NumbersDao numbersDao) {
            this.numbersDao = numbersDao;
        }

        @Override
        protected Void doInBackground(Numbers... numbers) {
             numbersDao.deleteTask (numbers[0]);
            return null;
        }
    }

    private static class deleteAllNumberAsyncTask extends AsyncTask <Void, Void, Void>{
        private NumbersDao numbersDao;

        public deleteAllNumberAsyncTask(NumbersDao numbersDao) {
            this.numbersDao = numbersDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            numbersDao.deleteAllNumbers ();
            return null;
        }
    }
}
