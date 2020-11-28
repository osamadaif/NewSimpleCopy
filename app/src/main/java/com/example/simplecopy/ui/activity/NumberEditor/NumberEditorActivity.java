package com.example.simplecopy.ui.activity.NumberEditor;


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
import com.example.simplecopy.ui.fragment.MainNumbers.MainViewModel;
import com.example.simplecopy.utils.AppExecutors;
import com.example.simplecopy.R;
import com.example.simplecopy.data.local.database.AppDatabase;
import com.example.simplecopy.data.model.Numbers;
import com.example.simplecopy.utils.Constants;
import com.example.simplecopy.utils.FireStoreHelperQuery;
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
import static com.example.simplecopy.utils.Constants.ISFIRST;
import static com.example.simplecopy.utils.Constants.ISLOGIN;
import static com.example.simplecopy.utils.Constants.NOTE;
import static com.example.simplecopy.utils.Constants.NUMBER;
import static com.example.simplecopy.utils.Constants.NUMBERS;
import static com.example.simplecopy.utils.Constants.TITLE;
import static com.example.simplecopy.utils.Constants.UID;
import static com.example.simplecopy.utils.Constants.USERS;
import static com.example.simplecopy.utils.FireStoreHelperQuery.fsInsert;
import static com.example.simplecopy.utils.FireStoreHelperQuery.fsUpdate;
import static com.example.simplecopy.utils.HelperMethods.isConnected;


public class NumberEditorActivity extends AppCompatActivity {
    public static final String TAG = "NumberEditorActivity";

    public static final String EXTRA_NUMBER_ID = "extraTaskId";
    private static final int DEFAULT_NUMBER_ID = -1;
    public static final String INSTANCE_NUMBER_ID = "instanceTaskId";
    private int mNumberId = DEFAULT_NUMBER_ID;
    private int fav;
    private int done;
    private int daily;
    private boolean isExist;
    private Long rowId;
    private MainViewModel mainViewModel;
    private EditText mTitleEditText;
    private EditText mNumbersEditText;
    private EditText mNotesEditText;
    private AppDatabase mDb;
    private FirebaseFirestore fdb;
    private CollectionReference numberDocuRef;


    Toolbar toolbar;

    TextViewUndoRedo undoRedoT;
    TextViewUndoRedo undoRedoM;
    TextViewUndoRedo undoRedoN;

    private boolean mNumberHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener ( ) {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mNumberHasChanged = true;
            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        HelperMethods.changeLang (this, SharedPreferencesManger.onLoadLang (this));
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_editor);

        // all views that we will need to read user input from
        toolbar = findViewById (R.id.toolbar2);
        setSupportActionBar (toolbar);
        getSupportActionBar ( ).setDisplayHomeAsUpEnabled (true);
        getSupportActionBar ( ).setHomeAsUpIndicator (getResources ( ).getDrawable (R.drawable.ic__arrow_back));
        initViews ( );
        mainViewModel = new ViewModelProvider (this).get (MainViewModel.class);
        mDb = AppDatabase.getInstance (getApplicationContext ( ));
        fdb = FirebaseFirestore.getInstance ( );
        numberDocuRef = fdb.collection (USERS)
                .document (LoadData (NumberEditorActivity.this, USER_NAME) + " " + LoadData (NumberEditorActivity.this, USER_ID))
                .collection (NUMBERS);

        if (savedInstanceState != null && savedInstanceState.containsKey (INSTANCE_NUMBER_ID)) {
            mNumberId = savedInstanceState.getInt (INSTANCE_NUMBER_ID, DEFAULT_NUMBER_ID);
        }

        Intent intent = getIntent ( );
        if (intent != null && intent.hasExtra (EXTRA_NUMBER_ID)) {
            setTitle (getString (R.string.editor_activity_title_edit_number));
            getSupportActionBar ( ).setTitle (Html.fromHtml ("<font color=\"#FFFFFF\">" + getString (R.string.editor_activity_title_edit_number) + "</font>"));
            if (mNumberId == DEFAULT_NUMBER_ID) {

                mNumberId = intent.getIntExtra (EXTRA_NUMBER_ID, DEFAULT_NUMBER_ID);

                EditorViewModelFactory factory = new EditorViewModelFactory (mDb, mNumberId);
                final EditorViewModel viewModel = new ViewModelProvider (this, factory).get (EditorViewModel.class);
                viewModel.getNumbers ( ).observe (this, new Observer<Numbers> ( ) {
                    @Override
                    public void onChanged(Numbers numbers1) {
                        viewModel.getNumbers ( ).removeObserver (this);
                        populateUI (numbers1);
                    }
                });
            }
        } else {
            setTitle (getString (R.string.editor_activity_title_new_number));
            getSupportActionBar ( ).setTitle (Html.fromHtml ("<font color=\"#FFFFFF\">" + getString (R.string.editor_activity_title_new_number) + "</font>"));
        }

        mTitleEditText.setOnTouchListener (mTouchListener);
        mNumbersEditText.setOnTouchListener (mTouchListener);
        mNotesEditText.setOnTouchListener (mTouchListener);
        undoRedoT = new TextViewUndoRedo (mTitleEditText, this);
        undoRedoM = new TextViewUndoRedo (mNumbersEditText, this);
        undoRedoN = new TextViewUndoRedo (mNotesEditText, this);
    }

    // get user input from editor and save new data into database
    private void insertData() {
        if (mTitleEditText.getText ( ) == null || mTitleEditText.getText ( ).length ( ) == 0) {
            mTitleEditText.setError (getString (R.string.Please_insert_name));
            return;
        }
        String titleString = mTitleEditText.getText ( ).toString ( ).trim ( );

        if (mNumbersEditText.getText ( ) == null || mNumbersEditText.getText ( ).length ( ) == 0) {
            mNumbersEditText.setError (getString (R.string.Please_insert_number));
            return;
        }
        String numbers = mNumbersEditText.getText ( ).toString ( ).trim ( );

        String notesString = mNotesEditText.getText ( ).toString ( ).trim ( );

        final Numbers numbers1 = new Numbers (titleString, numbers, notesString);
        final Numbers numbers2 = new Numbers (titleString, numbers, notesString, fav, done, daily);
        Map<String, Object> numbersMap = new HashMap<> ( );
        numbersMap.put (TITLE, titleString);
        numbersMap.put (NUMBER, numbers);
        numbersMap.put (NOTE, notesString);
        numbersMap.put (FAVORITE, fav);
        numbersMap.put (DONE, done);
        numbersMap.put (DAILY, daily);

        if (mNumberId == DEFAULT_NUMBER_ID) {
            AppExecutors.getInstance ( ).diskIO ( ).execute (new Runnable ( ) {
                @Override
                public void run() {
                    if (mDb.numbersDao ( ).isNameExist (titleString)) {
                        Log.d (TAG, "run: is true");
                        AppExecutors.getInstance ( ).mainThread ( ).execute (new Runnable ( ) {
                            @Override
                            public void run() {
                                Toast.makeText (NumberEditorActivity.this, getString (R.string.Name_is_exist), Toast.LENGTH_SHORT).show ( );
                            }
                        });

                    } else {
                        if (isConnected (App.getContext ())){
                            SaveData (NumberEditorActivity.this, ISFIRST, false);
                            Log.d (TAG, "run: is false");
                        }

                        try {
                            rowId = mainViewModel.insert (numbers1);
                        } catch (ExecutionException e) {
                            // TODO - handle error
                            e.printStackTrace ( );
                        } catch (InterruptedException e) {
                            // TODO - handle error
                            e.printStackTrace ( );
                        }
                        numbersMap.put (UID, rowId);
                        String rowIdStr = rowId.toString ( );
//                        mDb.numbersDao ( ).insertTask (numbers1);
                        if (SharedPreferencesManger.LoadBoolean (NumberEditorActivity.this, ISLOGIN)) {
                            fsInsert (numberDocuRef, rowIdStr, numbersMap);
                        }
                        // Exit activity
                        finish ( );
                    }
                }
            });
            Toast.makeText (this, getString (R.string.Saved), Toast.LENGTH_SHORT).show ( );
        } else {
            AppExecutors.getInstance ( ).diskIO ( ).execute (new Runnable ( ) {
                @Override
                public void run() {
                    numbers2.setId (mNumberId);
                    mDb.numbersDao ( ).updateTask (numbers2);
                    String mNumberIdStr = String.valueOf (mNumberId);
                    if (SharedPreferencesManger.LoadBoolean (NumberEditorActivity.this, ISLOGIN)) {
                        fsUpdate (numberDocuRef, mNumberIdStr, numbersMap);
                    }
                }
            });
            // Exit activity
            finish ( );
            Toast.makeText (this, getString (R.string.Saved), Toast.LENGTH_SHORT).show ( );
        }

    }

    private void initViews() {
        mTitleEditText = findViewById (R.id.edit_title);
        mNumbersEditText = findViewById (R.id.edit_number);
        mNotesEditText = findViewById (R.id.edit_notes);
    }

    private void populateUI(Numbers number) {
        //return if the number is null
        if (number == null) {
            return;
        }

        mTitleEditText.setText (number.getTitle ( ));
        mNumbersEditText.setText (number.getNumber ( ));
        mNotesEditText.setText (number.getNote ( ));
        fav = number.getFavorite ( );
        done = number.getDone ( );
        daily = number.getDaily ( );
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
        if (mNumbersEditText.isFocused ( )) {
            invalidateOptionsMenu ( );
            if (undoRedoM.getCanUndo ( )) {
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
        if (mNumbersEditText.isFocused ( )) {
            invalidateOptionsMenu ( );
            if (undoRedoM.getCanRedo ( )) {
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
                if (mNumbersEditText.isFocused ( )) {
                    if (undoRedoM.getCanUndo ( )) {
                        undoRedoM.undo ( );
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
                if (mNumbersEditText.isFocused ( )) {
                    if (undoRedoM.getCanRedo ( )) {
                        undoRedoM.redo ( );
                    }
                }
                if (mNotesEditText.isFocused ( )) {
                    if (undoRedoN.getCanRedo ( )) {
                        undoRedoN.redo ( );
                    }
                }
                return true;

            case android.R.id.home:
                // If the number hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mNumberHasChanged) {
                    NavUtils.navigateUpFromSameTask (NumberEditorActivity.this);
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
                                NavUtils.navigateUpFromSameTask (NumberEditorActivity.this);
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
        // If the number hasn't changed, continue with handling back button press
        if (!mNumberHasChanged) {
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
                // and continue editing the number.
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
