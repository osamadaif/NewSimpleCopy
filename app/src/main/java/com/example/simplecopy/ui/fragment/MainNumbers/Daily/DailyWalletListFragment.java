package com.example.simplecopy.ui.fragment.MainNumbers.Daily;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.simplecopy.data.local.prefs.SharedPreferencesManger;
import com.example.simplecopy.ui.activity.MainActivity;
import com.example.simplecopy.ui.activity.NumberEditor.NumberEditorActivity;
import com.example.simplecopy.ui.activity.user.UserActivity;
import com.example.simplecopy.utils.AppExecutors;
import com.example.simplecopy.R;
import com.example.simplecopy.ui.fragment.MainNumbers.MainViewModel;
import com.example.simplecopy.adapters.DailyRecyclerAdapter;
import com.example.simplecopy.data.local.database.AppDatabase;
import com.example.simplecopy.data.model.Numbers;
import com.example.simplecopy.utils.HelperMethods;
import com.example.simplecopy.utils.ThemeHelper;
import com.ferfalk.simplesearchview.SimpleSearchView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.LoadData;
import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.SaveData;
import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.USER_ID;
import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.USER_NAME;
import static com.example.simplecopy.utils.Constants.DONE;
import static com.example.simplecopy.utils.Constants.ISLOGIN;
import static com.example.simplecopy.utils.Constants.NUMBERS;
import static com.example.simplecopy.utils.Constants.USERS;
import static com.example.simplecopy.utils.FireStoreHelperQuery.fsUpdate;


public class DailyWalletListFragment extends Fragment implements DailyRecyclerAdapter.ItemClickListener {
    View view;
    private static final String TAG = "DailyWalletListFragment";

    private MainViewModel mainViewModel;
    private RecyclerView mNumbersList;
    private DailyRecyclerAdapter mAdapter;
    private AppDatabase mDB;
    private MainActivity mainActivity;
    //FireBase auth
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private FirebaseFirestore fdb;
    private CollectionReference numberDocRef;

    private View mEmptyView;
    Button empty_btn;

    private SimpleSearchView searchView;

    // Required empty public constructor
    public DailyWalletListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate (R.layout.fragment_daily__wallet, container, false);
        mainViewModel = new ViewModelProvider (this).get (MainViewModel.class);
        setUpRecycleView ();
        mDB = AppDatabase.getInstance (getContext ());
        firebaseAuth = FirebaseAuth.getInstance ();
        user = firebaseAuth.getCurrentUser ();
        fdb = FirebaseFirestore.getInstance ( );
        numberDocRef = fdb.collection (USERS)
                .document (LoadData (getActivity (), USER_NAME) + " " + LoadData (getActivity (), USER_ID))
                .collection (NUMBERS);
        mainActivity =(MainActivity)this.getActivity();
        searchView = getActivity ().findViewById(R.id.searchView);
        setupViewModel ( );
        empty_btn = view.findViewById (R.id.btn_empty_title);
        empty_btn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity (), NumberEditorActivity.class));
            }
        });
        return view;
    }

    public void setUpRecycleView(){
        mNumbersList = view.findViewById (R.id.recycle_daily_list);
        mEmptyView = view.findViewById (R.id.empty_layout_daily);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager (getActivity ());
        mNumbersList.setLayoutManager (layoutManager);
//        mNumbersList.addItemDecoration(new DividerItemDecoration (Objects.requireNonNull (getContext ( )), 0));
        mNumbersList.setItemAnimator (new DefaultItemAnimator ());
        mNumbersList.setHasFixedSize (true);
        mAdapter = new DailyRecyclerAdapter(getActivity (),getActivity (), this);
        //mAdapter.setHasStableIds (true);
        mNumbersList.setAdapter (mAdapter);
        Map<String, Object> numbersMap = new HashMap<> ( );

        new ItemTouchHelper (new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to set is done
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {

                        final int position = viewHolder.getAdapterPosition();
                        //final Numbers numbers = mNumber.get (position);
                        final List<Numbers> number = mAdapter.getItems ();
                        String uidStr = String.valueOf (number.get (position).getId ());
                        if (number.get (position).getDaily () != 0){
                        if (number.get(position).getDone () == 0){
                            mDB.numbersDao ().insertIfDone (1, number.get (position).getId ());
                            Map<String, Object> doneMapP = new HashMap<> ( );
                            doneMapP.put (DONE , "1");
                            fsUpdate (numberDocRef, uidStr, doneMapP);

                        } else if (number.get(position).getDone () == 1){
                            mDB.numbersDao ().insertIfDone (0, number.get (position).getId ());
                            Map<String, Object> doneMapM = new HashMap<> ( );
                            doneMapM.put (DONE , "0");
                            fsUpdate (numberDocRef, uidStr, doneMapM);
                        }}
                        AppExecutors.getInstance().mainThread ().execute (new Runnable ( ) {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged ();
                            }
                        });
                    }

                });
            }
        }).attachToRecyclerView(mNumbersList);

    }


    private void setupViewModel() {
        MainViewModel viewModel = new ViewModelProvider (this).get(MainViewModel.class);
        viewModel.getSearchQueryByDaily ().observe ( getViewLifecycleOwner(), new Observer<List<Numbers>> ( ) {
            @Override
            public void onChanged(List<Numbers> numbersList1) {
                if (numbersList1.isEmpty ()){
                    mNumbersList.setVisibility (View.GONE);
                    if (mAdapter.getItemCount () == 0){
                        mEmptyView.setVisibility (View.VISIBLE);
                    } else {
                        mEmptyView.setVisibility (View.GONE);
                    }

                }else {
                    mNumbersList.setVisibility (View.VISIBLE);
                    mEmptyView.setVisibility (View.GONE);
                    mAdapter.setItems (numbersList1);

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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate (R.menu.menu_daily, menu);
        final MenuItem item = menu.findItem (R.id.searchDaily);
        searchView.setMenuItem(item);
        searchView.setTabLayout( mainActivity.tabLayout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId ()){
            case R.id.lang_daily:
                HelperMethods.showSelectLanguageDialog (getActivity (), getContext ());
                break;
            case R.id.darkMode_daily:
                if (AppCompatDelegate.getDefaultNightMode () == AppCompatDelegate.MODE_NIGHT_YES){
                    SharedPreferencesManger.SaveThemePref (false, getActivity ());
                    ThemeHelper.applyTheme (false);
                } else {
                    SharedPreferencesManger.SaveThemePref (true, getActivity ());
                    ThemeHelper.applyTheme (true);
                }
                restartApp ();
                break;
            case R.id.logout_daily:
                if (user != null){
                    showLogoutDialog ();
                } else {
                    SaveData (getActivity (), ISLOGIN, false);
                    startActivity (new Intent (getActivity (), UserActivity.class));
                }

        }
        return super.onOptionsItemSelected (item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem darkModeItem = menu.findItem (R.id.darkMode_daily);
        if (AppCompatDelegate.getDefaultNightMode () == AppCompatDelegate.MODE_NIGHT_YES) {
            darkModeItem.setTitle (getResources ( ).getString (R.string.lightMode));
        } else {
            darkModeItem.setTitle (getResources ( ).getString (R.string.darkMode));
        }

        MenuItem item = menu.findItem (R.id.logout_daily);
        if (user != null){
            item.setTitle (getResources ().getString (R.string.logout));
        } else {
            item.setTitle (getResources ().getString (R.string.login));
        }
        super.onPrepareOptionsMenu (menu);
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ());
        builder.setMessage (R.string.logout_dialog_msg);
        builder.setPositiveButton (R.string.logout, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {
                firebaseAuth.signOut ();
                SaveData (getActivity (), ISLOGIN, false);
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

    @Override
    public void onItemClickListener(Numbers numbers) {
        String numberStr = numbers.getNumber ( );
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService (Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText ("TextView",numberStr);
        clipboard.setPrimaryClip (clip);
        Toast.makeText (getActivity (), getString (R.string.Copied), Toast.LENGTH_SHORT).show ( );
    }

    @Override
    public void onNumberSubmit(int itemId) { }

    @Override
    public void onNumberClear(int itemId) { }

    public void restartApp()
    {
        getActivity ().recreate ();
    }
}
