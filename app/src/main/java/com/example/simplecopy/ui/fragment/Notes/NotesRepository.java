package com.example.simplecopy.ui.fragment.Notes;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.simplecopy.data.local.database.AppDatabase;
import com.example.simplecopy.data.local.database.dao.NotesDao;
import com.example.simplecopy.data.model.NotesData;

import java.util.List;

public class NotesRepository {
    private NotesDao notesDao;
    private LiveData<List<NotesData>> notes;

    public NotesRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance (context);
        notesDao = database.NotesDao ();
        notes = notesDao.loadAllTasks ();
    }


    public LiveData<List<NotesData>> getAllNumbers(){
        return notes;
    }

    public LiveData<List<NotesData>> searchQuery(String query){
        return notesDao.searchFor ("%" + query + "%");
    }

    public void insert(NotesData notes){
        new insertNumberAsyncTask (notesDao).execute (notes);
    }


    public void update(NotesData notes){
        new updateNumberAsyncTask (notesDao).execute (notes);
    }

    public void delete(NotesData notes){
        new deleteNumberAsyncTask (notesDao).execute (notes);

    }

    public void deleteAllNumbers(){
        new deleteAllNumberAsyncTask (notesDao).execute ();
    }



    private static class insertNumberAsyncTask extends AsyncTask <NotesData, Void, Void>{
        private NotesDao notesDao;

        public insertNumberAsyncTask(NotesDao notesDao) {
            this.notesDao = notesDao;
        }

        @Override
        protected Void doInBackground(NotesData... notes) {
            notesDao.insertTask (notes[0]);
            return null;
        }
    }

    private static class updateNumberAsyncTask extends AsyncTask <NotesData, Void, Void>{
        private NotesDao notesDao;

        public updateNumberAsyncTask(NotesDao notesDao) {
            this.notesDao = notesDao;
        }

        @Override
        protected Void doInBackground(NotesData... notes) {
            notesDao.updateTask (notes[0]);
            return null;
        }
    }

    private static class deleteNumberAsyncTask extends AsyncTask<NotesData, Void, Void> {
        private NotesDao notesDao;

        public  deleteNumberAsyncTask(NotesDao notesDao) {
            this.notesDao = notesDao;
        }

        @Override
        protected Void doInBackground(NotesData... notes) {
            notesDao.deleteTask (notes[0]);
            return null;
        }
    }

    private static class deleteAllNumberAsyncTask extends AsyncTask <Void, Void, Void>{
        private NotesDao notesDao;

        public deleteAllNumberAsyncTask(NotesDao notesDao) {
            this.notesDao = notesDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            notesDao.deleteAllNotes ();
            return null;
        }
    }
}
