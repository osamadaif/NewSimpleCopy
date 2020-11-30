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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.simplecopy.data.local.prefs.SharedPreferencesManger;
import com.example.simplecopy.ui.activity.MainActivity;
import com.example.simplecopy.ui.activity.user.UserActivity;
import com.example.simplecopy.ui.fragment.MainNumbers.MainViewModel;
import com.example.simplecopy.adapters.CopyAdapter;
import com.example.simplecopy.data.local.database.AppDatabase;
import com.example.simplecopy.data.model.Numbers;
import com.example.simplecopy.ui.activity.NumberEditor.NumberEditorActivity;
import com.example.simplecopy.utils.AppExecutors;
import com.example.simplecopy.utils.HelperMethods;
import com.example.simplecopy.utils.ThemeHelper;
import com.ferfalk.simplesearchview.SimpleSearchView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import static com.example.simplecopy.utils.FireStoreHelperQuery.fsDelete;
import static com.example.simplecopy.utils.HelperMethods.dismissProgressDialog;
import static com.example.simplecopy.utils.HelperMethods.isConnected;
import static com.example.simplecopy.utils.HelperMethods.progressDialog;
import static com.example.simplecopy.utils.HelperMethods.replaceFragment;
import static com.example.simplecopy.utils.HelperMethods.showProgressDialog;
import static com.example.simplecopy.utils.HelperMethods.vibrate;


public class NumberListFragment extends Fragment implements CopyAdapter.ItemClickListener {
    View view;
    private static final String TAG = "NumberListFragment";

    // Member variables for the adapter and RecyclerView
    private RecyclerView mNumbersList;
    private CopyAdapter mAdapter;
    private Numbers numbers;
    private MainViewModel mainViewModel;
    private AppDatabase mDB;
    private MainActivity mainActivity;
    private boolean enableMenuItem;
    private boolean enableSyncMenuItem;
    private FloatingActionButton fab;
    private FirebaseAuth firebaseAuth;  //FireBase auth
    private FirebaseUser user;
    private FirebaseFirestore fdb;
    private CollectionReference numberDocuRef;
    private SimpleSearchView searchView;
    private ActionModeCallback actionModeCallback;
    public static ActionMode actionMode;
    View mEmptyView;
    Button empty_btn;

    // Required empty public constructor
    public NumberListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate (R.layout.fragment_copy_number_list, container, false);
        fab = Objects.requireNonNull (getActivity ( )).findViewById (R.id.fab);
        setUpRecycleView ( );

        mainViewModel = new ViewModelProvider (this).get (MainViewModel.class);
        mainActivity = (MainActivity) this.getActivity ( );
        mDB = AppDatabase.getInstance (getContext ( ));
        firebaseAuth = FirebaseAuth.getInstance ( );
        user = firebaseAuth.getCurrentUser ( );
        fdb = FirebaseFirestore.getInstance ( );
        numberDocuRef = fdb.collection (USERS)
                .document (LoadData (getActivity ( ), USER_NAME) + " " + LoadData (getActivity ( ), USER_ID))
                .collection (NUMBERS);

        searchView = getActivity ( ).findViewById (R.id.searchView);
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
        MainViewModel viewModel = new ViewModelProvider (this).get (MainViewModel.class);
        viewModel.getSearchQueryForNumber ( ).observe (getViewLifecycleOwner ( ), new Observer<List<Numbers>> ( ) {
            @Override
            public void onChanged(List<Numbers> numbersList1) {
                if (numbersList1.isEmpty ( )) {
                    Log.d (TAG, "onChanged: is emptyyyyyy");
                    mNumbersList.setVisibility (View.GONE);
                    if (isConnected (getContext ( )) && LoadBoolean (getActivity ( ), ISLOGIN) && LoadBoolean (getActivity ( ), ISFIRST)) {
                        Log.d (TAG, "onChanged: is Connnnnnected");
                        numberDocuRef.addSnapshotListener (new EventListener<QuerySnapshot> ( ) {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (!value.isEmpty ( )) {
                                    Log.d (TAG, "onEvent: siiiize noooo 0000");
//                                    showRetrieveDataFromFSDialog ( );
                                    getAllDataFromFireStore ( );
                                }
                                Log.d (TAG, "onEvent: siiiiiize 0000");
                            }
                        });

                    }

                    if (mAdapter.getItemCount ( ) == 0) {
                        mEmptyView.setVisibility (View.VISIBLE);
                    } else {
                        mEmptyView.setVisibility (View.GONE);
                    }
                    enableMenuItem = false;
                    enableSyncMenuItem = false;

                } else {
                    mNumbersList.setVisibility (View.VISIBLE);
                    mEmptyView.setVisibility (View.GONE);
                    mAdapter.setItems (numbersList1);
                    enableMenuItem = true;
                    enableSyncMenuItem = true;
                    if (isConnected (getContext ( )) && LoadBoolean (getActivity ( ), ISLOGIN) && LoadBoolean (getActivity ( ), ISFIRST)) {


                    }
                }
            }
        });

        //first time set an empty value to get all data
        viewModel.filterTextAll.setValue ("");
        searchView.getSearchEditText ( ).

                addTextChangedListener (new TextWatcher ( ) {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        viewModel.setFilter (s.toString ( ));
                        mAdapter.searchString = s.toString ( );
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        viewModel.setFilter (s.toString ( ));
                        mAdapter.searchString = s.toString ( );
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

    public void setUpRecycleView() {
        mNumbersList = view.findViewById (R.id.recycle_list);
        mEmptyView = view.findViewById (R.id.empty_layout);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager (getActivity ( ));
        mNumbersList.setLayoutManager (layoutManager);
        mNumbersList.setItemAnimator (new DefaultItemAnimator ( ));
        //mNumbersList.showIfEmpty(mEmptyView);
        mNumbersList.setHasFixedSize (true);
        mAdapter = new CopyAdapter (getActivity ( ), getActivity ( ), this);
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

    public void saveDataIntoFireStore() {
        numberDocuRef.get ( ).addOnCompleteListener (new OnCompleteListener<QuerySnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful ( )) {
                    if (task.getResult ( ).size ( ) > 0) {
                        Log.d (TAG, "Collection already exists");

                        numberDocuRef.addSnapshotListener (new EventListener<QuerySnapshot> ( ) {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                                if (queryDocumentSnapshots.isEmpty ( )) {
                                    mAdapter.insertAllIntoFireStore ( );
                                    SaveData (getActivity ( ), ISFIRST, false);
                                }
                            }
                        });
                    } else {
                        Log.d (TAG, "Collection doesn't exist create a new Collection");
                        mAdapter.insertAllIntoFireStore ( );
                        SaveData (getActivity ( ), ISFIRST, false);
                    }
                } else {
                    Log.d (TAG, "Error getting documents: ", task.getException ( ));
                }
            }
        });
    }

    public void getAllDataFromFireStore() {
        numberDocuRef
                .addSnapshotListener (new EventListener<QuerySnapshot> ( ) {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (!queryDocumentSnapshots.isEmpty ( )) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                AppExecutors.getInstance ( ).diskIO ( ).execute (new Runnable ( ) {
                                    @Override
                                    public void run() {
                                        if (mDB.numbersDao ( ).isIdExist (Integer.parseInt (String.valueOf (snapshot.get (UID)))) && LoadBoolean (getActivity ( ), ISFIRST)) {
                                            AppExecutors.getInstance ( ).mainThread ( ).execute (new Runnable ( ) {
                                                @Override
                                                public void run() {
                                                    numbers = new Numbers (1000 + Integer.parseInt (String.valueOf (snapshot.get (UID))),
                                                            snapshot.getString (TITLE), snapshot.getString (NUMBER),
                                                            snapshot.getString (NOTE), Integer.parseInt (String.valueOf (snapshot.get (FAVORITE))),
                                                            Integer.parseInt (String.valueOf (snapshot.get (DONE))),
                                                            Integer.parseInt (String.valueOf (snapshot.get (DAILY))));
                                                    mainViewModel.insertt (numbers);
                                                }
                                            });
                                        } else if (mDB.numbersDao ( ).isIdExist (Integer.parseInt (String.valueOf (snapshot.get (UID))))) {
                                            return;
                                        } else {
                                            AppExecutors.getInstance ( ).mainThread ( ).execute (new Runnable ( ) {
                                                @Override
                                                public void run() {
                                                    numbers = new Numbers (Integer.parseInt (String.valueOf (snapshot.get (UID))),
                                                            snapshot.getString (TITLE), snapshot.getString (NUMBER),
                                                            snapshot.getString (NOTE), Integer.parseInt (String.valueOf (snapshot.get (FAVORITE))),
                                                            Integer.parseInt (String.valueOf (snapshot.get (DONE))),
                                                            Integer.parseInt (String.valueOf (snapshot.get (DAILY))));
                                                    mainViewModel.insertt (numbers);
                                                }
                                            });

                                        }
                                    }
                                });
                            }
                        }
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate (R.menu.menu_home, menu);
        MenuItem item = menu.findItem (R.id.search);
        searchView.setMenuItem (item);
        searchView.setTabLayout (mainActivity.tabLayout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId ( )) {

            case R.id.sync:
                if (isConnected (getContext ()) && LoadBoolean (getActivity ( ), ISLOGIN)) {
                    syncNumberData ( );
                }
                break;

            case R.id.deletall:
                // delete all data
                showDeleteAllConfirmationDialog ( );
                break;
            case R.id.lang:
                HelperMethods.showSelectLanguageDialog (getActivity ( ), getContext ( ));
                break;
            case R.id.darkMode:
                if (AppCompatDelegate.getDefaultNightMode ( ) == AppCompatDelegate.MODE_NIGHT_YES) {
                    SharedPreferencesManger.SaveThemePref (false, getActivity ( ));
                    ThemeHelper.applyTheme (false);
                } else {
                    SharedPreferencesManger.SaveThemePref (true, getActivity ( ));
                    ThemeHelper.applyTheme (true);
                }
                restartApp ( );
                break;
            case R.id.logout:
                if (user != null) {
                    showLogoutDialog ( );
                } else {
                    SaveData (getActivity ( ), ISLOGIN, false);
                    startActivity (new Intent (getActivity ( ), UserActivity.class));
                    getActivity ().finish ();
                }
                return true;
        }
        return super.onOptionsItemSelected (item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem deleteAllItem = menu.findItem (R.id.deletall);
        if (enableMenuItem) {
            deleteAllItem.setEnabled (true);
        } else {
            // disabled
            deleteAllItem.setEnabled (false);
        }

        MenuItem syncItem = menu.findItem (R.id.sync);
        if (enableSyncMenuItem) {
            syncItem.setEnabled (true);
            syncItem.setIcon (R.drawable.ic_refresh);
        } else {
            syncItem.setEnabled (false);
            syncItem.setIcon (R.drawable.ic_refresh_false);
        }

        MenuItem darkModeItem = menu.findItem (R.id.darkMode);
        if (AppCompatDelegate.getDefaultNightMode ( ) == AppCompatDelegate.MODE_NIGHT_YES) {
            darkModeItem.setTitle (getResources ( ).getString (R.string.lightMode));
        } else {
            darkModeItem.setTitle (getResources ( ).getString (R.string.darkMode));
        }

        MenuItem item = menu.findItem (R.id.logout);
        if (user != null) {
            item.setTitle (getResources ( ).getString (R.string.logout));
        } else {
            item.setTitle (getResources ( ).getString (R.string.login));
        }
        super.onPrepareOptionsMenu (menu);
    }

    private void syncNumberData() {
        if (progressDialog == null) {
            showProgressDialog (getActivity ( ), getResources ( ).getString (R.string.refresh));
        } else {
            if (!progressDialog.isShowing ( )) {
                showProgressDialog (getActivity ( ), getResources ( ).getString (R.string.refresh));
            }
        }
        mAdapter.syncDataWithFireStore ( );
        syncDataWithRoom ( );
        final Handler handler = new Handler (Looper.getMainLooper ( ));
        handler.postDelayed (new Runnable ( ) {
            @Override
            public void run() {
                //Do something after 1000ms
                dismissProgressDialog ( );
            }
        }, 1000);
    }

    private void syncDataWithRoom() {
        numberDocuRef
                .addSnapshotListener (new EventListener<QuerySnapshot> ( ) {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (!queryDocumentSnapshots.isEmpty ( )) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                AppExecutors.getInstance ( ).diskIO ( ).execute (new Runnable ( ) {
                                    @Override
                                    public void run() {
                                        if (mDB.numbersDao ( ) != null && queryDocumentSnapshots != null) {
                                            if (mDB.numbersDao ( ).isIdExist (Integer.parseInt (String.valueOf (snapshot.get (UID))))) {

                                                return;
                                            } else{
                                                AppExecutors.getInstance ( ).mainThread ( ).execute (new Runnable ( ) {
                                                    @Override
                                                    public void run() {
                                                        numbers = new Numbers (Integer.parseInt (String.valueOf (snapshot.get (UID))),
                                                                snapshot.getString (TITLE), snapshot.getString (NUMBER),
                                                                snapshot.getString (NOTE), Integer.parseInt (String.valueOf (snapshot.get (FAVORITE))),
                                                                Integer.parseInt (String.valueOf (snapshot.get (DONE))),
                                                                Integer.parseInt (String.valueOf (snapshot.get (DAILY))));
                                                        mainViewModel.insertt (numbers);
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
    }

    private void showDeleteAllConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ( ));
        builder.setMessage (R.string.delete_all_dialog_msg);
        builder.setPositiveButton (R.string.delete, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete All" button, so delete the All numbers.
                mainViewModel.deleteAllNumbers ( );
                mAdapter.deleteAllFS ( );
                Toast.makeText (getActivity ( ), getString (R.string.Deleted), Toast.LENGTH_SHORT).show ( );
                enableSyncMenuItem = false;
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
                SaveData (getActivity ( ), ISLOGIN, false);
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

    @Override
    public void onNumberEdit(int itemId) {
        Intent intent = new Intent (getActivity ( ), NumberEditorActivity.class);
        intent.putExtra (NumberEditorActivity.EXTRA_NUMBER_ID, itemId);
        startActivity (intent);
    }

    @Override
    public void onNumberDelete(Numbers numbers, int itemId) {
        showDeleteConfirmationDialog (numbers, itemId);
    }

    public void addSomeNumber(View view) {
        Intent intent = new Intent (getActivity ( ), NumberEditorActivity.class);
        startActivity (intent);
    }

    private void showDeleteConfirmationDialog(final Numbers numbers, int itemId) {

        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ( ));
        builder.setMessage (R.string.delete_dialog_msg);
        builder.setPositiveButton (R.string.delete, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the Number.
                mainViewModel.delete (numbers);
                String uidStr = String.valueOf (itemId);
                fsDelete (numberDocuRef, uidStr);
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

    @Override
    public void onItemClickListener(View view, Numbers numbers, int pos, int itemId) {
        String numberStr = numbers.getNumber ( );

        if (mAdapter.getSelectedItemCount ( ) > 0) {
            enableActionMode (pos, view);
        } else {
            ClipboardManager clipboard = (ClipboardManager) getActivity ( ).getSystemService (Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText ("TextView", numberStr);
            clipboard.setPrimaryClip (clip);
            Toast.makeText (getActivity ( ), getString (R.string.Copied), Toast.LENGTH_SHORT).show ( );
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
            mAdapter.notifyDataSetChanged ( );
            if (fab.isShown ( )) {
                fab.hide ( );
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
            CopyAdapter.isBtnVisible = true;
            mAdapter.notifyDataSetChanged ( );
            if (!fab.isShown ( )) {
                fab.show ( );
            }
            actionMode = null;
//            HelperMethods.setSystemBarColor (getActivity ( ), R.color.colorPrimaryDark);
        }

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


    public void deleteSelection() {
        List<Integer> selectedItemPositions = mAdapter.getSelectedItems ( );
        List<Integer> selectedItemIDs = new ArrayList<> ( );
        for (int x : selectedItemPositions) {
            int selectedId = mAdapter.getId (x);
            selectedItemIDs.add (selectedId);

            String uidStr = String.valueOf (selectedId);
            fsDelete (numberDocuRef, uidStr);
        }
        mAdapter.removeSelected (selectedItemIDs);
        mAdapter.notifyDataSetChanged ( );
    }

    public void restartApp() {
        getActivity ( ).recreate ( );
    }

}
