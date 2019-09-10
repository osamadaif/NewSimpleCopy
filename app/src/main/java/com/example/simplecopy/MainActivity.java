package com.example.simplecopy;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplecopy.data.AppDatabase;
import com.example.simplecopy.data.Numbers;
import com.example.simplecopy.widgets.RecyclerViewObserver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class MainActivity extends AppCompatActivity implements CopyAdapter.ItemClickListener {

    // Member variables for the adapter and RecyclerView
    private RecyclerViewObserver mNumbersList;
    private CopyAdapter mAdapter;
    private MainViewModel mainViewModel;
    private AppDatabase mDB;
    View mEmptyView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = ViewModelProviders.of (this).get (MainViewModel.class);

        setUpRecycleView ();
        // Setup FAB to open EditorActivity
        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });


        mDB = AppDatabase.getInstance (getApplicationContext ());
        setupViewModel ( );


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



    public void setUpRecycleView(){
        mNumbersList = findViewById (R.id.recycle_list);
        mEmptyView = findViewById (R.id.empty_layout);
        mNumbersList.setHasFixedSize (true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager (this);
        mNumbersList.setLayoutManager (layoutManager);
        mNumbersList.showIfEmpty(mEmptyView);
        mAdapter = new CopyAdapter(this, this);
        mNumbersList.setAdapter (mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        mNumbersList.addItemDecoration(decoration);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater ().inflate (R.menu.menu_home, menu);
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
                        .observe (MainActivity.this, new Observer<List<Numbers>> ( ) {
                    @Override
                    public void onChanged(List<Numbers> numbers) {
                        if (numbers == null) return;
                        mAdapter.setSearchItem (numbers,newText);
                    }
                });
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId ()){

            case R.id.deletall:
                // delete all data
                showDeleteAllConfirmationDialog ( );
                return true;
        }
        return super.onOptionsItemSelected (item);
    }

    private void showDeleteConfirmationDialog(final Numbers numbers) {

        AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setMessage (R.string.delete_dialog_msg);
        builder.setPositiveButton (R.string.delete, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the Number.
                mainViewModel.delete (numbers);
                Toast.makeText (MainActivity.this, getString (R.string.Deleted), Toast.LENGTH_SHORT).show ( );

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

        AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setMessage (R.string.delete_all_dialog_msg);
        builder.setPositiveButton (R.string.delete, new DialogInterface.OnClickListener ( ) {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the number.
                mainViewModel.deleteAllNumbers ();
                Toast.makeText (MainActivity.this, getString (R.string.Deleted), Toast.LENGTH_SHORT).show ( );
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

        ClipboardManager clipboard = (ClipboardManager) getSystemService (Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText ("TextView",numberStr);
        clipboard.setPrimaryClip (clip);
        Toast.makeText (this, getString (R.string.Copied), Toast.LENGTH_SHORT).show ( );

    }

    @Override
    public void onNumberEdit(int itemId) {
        Intent intent = new Intent (MainActivity.this, EditorActivity.class);
        intent.putExtra (EditorActivity.EXTRA_NUMBER_ID, itemId);
        startActivity (intent);
    }

    @Override
    public void onNumberDelete(Numbers numbers) {
        showDeleteConfirmationDialog (numbers);

    }



    public void addSomeNumber(View view) {
        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        startActivity(intent);
    }
}