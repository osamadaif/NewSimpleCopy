package com.example.simplecopy;

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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.example.simplecopy.ViewModeler.MainViewModel;
import com.example.simplecopy.adapters.DailyRecyclerAdapter;
import com.example.simplecopy.data.AppDatabase;
import com.example.simplecopy.data.Numbers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Daily_Wallet extends Fragment implements DailyRecyclerAdapter.ItemClickListener {
    View view;

    private MainViewModel mainViewModel;
    private RecyclerView mNumbersList;
    private DailyRecyclerAdapter mAdapter;
    private AppDatabase mDB;

    // Required empty public constructor
    public Daily_Wallet() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate (R.layout.fragment_daily__wallet, container, false);
        mainViewModel = ViewModelProviders.of (this).get (MainViewModel.class);
        setUpRecycleView ();
        mDB = AppDatabase.getInstance (getContext ());
        setupViewModel ( );
        return view;
    }

    public void setUpRecycleView(){
        mNumbersList = view.findViewById (R.id.recycle_daily_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager (getActivity ());
        mNumbersList.setLayoutManager (layoutManager);
        mNumbersList.addItemDecoration(new DividerItemDecoration (Objects.requireNonNull (getContext ( )), 0));
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
                mAdapter.setItems (numbersList1);
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

//        searchView.setOnQueryTextFocusChangeListener (new View.OnFocusChangeListener ( ) {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus){
//                    item.collapseActionView ();
//                    getResults (query);
//
//                }
//            }
//        });

        //searchView.clearFocus ();

        //super.onCreateOptionsMenu (menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId ()){
//
//            case R.id.settings_daily:
//                Intent StartSettingActivity = new Intent (getActivity (),SettingsActivity.class);
//                startActivity (StartSettingActivity);
//                return true;
//        }
        return super.onOptionsItemSelected (item);
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
