package com.example.simplecopy;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
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

import com.example.simplecopy.ViewModeler.MainViewModel;
import com.example.simplecopy.adapters.CopyAdapter;
import com.example.simplecopy.data.AppDatabase;
import com.example.simplecopy.data.Numbers;
import com.example.simplecopy.widgets.RecyclerViewObserver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class CopyNumberList extends Fragment implements CopyAdapter.ItemClickListener {
    View view;

    // Member variables for the adapter and RecyclerView
    private RecyclerViewObserver mNumbersList;
    private CopyAdapter mAdapter;
    private MainViewModel mainViewModel;
    private AppDatabase mDB;
    View mEmptyView;
    Button empty_btn;


    public CopyNumberList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate (R.layout.fragment_copy_number_list, container, false);
        setUpRecycleView ();
        mainViewModel = ViewModelProviders.of (this).get (MainViewModel.class);
        mDB = AppDatabase.getInstance (getContext ());

        setupViewModel ( );

        // Setup FAB to open EditorActivity
        FloatingActionButton fab =  view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity (), EditorActivity.class);
                startActivity(intent);
            }
        });

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

        viewModel.getNumbers ().observe ( this, new Observer<List<Numbers>> ( ) {
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
        inflater.inflate (R.menu.menu_home, menu);

        MenuItem item = menu.findItem (R.id.search);
        SearchView searchView = (SearchView)item.getActionView ();

        searchView.setOnQueryTextListener (new SearchView.OnQueryTextListener ( ) {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getResults (query);
                return true;
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
                                if (newText.length () == 0){
                                    mAdapter.setSearchItem (numbers);
                                }else{
                                    mAdapter.setSearchItem (numbers,newText);
                                }

                            }
                        });
            }
        });
        //super.onCreateOptionsMenu (menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId ()){

            case R.id.deletall:
                // delete all data
                showDeleteAllConfirmationDialog ( );
//                break;
//            case R.id.settings:
//                Intent StartSettingActivity = new Intent (getActivity (),SettingsActivity.class);
//                startActivity (StartSettingActivity);
                return true;
        }
        return super.onOptionsItemSelected (item);
    }

    public void setUpRecycleView(){
        mNumbersList = view.findViewById (R.id.recycle_list);
        mEmptyView = view.findViewById (R.id.empty_layout);
        mNumbersList.setHasFixedSize (true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager (getActivity ());
        mNumbersList.setLayoutManager (layoutManager);

        mAdapter = new CopyAdapter(getActivity (), this);

        mNumbersList.setAdapter (mAdapter);
        mNumbersList.showIfEmpty(mEmptyView);
        //((DefaultItemAnimator) mNumbersList.getItemAnimator()).setSupportsChangeAnimations(true);


        DividerItemDecoration decoration = new DividerItemDecoration(getActivity (), DividerItemDecoration.VERTICAL);
        mNumbersList.addItemDecoration(decoration);

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

    @Override
    public void onItemClickListener(Numbers numbers) {

        long numberLong = numbers.getNumber ( );
        String numberStr = String.valueOf (numberLong);

        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService (Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText ("TextView",numberStr);
        clipboard.setPrimaryClip (clip);
        Toast.makeText (getActivity (), getString (R.string.Copied), Toast.LENGTH_SHORT).show ( );

    }

    @Override
    public void onNumberEdit(int itemId) {
        Intent intent = new Intent (getActivity (), EditorActivity.class);
        intent.putExtra (EditorActivity.EXTRA_NUMBER_ID, itemId);
        startActivity (intent);
    }

    @Override
    public void onNumberDelete(Numbers numbers) {
        showDeleteConfirmationDialog (numbers);

    }

    public void addSomeNumber(View view) {
        Intent intent = new Intent(getActivity (), EditorActivity.class);
        startActivity(intent);
    }
}