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
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.simplecopy.ui.activity.NumberEditor.NumberEditorActivity;
import com.example.simplecopy.ui.activity.SettingsActivity;
import com.example.simplecopy.ui.activity.user.UserActivity;
import com.example.simplecopy.ui.fragment.MainNumbers.Numbers.NumberListFragment;
import com.example.simplecopy.utils.AppExecutors;
import com.example.simplecopy.R;
import com.example.simplecopy.ui.fragment.MainNumbers.MainViewModel;
import com.example.simplecopy.adapters.DailyRecyclerAdapter;
import com.example.simplecopy.data.local.database.AppDatabase;
import com.example.simplecopy.data.model.Numbers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Objects;


public class DailyWalletListFragment extends Fragment implements DailyRecyclerAdapter.ItemClickListener {
    View view;

    private MainViewModel mainViewModel;
    private RecyclerView mNumbersList;
    private DailyRecyclerAdapter mAdapter;
    private AppDatabase mDB;
    //FireBase auth
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private View mEmptyView;
    Button empty_btn;

    // Required empty public constructor
    public DailyWalletListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate (R.layout.fragment_daily__wallet, container, false);
        mainViewModel = ViewModelProviders.of (this).get (MainViewModel.class);
        setUpRecycleView ();
        mDB = AppDatabase.getInstance (getContext ());
        firebaseAuth = FirebaseAuth.getInstance ();
        user = firebaseAuth.getCurrentUser ();
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
        mAdapter = new DailyRecyclerAdapter(getActivity (), this);
        //mAdapter.setHasStableIds (true);
        mNumbersList.setAdapter (mAdapter);

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

                        if (number.get (position).getDaily () != 0){
                        if (number.get(position).getDone () == 0){
                            mDB.numbersDao ().insertIfDone (1, number.get (position).getId ());

                        } else if (number.get(position).getDone () == 1){
                            mDB.numbersDao ().insertIfDone (0, number.get (position).getId ());
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
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getDailyFirst ().observe ( this, new Observer<List<Numbers>> ( ) {
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate (R.menu.menu_daily, menu);
        final MenuItem item = menu.findItem (R.id.searchDaily);
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
//                newText = newText.toLowerCase();
//                List<Numbers> newList = new ArrayList<> ();

                mainViewModel.searchQueryByDaily (newText)
                        .observe (Objects.requireNonNull (getActivity ( )), new Observer<List<Numbers>> ( ) {
                            @Override
                            public void onChanged(List<Numbers> numbers) {

                                if (numbers == null)return;
                                     mAdapter.setSearchItem (numbers,newText);
                            }
                        });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId ()){
            case R.id.settings_daily:
                startActivity (new Intent (getActivity (), SettingsActivity.class));
                getActivity ().finish ();
                return true;
            case R.id.logout_daily:
                if (user != null){
                    showLogoutDialog ();
                } else {
                    startActivity (new Intent (getActivity (), UserActivity.class));
                }
        }
        return super.onOptionsItemSelected (item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
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
    public void onNumberSubmit(int itemId) {
//        mAdapter.notifyItemMoved (itemId, mNumbersList.getAdapter ().getItemCount ());
//        mNumbersList.smoothScrollToPosition (itemId);

    }

    @Override
    public void onNumberClear(int itemId) {
//        mAdapter.notifyItemMoved (itemId, mNumbersList.getAdapter ().getItemCount ());
//        mNumbersList.smoothScrollToPosition (itemId);

    }
}
