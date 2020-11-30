package com.example.simplecopy.ui.activity.NoteEditor;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.simplecopy.App;
import com.example.simplecopy.data.local.prefs.SharedPreferencesManger;
import com.example.simplecopy.ui.activity.MainActivity;
import com.example.simplecopy.ui.activity.NumberEditor.NumberEditorActivity;
import com.example.simplecopy.ui.fragment.MainNumbers.MainViewModel;
import com.example.simplecopy.ui.fragment.Notes.NotesViewModel;
import com.example.simplecopy.utils.AppExecutors;
import com.example.simplecopy.R;
import com.example.simplecopy.data.local.database.AppDatabase;
import com.example.simplecopy.data.model.NotesData;
import com.example.simplecopy.utils.Constants;
import com.example.simplecopy.utils.HelperMethods;
import com.example.simplecopy.utils.TextViewUndoRedo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.LoadBoolean;
import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.LoadData;
import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.SaveData;
import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.USER_ID;
import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.USER_NAME;
import static com.example.simplecopy.utils.Constants.DAILY;
import static com.example.simplecopy.utils.Constants.DONE;
import static com.example.simplecopy.utils.Constants.FAVORITE;
import static com.example.simplecopy.utils.Constants.FAVORITE_NOTE;
import static com.example.simplecopy.utils.Constants.ISFIRST;
import static com.example.simplecopy.utils.Constants.ISLOGIN;
import static com.example.simplecopy.utils.Constants.NOTE;
import static com.example.simplecopy.utils.Constants.NOTES;
import static com.example.simplecopy.utils.Constants.NOTE_NOTE;
import static com.example.simplecopy.utils.Constants.NUMBER;
import static com.example.simplecopy.utils.Constants.NUMBERS;
import static com.example.simplecopy.utils.Constants.TITLE;
import static com.example.simplecopy.utils.Constants.TITLE_NOTE;
import static com.example.simplecopy.utils.Constants.UID;
import static com.example.simplecopy.utils.Constants.UID_NOTE;
import static com.example.simplecopy.utils.Constants.USERS;
import static com.example.simplecopy.utils.FireStoreHelperQuery.fsInsert;
import static com.example.simplecopy.utils.FireStoreHelperQuery.fsUpdate;
import static com.example.simplecopy.utils.HelperMethods.isConnected;


public class NoteEditorActivity extends AppCompatActivity {
    public static final String TAG = "NumberEditorActivity";

    public static final String EXTRA_Note_ID = "extraTaskId";
    private static final int DEFAULT_Note_ID = -1;
    public static final String INSTANCE_Note_ID = "instanceTaskId";
    private int mNoteId = DEFAULT_Note_ID;
    private int fav;
    private Long rowId;
    private NotesViewModel notesViewModel;
    private EditText mTitleEditText;
    private EditText mNotesEditText;
    private AppDatabase mDb;
    private FirebaseFirestore fdb;
    private CollectionReference noteDocuRef;

    Toolbar toolbar;
    TextViewUndoRedo undoRedoT;
    TextViewUndoRedo undoRedoN;

    private boolean mNoteHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener ( ) {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mNoteHasChanged = true;
            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        HelperMethods.changeLang (this, SharedPreferencesManger.onLoadLang (this));
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_note_editor);

        // all views that we will need to read user input from
        toolbar = findViewById (R.id.toolbar_note);
        setSupportActionBar (toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar ().setHomeAsUpIndicator (getResources ().getDrawable (R.drawable.ic__arrow_back));
        initViews ( );

        notesViewModel = new ViewModelProvider (this).get (NotesViewModel.class);
        mDb = AppDatabase.getInstance (getApplicationContext ( ));
        fdb = FirebaseFirestore.getInstance ( );
        noteDocuRef = fdb.collection (USERS)
                .document (LoadData (NoteEditorActivity.this, USER_NAME) + " " + LoadData (NoteEditorActivity.this, USER_ID))
                .collection (NOTES);

        if (savedInstanceState != null && savedInstanceState.containsKey (INSTANCE_Note_ID)) {
            mNoteId = savedInstanceState.getInt (INSTANCE_Note_ID, DEFAULT_Note_ID);
        }

        if (isConnected (App.getContext ( )) && LoadBoolean (NoteEditorActivity.this , ISLOGIN)){
            SaveData (NoteEditorActivity.this, ISFIRST, false);
        }

        Intent intent = getIntent ( );
        if (intent != null && intent.hasExtra (EXTRA_Note_ID)) {
            setTitle (getString (R.string.editor_activity_title_edit_note));
            getSupportActionBar ( ).setTitle (Html.fromHtml ("<font color=\"#FFFFFF\">" + getString (R.string.editor_activity_title_edit_note) + "</font>"));
            if (mNoteId == DEFAULT_Note_ID) {

                mNoteId = intent.getIntExtra (EXTRA_Note_ID, DEFAULT_Note_ID);

                EditorNotesViewModelFactory factory = new EditorNotesViewModelFactory (mDb, mNoteId);
                final EditorNotesViewModel viewModel = new ViewModelProvider (this, factory).get (EditorNotesViewModel.class);
                viewModel.getNotes ( ).observe (this, new Observer<NotesData> ( ) {
                    @Override
                    public void onChanged(NotesData notesData) {
                        viewModel.getNotes ( ).removeObserver (this);
                        populateUI (notesData);
                    }
                });
            }
        } else {
            setTitle (getString (R.string.editor_activity_title_new_note));
            getSupportActionBar ( ).setTitle (Html.fromHtml ("<font color=\"#FFFFFF\">" + getString (R.string.editor_activity_title_new_note) + "</font>"));
        }

        mTitleEditText.setOnTouchListener (mTouchListener);
        mNotesEditText.setOnTouchListener (mTouchListener);
        undoRedoT = new TextViewUndoRedo (mTitleEditText, this);
        undoRedoN = new TextViewUndoRedo (mNotesEditText, this);
    }

    // get user input from editor and save new data into database
    private void insertData() {
        if (mTitleEditText.getText ( ) == null || mTitleEditText.getText ( ).length ( ) == 0) {
            mTitleEditText.setError (getString (R.string.Please_insert_name));
            mTitleEditText.setFocusable (true);
            return;
        }
        String titleString = mTitleEditText.getText ( ).toString ( ).trim ( );

        if (mNotesEditText.getText ( ) == null || mNotesEditText.getText ( ).length ( ) == 0) {
            mNotesEditText.setError (getString (R.string.Please_insert_note));
            mNotesEditText.setFocusable (true);
            return;
        }
        String notesString = mNotesEditText.getText ( ).toString ( ).trim ( );

        final NotesData notes1 = new NotesData (titleString, notesString);
        final NotesData notes2 = new NotesData (titleString, notesString, fav);
        Map<String, Object> notesMap = new HashMap<> ( );
        notesMap.put (TITLE_NOTE, titleString);
        notesMap.put (NOTE_NOTE, notesString);
        notesMap.put (FAVORITE_NOTE, fav);

        if (mNoteId == DEFAULT_Note_ID) {
            AppExecutors.getInstance ( ).diskIO ( ).execute (new Runnable ( ) {
                @Override
                public void run() {
                    if (mDb.NotesDao ( ).isNameExist (titleString)) {
                        Log.d (TAG, "run: is true");
                        AppExecutors.getInstance ( ).mainThread ( ).execute (new Runnable ( ) {
                            @Override
                            public void run() {
                                Toast.makeText (NoteEditorActivity.this, getString (R.string.Name_is_exist), Toast.LENGTH_SHORT).show ( );
                            }
                        });

                    } else {
                        if (isConnected (App.getContext ())){
                            SaveData (NoteEditorActivity.this, ISFIRST, false);
                            Log.d (TAG, "run: is false");
                        }

                        try {
                            rowId = notesViewModel.insert (notes1);
                        } catch (ExecutionException e) {
                            // TODO - handle error
                            e.printStackTrace ( );
                        } catch (InterruptedException e) {
                            // TODO - handle error
                            e.printStackTrace ( );
                        }
                        notesMap.put (UID_NOTE, rowId);
                        String rowIdStr = rowId.toString ( );
//                        mDb.NotesDao ( ).insertTask (notes1);
                        if (SharedPreferencesManger.LoadBoolean (NoteEditorActivity.this, ISLOGIN)) {
                            fsInsert (noteDocuRef, rowIdStr, notesMap);
                        }
                        // Exit activity
                        finish ( );
                    }
                }
            });
            Toast.makeText (NoteEditorActivity.this, getString (R.string.Saved), Toast.LENGTH_SHORT).show ( );
        } else {
            AppExecutors.getInstance ( ).diskIO ( ).execute (new Runnable ( ) {
                @Override
                public void run() {
                    notes2.setId (mNoteId);
                    mDb.NotesDao ( ).updateTask (notes2);
                    String mNoteIdIdStr = String.valueOf (mNoteId);
                    if (SharedPreferencesManger.LoadBoolean (NoteEditorActivity.this, ISLOGIN)) {
                        fsUpdate (noteDocuRef, mNoteIdIdStr, notesMap);
                    }
                }
            });
            // Exit activity
            finish ( );
            Toast.makeText (NoteEditorActivity.this, getString (R.string.Saved), Toast.LENGTH_SHORT).show ( );
        }

    }

    private void initViews() {
        mTitleEditText = findViewById (R.id.edit_title_note);
        mNotesEditText = findViewById (R.id.edit_notes_note);
    }

    private void populateUI(NotesData notesData) {
        //return if the note is null
        if (notesData == null) {
            return;
        }

        mTitleEditText.setText (notesData.getTitle ( ));
        mNotesEditText.setText (notesData.getNote ( ));
        fav = notesData.getFavorite ( );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater ( ).inflate (R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateEnableButtons (menu);
        super.onPrepareOptionsMenu (menu);
        return true;
    }

    private void updateEnableButtons(Menu menu) {
        //enable undo button
        if (mTitleEditText.isFocused ( )) {
            invalidateOptionsMenu ( );
            if (undoRedoT.getCanUndo ( )) {
                menu.getItem (0).setEnabled (true);
                menu.getItem (0).setIcon (R.drawable.ic_undo_white);
            } else {
                menu.getItem (0).setEnabled (false);
                menu.getItem (0).setIcon (R.drawable.ic_undo_false);
            }
        }
        if (mNotesEditText.isFocused ( )) {
            invalidateOptionsMenu ( );
            if (undoRedoN.getCanUndo ( )) {
                menu.getItem (0).setEnabled (true);
                menu.getItem (0).setIcon (R.drawable.ic_undo_white);
            } else {
                menu.getItem (0).setEnabled (false);
                menu.getItem (0).setIcon (R.drawable.ic_undo_false);
            }
        }

        //enable redo button
        if (mTitleEditText.isFocused ( )) {
            invalidateOptionsMenu ( );
            if (undoRedoT.getCanRedo ( )) {
                menu.getItem (1).setEnabled (true);
                menu.getItem (1).setIcon (R.drawable.ic_redo_white);
            } else {
                menu.getItem (1).setEnabled (false);
                menu.getItem (1).setIcon (R.drawable.ic_redo_false);
            }
        }
        if (mNotesEditText.isFocused ( )) {
            invalidateOptionsMenu ( );
            if (undoRedoN.getCanRedo ( )) {
                menu.getItem (1).setEnabled (true);
                menu.getItem (1).setIcon (R.drawable.ic_redo_white);
            } else {
                menu.getItem (1).setEnabled (false);
                menu.getItem (1).setIcon (R.drawable.ic_redo_false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId ( )) {
            case R.id.save:
                // Save Data
                insertData ( );
                return true;

            case R.id.undo:
                if (mTitleEditText.isFocused ( )) {
                    if (undoRedoT.getCanUndo ( )) {
                        undoRedoT.undo ( );
                    }
                }
                if (mNotesEditText.isFocused ( )) {
                    if (undoRedoN.getCanUndo ( )) {
                        undoRedoN.undo ( );
                    }
                }
                return true;

            case R.id.redo:
                if (mTitleEditText.isFocused ( )) {
                    if (undoRedoT.getCanRedo ( )) {
                        undoRedoT.redo ( );
                    }
                }
                if (mNotesEditText.isFocused ( )) {
                    if (undoRedoN.getCanRedo ( )) {
                        undoRedoN.redo ( );
                    }
                }
                return true;

            case android.R.id.home:
                // If the note hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mNoteHasChanged) {
                    NavUtils.navigateUpFromSameTask (NoteEditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener ( ) {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask (NoteEditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog (discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected (item);
    }

    @Override
    public void onBackPressed() {
        // If the note hasn't changed, continue with handling back button press
        if (!mNoteHasChanged) {
            super.onBackPressed ( );
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener ( ) {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish ( );
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog (discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setMessage (R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton (R.string.discard, discardButtonClickListener);
        builder.setNeutralButton (R.string.Save, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {
                //save data
                insertData ( );
            }
        });
        builder.setNegativeButton (R.string.keep_editing, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the note.
                if (dialog != null) {
                    dialog.dismiss ( );
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create ( );
        alertDialog.show ( );
    }
}
