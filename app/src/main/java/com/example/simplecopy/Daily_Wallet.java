package com.example.simplecopy;

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

import com.example.simplecopy.ViewModeler.MainViewModel;
import com.example.simplecopy.adapters.DailyRecyclerAdapter;
import com.example.simplecopy.data.AppDatabase;
import com.example.simplecopy.data.Numbers;

import java.util.List;
import java.util.Objects;


public class Daily_Wallet extends Fragment implements DailyRecyclerAdapter.ItemClickListener {
    View view;

    private List<Numbers> mNumber;
    private MainViewModel mainViewModel;
    private RecyclerView mNumbersList;
    private DailyRecyclerAdapter mAdapter;
    private AppDatabase mDB;

    public Daily_Wallet() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate (R.layout.fragment_daily__wallet, container, false);
        mainViewModel = ViewModelProviders.of (this).get (MainViewModel.class);
        setUpRecycleView ();



        new ItemTouchHelper (new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {

                        final int position = viewHolder.getAdapterPosition();
                        //final Numbers numbers = mNumber.get (position);
                        final List<Numbers> number = mAdapter.getItems ();

                        if (number.get(position).getDone () == 0){
                        mDB.numbersDao ().insertIfDone (1, number.get (position).getId ());

                        } else if (number.get(position).getDone () == 1){
                            mDB.numbersDao ().insertIfDone (0, number.get (position).getId ());
                        }
                        AppExecutors.getInstance().mainThread ().execute (new Runnable ( ) {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged ();

                                //mNumbersList.getLayoutManager ().smoothScrollToPosition ();

                            }
                        });
                    }

                });
            }
        }).attachToRecyclerView(mNumbersList);

        mDB = AppDatabase.getInstance (getContext ());
        setupViewModel ( );
        return view;
    }

    public void setUpRecycleView(){
        mNumbersList = view.findViewById (R.id.recycle_daily_list);
        mNumbersList.setHasFixedSize (true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager (getActivity ());
        mNumbersList.setLayoutManager (layoutManager);
        mAdapter = new DailyRecyclerAdapter(getActivity (), this);
        mAdapter.setHasStableIds (true);

        mNumbersList.setAdapter (mAdapter);
        //((DefaultItemAnimator) mNumbersList.getItemAnimator()).setSupportsChangeAnimations(true);


        DividerItemDecoration decoration = new DividerItemDecoration(getActivity (), DividerItemDecoration.VERTICAL);
        mNumbersList.addItemDecoration(decoration);
        mNumbersList.setItemAnimator (new DefaultItemAnimator ());

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



        searchView.setOnQueryTextListener (new SearchView.OnQueryTextListener ( ) {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getResults (query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getResults (newText);
                return false;
            }



            private void getResults (final String newText){
                mainViewModel.searchQueryByDaily (newText)
                        .observe (Objects.requireNonNull (getActivity ( )), new Observer<List<Numbers>> ( ) {
                            @Override
                            public void onChanged(List<Numbers> numbers) {
                                if (newText.length () == 0){
                                    mAdapter.setSearchItem (numbers);
                                }else{
                                    mAdapter.setSearchItem (numbers,newText);
                                }

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
