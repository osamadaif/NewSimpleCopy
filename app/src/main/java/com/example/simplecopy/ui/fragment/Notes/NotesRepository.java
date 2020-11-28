package com.example.simplecopy.ui.fragment.Notes;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.simplecopy.data.local.database.AppDatabase;
import com.example.simplecopy.data.local.database.dao.NotesDao;
import com.example.simplecopy.data.model.NotesData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class NotesRepository {
    private NotesDao notesDao;
    private LiveData<List<NotesData>> notes;

    public NotesRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance (context);
        notesDao = database.NotesDao ();
        notes = notesDao.loadAllTasks ();
    }


    public LiveData<List<NotesData>> getAllNotes(){
        return notes;
    }

    public LiveData<List<NotesData>> searchQueryForNotes(String query){
        return notesDao.searchFor ("%" + query + "%");
    }

    public long insert(NotesData notes) throws ExecutionException, InterruptedException{
        return new insertNoteAsyncTask (notesDao).execute (notes).get ();
    }

    public void insertt(NotesData notes) {
        new insertNoteAsyncTaskk (notesDao).execute (notes);
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



    private static class insertNoteAsyncTask extends AsyncTask <NotesData, Void, Long>{
        private NotesDao notesDao;

        public insertNoteAsyncTask(NotesDao notesDao) {
            this.notesDao = notesDao;
        }

        @Override
        protected Long doInBackground(NotesData... notes) {
            long id = notesDao.insertTask (notes[0]);
            return id;
        }
    }

    private static class insertNoteAsyncTaskk extends AsyncTask <NotesData, Void, Void>{
        private NotesDao notesDao;

        public insertNoteAsyncTaskk(NotesDao notesDao) {
            this.notesDao = notesDao;
        }

        @Override
        protected Void doInBackground(NotesData... notes) {
            notesDao.insertTaskk (notes[0]);
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
