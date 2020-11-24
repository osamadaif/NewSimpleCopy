package com.example.simplecopy.ui.fragment.Notes;

import androidx.appcompat.app.AlertDialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplecopy.R;
import com.example.simplecopy.adapters.CopyNoteAdapter;
import com.example.simplecopy.data.local.database.AppDatabase;
import com.example.simplecopy.data.local.prefs.SharedPreferencesManger;
import com.example.simplecopy.data.model.NotesData;
import com.example.simplecopy.ui.activity.MainActivity;
import com.example.simplecopy.ui.activity.NoteEditor.NoteEditorActivity;
import com.example.simplecopy.ui.activity.user.UserActivity;
import com.example.simplecopy.utils.HelperMethods;
import com.example.simplecopy.utils.ThemeHelper;
import com.ferfalk.simplesearchview.SimpleSearchView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.simplecopy.adapters.CopyNoteAdapter.isBtnVisible;
import static com.example.simplecopy.utils.HelperMethods.vibrate;


public class NoteListFragment extends Fragment implements CopyNoteAdapter.ItemClickListener {
    View view;

    // Member variables for the adapter and RecyclerView
    private RecyclerView mNumbersList;
    private CopyNoteAdapter mAdapter;
    private NotesViewModel mainViewModel;
    private AppDatabase mDB;
    private MainActivity mainActivity;

    View mEmptyView;
    Button empty_btn;

    private boolean enableMenuItemNote;

    private FloatingActionButton fab;
    //FireBase auth
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private SimpleSearchView searchView;
    public static ActionMode actionMode;
    private ActionModeCallback actionModeCallback;

    // Required empty public constructor
    public NoteListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate (R.layout.copy_notes_fragment, container, false);
        fab = getActivity ( ).findViewById (R.id.fab);
        setUpRecycleView ( );
        mainViewModel = new ViewModelProvider (this).get (NotesViewModel.class);
        mDB = AppDatabase.getInstance (getContext ( ));
        firebaseAuth = FirebaseAuth.getInstance ( );
        user = firebaseAuth.getCurrentUser ( );
        mainActivity =(MainActivity)this.getActivity();
        searchView = getActivity ().findViewById(R.id.searchView);
        setupViewModel ( );
        actionModeCallback = new ActionModeCallback ( );
        empty_btn = view.findViewById (R.id.btn_empty_note);
        empty_btn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                addSomeNumber (view);
            }
        });
        return view;
    }

    private void setupViewModel() {
        NotesViewModel viewModel = new ViewModelProvider (this).get (NotesViewModel.class);

        viewModel.getsearchQueryByNote ( ).observe (getViewLifecycleOwner ( ), new Observer<List<NotesData>> ( ) {
            @Override
            public void onChanged(List<NotesData> notesDataList1) {
                if (!notesDataList1.isEmpty ( )) {
                    mNumbersList.setVisibility (View.VISIBLE);
                    mEmptyView.setVisibility (View.GONE);
                    mAdapter.setItems (notesDataList1);
                    enableMenuItemNote = true;
                } else {
                    mNumbersList.setVisibility (View.GONE);
                    if (mAdapter.getItems ().size () == 0){
                        mEmptyView.setVisibility (View.VISIBLE);
                    } else {
                        mEmptyView.setVisibility (View.GONE);
                    }
                    enableMenuItemNote = false;
                }
            }
        });

        //first time set an empty value to get all data
        viewModel.filterTextAll.setValue ("");
        searchView.getSearchEditText ().addTextChangedListener (new TextWatcher ( ) {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setFilter (s.toString());
                mAdapter.searchString = s.toString ();

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setFilter (s.toString());
                mAdapter.searchString = s.toString ();
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu (true);
        super.onCreate (savedInstanceState);
    }

    private void setUpRecycleView() {
        mNumbersList = view.findViewById (R.id.recycle_note);
        mEmptyView = view.findViewById (R.id.empty_notes_layout);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager (getActivity ( ));
        mNumbersList.setLayoutManager (layoutManager);
//        mNumbersList.addItemDecoration(new DividerItemDecoration (Objects.requireNonNull (getContext ( )), 0));
        mNumbersList.setItemAnimator (new DefaultItemAnimator ( ));
        //mNumbersList.showIfEmpty(mEmptyView);
        mNumbersList.setHasFixedSize (true);
        mAdapter = new CopyNoteAdapter (getActivity ( ), this);
        mAdapter.setHasStableIds (true);
        mNumbersList.setAdapter (mAdapter);
        mNumbersList.addOnScrollListener (new RecyclerView.OnScrollListener ( ) {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged (recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0 && !fab.isShown ( ))
                    fab.show ( );
                else if (dy > 0 && fab.isShown ( ))
                    fab.hide ( );
            }
        });

    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate (R.menu.menu_home, menu);
        MenuItem item = menu.findItem (R.id.search);
//        SearchView searchView = (SearchView) item.getActionView ( );
//        searchView.setActivated (true);
//        searchView.setQueryHint (getString (R.string.Search));
        searchView.setMenuItem(item);
        searchView.setTabLayout( mainActivity.tabLayout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId ( )) {

            case R.id.deletall:
                // delete all data
                showDeleteAllConfirmationDialog ( );
                break;
            case R.id.lang:
                HelperMethods.showSelectLanguageDialog (getActivity (), getContext ());
                break;
            case R.id.darkMode:
                if (AppCompatDelegate.getDefaultNightMode () == AppCompatDelegate.MODE_NIGHT_YES){
                    SharedPreferencesManger.SaveThemePref (false, getActivity ());
                    ThemeHelper.applyTheme (false);
                } else {
                    SharedPreferencesManger.SaveThemePref (true, getActivity ());
                    ThemeHelper.applyTheme (true);
                }
                restartApp ();
                break;
            case R.id.logout:
                if (user != null) {
                    showLogoutDialog ( );
                } else {
                    startActivity (new Intent (getActivity ( ), UserActivity.class));
                }
                return true;
        }
        return super.onOptionsItemSelected (item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem item = menu.findItem (R.id.logout);
        if (user != null) {
            item.setTitle (getResources ( ).getString (R.string.logout));
        } else {
            item.setTitle (getResources ( ).getString (R.string.login));
        }

        MenuItem darkModeItem = menu.findItem (R.id.darkMode);
        if (AppCompatDelegate.getDefaultNightMode () == AppCompatDelegate.MODE_NIGHT_YES) {
            darkModeItem.setTitle (getResources ( ).getString (R.string.lightMode));
        } else {
            darkModeItem.setTitle (getResources ( ).getString (R.string.darkMode));
        }

        MenuItem deleteAllItemNote = menu.findItem (R.id.deletall);
        if (enableMenuItemNote) {
            deleteAllItemNote.setEnabled(true);
        } else {
            // disabled
            deleteAllItemNote.setEnabled(false);
        }
        super.onPrepareOptionsMenu (menu);
    }

    private void showDeleteAllConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ( ));
        builder.setMessage (R.string.delete_all_notes_dialog_msg);
        builder.setPositiveButton (R.string.delete, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the number.
                mainViewModel.deleteAllNumbers ( );
                Toast.makeText (getActivity ( ), getString (R.string.Deleted), Toast.LENGTH_SHORT).show ( );
            }
        });
        builder.setNegativeButton (R.string.cancel, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss ( );
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create ( );
        alertDialog.show ( );
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ( ));
        builder.setMessage (R.string.logout_dialog_msg);
        builder.setPositiveButton (R.string.logout, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {
                firebaseAuth.signOut ( );
                startActivity (new Intent (getActivity ( ), UserActivity.class));
                getActivity ( ).finish ( );
            }
        });
        builder.setNegativeButton (R.string.cancel, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss ( );
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create ( );
        alertDialog.show ( );
    }

    private void showDeleteConfirmationDialog(final NotesData notesData) {

        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ( ));
        builder.setMessage (R.string.delete_note_dialog_msg);
        builder.setPositiveButton (R.string.delete, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the Number.
                mainViewModel.delete (notesData);

                Toast.makeText (getActivity ( ), getString (R.string.Deleted), Toast.LENGTH_SHORT).show ( );

            }
        });
        builder.setNegativeButton (R.string.cancel, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss ( );
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create ( );
        alertDialog.show ( );
    }

    public void addSomeNumber(View view) {
        Intent intent = new Intent (getActivity ( ), NoteEditorActivity.class);
        startActivity (intent);
    }

    @Override
    public void onNoteEdit(int itemId) {
        Intent intent = new Intent (getActivity ( ), NoteEditorActivity.class);
        intent.putExtra (NoteEditorActivity.EXTRA_Note_ID, itemId);
        startActivity (intent);
    }

    @Override
    public void onNoteDelete(NotesData notesData) {
        showDeleteConfirmationDialog (notesData);

    }

    @Override
    public void onItemClickListener(View view, NotesData notesData, int pos, int itemId) {
        String noteString = notesData.getNote ( );

        if (mAdapter.getSelectedItemCount ( ) > 0) {
            enableActionMode (pos, view);
        } else {
            ClipboardManager clipboard = (ClipboardManager) getActivity ( ).getSystemService (Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText ("TextView", noteString);
            clipboard.setPrimaryClip (clip);
            Toast.makeText (getActivity ( ), getString (R.string.Note_Copied), Toast.LENGTH_SHORT).show ( );
        }
    }

    @Override
    public void onItemLongClick(View view, NotesData notesData, int pos, int itemId) {
        enableActionMode (pos, view);
    }

    private void enableActionMode(int position, View view) {
        if (actionMode == null) {
            view.setHapticFeedbackEnabled (true);

            vibrate (view);
            actionMode = (Objects.requireNonNull (getActivity ( ))).startActionMode (actionModeCallback);
        }
        toggleSelection (position);
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection (position);
        int count = mAdapter.getSelectedItemCount ( );

        if (count == 0) {
            actionMode.finish ( );
            isBtnVisible = true;
        } else {
            actionMode.setTitle (String.valueOf (count) + " " + getResources ( ).getString (R.string.selected));
            actionMode.invalidate ( );
        }
    }

    public class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            HelperMethods.setSystemBarColor (getActivity ( ), R.color.colorPrimaryLight);
            isBtnVisible = false;
            mAdapter.notifyDataSetChanged ();
            if (fab.isShown ()){
                fab.hide ();
            }

            mode.getMenuInflater ( ).inflate (R.menu.menu_delete, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId ( );
            if (id == R.id.action_delete) {
                showDeleteSelectedConfirmationDialog ( );
//                mode.finish ( );
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelections ( );
            actionMode.finish ( );
            isBtnVisible = true;
            mAdapter.notifyDataSetChanged ();
            if (!fab.isShown ()){
                fab.show ();
            }
            actionMode = null;
//            HelperMethods.setSystemBarColor (getActivity ( ), R.color.colorPrimaryDark);
        }
    }

    public void deleteSelection() {
        List<Integer> selectedItemPositions = mAdapter.getSelectedItems ( );
        List<Integer> selectedItemIDs = new ArrayList<> ( );
        for (int x : selectedItemPositions) {
            int selectedId = mAdapter.getId (x);
            selectedItemIDs.add (selectedId);
        }
        mAdapter.removeSelected (selectedItemIDs);
        mAdapter.notifyDataSetChanged ( );
    }

    private void showDeleteSelectedConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ( ));
        builder.setMessage (R.string.delete_note_selected_dialog_msg);
        builder.setPositiveButton (R.string.delete, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {
                deleteSelection ( );
                actionMode.finish ( );
                Toast.makeText (getActivity ( ), getString (R.string.Deleted), Toast.LENGTH_SHORT).show ( );
            }
        });
        builder.setNegativeButton (R.string.cancel, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss ( );
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create ( );
        alertDialog.show ( );
    }

    public void restartApp()
    {
        getActivity ().recreate ();
    }
}
