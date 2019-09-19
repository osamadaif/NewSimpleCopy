package com.example.simplecopy.ViewModeler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.simplecopy.data.AppDatabase;
import com.example.simplecopy.data.NotesData;


public class EditorNotesViewModel extends ViewModel {

    private LiveData<NotesData> notes;

    public EditorNotesViewModel(AppDatabase database, int notesId) {
        notes = database.NotesDao ().loadNoteById (notesId);
    }

    public LiveData<NotesData> getNotes() {
        return notes;
    }
}
