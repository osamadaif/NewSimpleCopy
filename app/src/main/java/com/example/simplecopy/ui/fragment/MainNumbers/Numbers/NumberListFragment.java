package com.example.simplecopy.ui.fragment.MainNumbers.Numbers;

import androidx.appcompat.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.simplecopy.R;
import com.example.simplecopy.ui.activity.MainActivity;
import com.example.simplecopy.ui.activity.SettingsActivity;
import com.example.simplecopy.ui.activity.user.UserActivity;
import com.example.simplecopy.ui.fragment.MainNumbers.MainViewModel;
import com.example.simplecopy.adapters.CopyAdapter;
import com.example.simplecopy.data.local.database.AppDatabase;
import com.example.simplecopy.data.model.Numbers;
import com.example.simplecopy.ui.activity.NumberEditor.NumberEditorActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.simplecopy.utils.HelperMethods.vibrate;


public class NumberListFragment extends Fragment implements CopyAdapter.ItemClickListener {
    View view;

    // Member variables for the adapter and RecyclerView
    private RecyclerView mNumbersList;
    private CopyAdapter mAdapter;
    private MainViewModel mainViewModel;
    private AppDatabase mDB;
    View mEmptyView;
    Button empty_btn;
    private FloatingActionButton fab;
    //FireBase auth
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    public static ActionMode actionMode;
    private ActionModeCallback actionModeCallback;

    // Required empty public constructor
    public NumberListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate (R.layout.fragment_copy_number_list, container, false);
        fab =  getActivity ().findViewById(R.id.fab);
        setUpRecycleView ();
        mainViewModel = ViewModelProviders.of (this).get (MainViewModel.class);
        mDB = AppDatabase.getInstance (getContext ());
        firebaseAuth = FirebaseAuth.getInstance ();
        user = firebaseAuth.getCurrentUser ();
        setupViewModel ( );
        actionModeCallback = new ActionModeCallback ( );
        empty_btn = view.findViewById (R.id.btn_empty_title);
        empty_btn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                addSomeNumber (view);
            }
        });
        return view;
    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getNumbers ().observe ( getViewLifecycleOwner(), new Observer<List<Numbers>> ( ) {
            @Override
            public void onChanged(List<Numbers> numbersList1) {
                if (numbersList1.isEmpty ()){
                    mNumbersList.setVisibility (View.GONE);
                    mEmptyView.setVisibility (View.VISIBLE);

                }else {
                    mNumbersList.setVisibility (View.VISIBLE);
                    mEmptyView.setVisibility (View.GONE);
                    mAdapter.setItems (numbersList1);
                }

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

    public void setUpRecycleView(){
        mNumbersList = view.findViewById (R.id.recycle_list);
        mEmptyView = view.findViewById (R.id.empty_layout);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager (getActivity ());
        mNumbersList.setLayoutManager (layoutManager);
        mNumbersList.setItemAnimator (new DefaultItemAnimator ());
        //mNumbersList.showIfEmpty(mEmptyView);
        mNumbersList.setHasFixedSize (true);
        mAdapter = new CopyAdapter(getActivity (), this);
        mAdapter.setHasStableIds (true);
        mNumbersList.setAdapter (mAdapter);
        mNumbersList.addOnScrollListener (new RecyclerView.OnScrollListener ( ) {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy<0 && !fab.isShown())
                    fab.show();
                else if(dy>0 && fab.isShown())
                    fab.hide();
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate (R.menu.menu_home, menu);
        MenuItem item = menu.findItem (R.id.search);
        SearchView searchView = (SearchView)item.getActionView ();
        searchView.setActivated (true);
        searchView.setQueryHint (getString (R.string.Search));
        //searchView.clearFocus ();

        searchView.setOnQueryTextListener (new SearchView.OnQueryTextListener ( ) {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getResults (newText);
                return false;
            }

            private void getResults (final String newText){

                mainViewModel.searchQuery (newText)
                        .observe (getActivity (), new Observer<List<Numbers>> ( ) {
                            @Override
                            public void onChanged(List<Numbers> numbers) {
                                if (numbers == null) return;
                                    mAdapter.setSearchItem (numbers,newText);

                            }
                        });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId ()){

            case R.id.deletall:
                // delete all data
                showDeleteAllConfirmationDialog ( );
                break;
            case R.id.settings:
                startActivity (new Intent (getActivity (), SettingsActivity.class));
                Objects.requireNonNull (getActivity ( )).finish ();
                break;
            case R.id.logout:
                if (user != null){
                    showLogoutDialog ();
                } else {
                    startActivity (new Intent (getActivity (), UserActivity.class));
                }
                return true;
        }
        return super.onOptionsItemSelected (item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem item = menu.findItem (R.id.logout);
        if (user != null){
            item.setTitle (getResources ().getString (R.string.logout));
        } else {
            item.setTitle (getResources ().getString (R.string.login));
        }
        super.onPrepareOptionsMenu (menu);
    }

    private void showDeleteAllConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ());
        builder.setMessage (R.string.delete_all_dialog_msg);
        builder.setPositiveButton (R.string.delete, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the number.
                mainViewModel.deleteAllNumbers ();
                Toast.makeText (getActivity (), getString (R.string.Deleted), Toast.LENGTH_SHORT).show ( );
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
        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ());
        builder.setMessage (R.string.logout_dialog_msg);
        builder.setPositiveButton (R.string.logout, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {
                firebaseAuth.signOut ();
                startActivity (new Intent (getActivity (), UserActivity.class));
                getActivity ().finish ();
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

    private void showDeleteConfirmationDialog(final Numbers numbers) {

        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ());
        builder.setMessage (R.string.delete_dialog_msg);
        builder.setPositiveButton (R.string.delete, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the Number.
                mainViewModel.delete (numbers);

                Toast.makeText (getActivity (), getString (R.string.Deleted), Toast.LENGTH_SHORT).show ( );

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

    @Override
    public void onNumberEdit(int itemId) {
        Intent intent = new Intent (getActivity (), NumberEditorActivity.class);
        intent.putExtra (NumberEditorActivity.EXTRA_NUMBER_ID, itemId);
        startActivity (intent);
    }

    @Override
    public void onNumberDelete(Numbers numbers) {
        showDeleteConfirmationDialog (numbers);

    }

    public void addSomeNumber(View view) {
        Intent intent = new Intent(getActivity (), NumberEditorActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClickListener(View view, Numbers numbers, int pos, int itemId) {
        String numberStr = numbers.getNumber ( );

        if (mAdapter.getSelectedItemCount ( ) > 0) {
            enableActionMode (pos, view);
        } else {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService (Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText ("TextView",numberStr);
        clipboard.setPrimaryClip (clip);
        Toast.makeText (getActivity (), getString (R.string.Copied), Toast.LENGTH_SHORT).show ( );
        }
    }

    @Override
    public void onItemLongClick(View view, Numbers numbers, int pos, int itemId) {
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
            CopyAdapter.isBtnVisible = true;
        } else {
            actionMode.setTitle (String.valueOf (count) + " " + getResources ( ).getString (R.string.selected));
            actionMode.invalidate ( );
        }
    }

    public class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            HelperMethods.setSystemBarColor (getActivity ( ), R.color.colorPrimaryLight);
            CopyAdapter.isBtnVisible = false;
            mAdapter.notifyDataSetChanged ();
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
            CopyAdapter.isBtnVisible = true;
            mAdapter.notifyDataSetChanged ();
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

}
