package com.example.simplecopy;


import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;

import android.text.Html;
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
import androidx.lifecycle.ViewModelProviders;

import com.example.simplecopy.ViewModeler.EditorViewModel;
import com.example.simplecopy.ViewModeler.EditorViewModelFactory;
import com.example.simplecopy.data.AppDatabase;
import com.example.simplecopy.data.Numbers;


public class EditorActivity extends AppCompatActivity
{
    public static final String TAG = "EditorActivity";

    public static final String EXTRA_NUMBER_ID = "extraTaskId";
    private static final int DEFAULT_NUMBER_ID = -1;
    public static final String INSTANCE_NUMBER_ID = "instanceTaskId";
    private int mNumberId = DEFAULT_NUMBER_ID;

    private EditText mTitleEditText;
    private EditText mNumbersEditText;
    private EditText mNotesEditText;
    private AppDatabase mDb;

    Toolbar toolbar;



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
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_editor);

        // all views that we will need to read user input from
        toolbar = findViewById (R.id.toolbar2);
        setSupportActionBar (toolbar);

        initViews();

        mDb = AppDatabase.getInstance (getApplicationContext ());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_NUMBER_ID) ){
            mNumberId = savedInstanceState.getInt(INSTANCE_NUMBER_ID, DEFAULT_NUMBER_ID);
        }

        Intent intent = getIntent ( );
        if (intent != null && intent.hasExtra(EXTRA_NUMBER_ID)){
            setTitle (getString (R.string.editor_activity_title_edit_number));
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.editor_activity_title_edit_number) + "</font>"));
            if (mNumberId == DEFAULT_NUMBER_ID){

                mNumberId = intent.getIntExtra(EXTRA_NUMBER_ID, DEFAULT_NUMBER_ID);

                EditorViewModelFactory factory = new EditorViewModelFactory (mDb,mNumberId);
                final EditorViewModel viewModel = ViewModelProviders.of(this,factory).get (EditorViewModel.class);
                viewModel.getNumbers ().observe (this, new Observer<Numbers> ( ) {
                    @Override
                    public void onChanged(Numbers numbers1) {
                        viewModel.getNumbers ().removeObserver (this);
                        populateUI(numbers1);
                    }
                });
            }
        }
        else {
            setTitle (getString (R.string.editor_activity_title_new_number));
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.editor_activity_title_new_number) + "</font>"));

        }

        mTitleEditText.setOnTouchListener (mTouchListener);
        mNumbersEditText.setOnTouchListener (mTouchListener);
        mNotesEditText.setOnTouchListener (mTouchListener);
    }

    // get user input from editor and save new data into database
    private void insertData() {
        if (mTitleEditText.getText ( ) == null || mTitleEditText.getText ( ).length () == 0)
        {
            mTitleEditText.setError (getString (R.string.Please_insert_name));
            return;
        }
        String titleString = mTitleEditText.getText ( ).toString ( ).trim ( );

        if (mNumbersEditText.getText ( ) == null || mNumbersEditText.getText ( ).length () == 0)
        {
            mNumbersEditText.setError (getString (R.string.Please_insert_number));
            return;
        }
        String numbersString = mNumbersEditText.getText ( ).toString ( ).trim ( );
        final long numbers = Long.parseLong (numbersString);
        String notesString = mNotesEditText.getText ( ).toString ( ).trim ( );


        final Numbers numbers1 = new Numbers (titleString,numbers,notesString);
        AppExecutors.getInstance ().diskIO ().execute (new Runnable ( ) {
            @Override
            public void run() {
                if (mNumberId == DEFAULT_NUMBER_ID){
                    mDb.numbersDao ().insertTask (numbers1);

                } else {
                    numbers1.setId (mNumberId);
                    mDb.numbersDao ().updateTask (numbers1);
                }
                // Exit activity
                finish ( );
            }
        });
        Toast.makeText (this, getString (R.string.Saved), Toast.LENGTH_SHORT).show ( );
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

        mTitleEditText.setText(number.getTitle());
        long numberEdit = number.getNumber();
        String numberStr = String.valueOf (numberEdit);
        mNumbersEditText.setText(numberStr);
        mNotesEditText.setText(number.getNote());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater ( ).inflate (R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId ( )) {
            case R.id.save:
                // Save Data
                insertData ( );
                return true;

            case android.R.id.home:
                // If the number hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mNumberHasChanged) {
                    NavUtils.navigateUpFromSameTask (EditorActivity.this);
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
                                NavUtils.navigateUpFromSameTask (EditorActivity.this);
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
        builder.setNeutralButton (R.string.discard, discardButtonClickListener);
        builder.setPositiveButton (R.string.Save, new DialogInterface.OnClickListener ( ) {
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
