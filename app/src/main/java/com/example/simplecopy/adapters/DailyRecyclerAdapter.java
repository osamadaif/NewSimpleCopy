package com.example.simplecopy.adapters;

import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplecopy.data.local.prefs.SharedPreferencesManger;
import com.example.simplecopy.utils.AppExecutors;
import com.example.simplecopy.R;
import com.example.simplecopy.data.local.database.AppDatabase;
import com.example.simplecopy.data.model.Numbers;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.LoadBoolean;
import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.LoadData;
import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.LoadHashMap;
import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.SaveData;
import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.USER_ID;
import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.USER_NAME;
//import static com.example.simplecopy.ui.fragment.MainNumbers.Daily.DailyWalletListFragment.enableMenuItemRedo;
//import static com.example.simplecopy.ui.fragment.MainNumbers.Daily.DailyWalletListFragment.enableMenuItemUndo;
import static com.example.simplecopy.utils.Constants.DAILY;
import static com.example.simplecopy.utils.Constants.DONE;
import static com.example.simplecopy.utils.Constants.ISLOGIN;
import static com.example.simplecopy.utils.Constants.NUMBERS;
import static com.example.simplecopy.utils.Constants.REDO;
import static com.example.simplecopy.utils.Constants.TITLE_NOTE;
import static com.example.simplecopy.utils.Constants.UID;
import static com.example.simplecopy.utils.Constants.UNDO;
import static com.example.simplecopy.utils.Constants.USERS;
import static com.example.simplecopy.utils.FireStoreHelperQuery.fsUpdate;
import static com.example.simplecopy.utils.HelperMethods.isConnected;

public class DailyRecyclerAdapter extends RecyclerView.Adapter<DailyRecyclerAdapter.DailyViewHolder> {
    private static final String TAG = DailyRecyclerAdapter.class.getSimpleName ( );

    private List<Numbers> mNumberList;
    //private List<Numbers> mFilteredNumberList= new ArrayList<> ();
    private Context mContext;
    private Activity activity;
    private ItemClickListener mItemClickListener;
    public String searchString = "";
    private AppDatabase mDB;
//    private Map<String, Object> undoMap;
//    private Map<String, Object> redoMap;


    private FirebaseFirestore fdb;
    private CollectionReference numberDocuRef;

    public DailyRecyclerAdapter(Context context, Activity activity, ItemClickListener listener) {
        mContext = context;
        this.mItemClickListener = listener;
        this.activity = activity;
        //mNumberListFull = new ArrayList<> (mNumberList);
    }

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from (mContext)
                .inflate (R.layout.list_daily_wallet, viewGroup, false);
        return new DailyViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DailyViewHolder holder, final int position) {
        final Numbers numbers = mNumberList.get (position);
        mDB = AppDatabase.getInstance (mContext.getApplicationContext ( ));
        fdb = FirebaseFirestore.getInstance ( );
        numberDocuRef = fdb.collection (USERS)
                .document (LoadData (activity, USER_NAME) + " " + LoadData (activity, USER_ID))
                .collection (NUMBERS);
        String title = numbers.getTitle ( );
        holder.mTitleTextView.setText (title);
        holder.setPosition (position);

        int numberInt = numbers.getDaily ( );
        String numberStr = String.valueOf (numberInt);
        holder.mNumberTextView.setText (numberStr);
        String uidStr = String.valueOf (mNumberList.get (position).getId ( ));
        holder.mClear_btn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    if (numbers.getDaily ( ) == 0 || holder.mNumberTextView.getText ( ) == "0") {
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder (mContext);
                    builder.setMessage (R.string.clear_dialog_msg_value);
                    builder.setPositiveButton (R.string.clear, new DialogInterface.OnClickListener ( ) {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked the "Delete" button, so delete the Number.
                            AppExecutors.getInstance ( ).diskIO ( ).execute (new Runnable ( ) {
                                @Override
                                public void run() {
                                    final int dailyNumber = 0;
                                    int oldDaily = numbers.getDaily ( );
                                    String oldDailyString = String.valueOf (oldDaily);
//                                    undoMap = new HashMap<> ( );
//                                    undoMap.put (UID, uidStr);
//                                    undoMap.put (DAILY, oldDailyString);
                                    if (numbers.getDone ( ) == 1) {
//                                        undoMap.put (DONE, "1");
                                        mDB.numbersDao ( ).insertIfDone (0, mNumberList.get (position).getId ( ));
                                        Map<String, Object> doneMap = new HashMap<> ( );
                                        doneMap.put (DONE , "0");
                                        fsUpdate (numberDocuRef, uidStr, doneMap);
                                    }
//                                    else {
//                                        undoMap.put (DONE, "0");
//                                    }
//                                    SaveData (activity, UNDO, undoMap);
//                                    enableMenuItemUndo = true;
//                                    activity.invalidateOptionsMenu ( );
//                                    enableMenuItemRedo = false;
                                    mDB.numbersDao ( ).insertDaily (dailyNumber, mNumberList.get (position).getId ( ));
                                    Map<String, Object> dailyeMap = new HashMap<> ( );
                                    dailyeMap.put (DAILY , "0");
                                    fsUpdate (numberDocuRef, uidStr, dailyeMap);

                                    AppExecutors.getInstance ( ).mainThread ( ).execute (new Runnable ( ) {
                                        @Override
                                        public void run() {
                                            notifyDataSetChanged ( );
                                        }
                                    });
                                }
                            });
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
        });

        // Spannable HighLight Work
        String sTitle = title.toLowerCase (Locale.getDefault ( ));
        if (!searchString.isEmpty ( )) {
            Log.d (TAG, "searchString != null");
            int startPos = sTitle.indexOf (searchString);
            int endPos = startPos + searchString.length ( );

            if (startPos != -1) {
                Log.d (TAG, "start pos != -1");
                Spannable spanStringTitle = Spannable.Factory.getInstance ( ).newSpannable (holder.mTitleTextView.getText ( ));
                spanStringTitle.setSpan (new ForegroundColorSpan (Color.parseColor ("#F9AA33")), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                holder.mTitleTextView.setText (spanStringTitle);

            } else {
                Log.d (TAG, "start pos == -1");
                holder.mTitleTextView.setText (title);
                //holder.mTitleTextView.setTextColor(Color.parseColor ("#4A6572"));
            }

        } else {
            Log.d (TAG, "searchString == null");
            holder.mTitleTextView.setText (title);
            //holder.mTitleTextView.setTextColor(Color.parseColor ("#4A6572"));
        }


        String sNumber = numberStr.toLowerCase (Locale.getDefault ( ));
        if (sNumber.contains (searchString)) {
            int startPos = sNumber.indexOf (searchString);
            int endPos = startPos + searchString.length ( );

            Spannable spanStringNumber = Spannable.Factory.getInstance ( ).newSpannable (holder.mNumberTextView.getText ( ));
            spanStringNumber.setSpan (new ForegroundColorSpan (Color.parseColor ("#F9AA33")), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.mNumberTextView.setText (spanStringNumber);
        }

        holder.mPlus_btn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService (Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow (holder.mPlus_btn.getWindowToken ( ), 0);
                holder.mAddNumber.setFocusable (false);
                holder.mAddNumber.setFocusableInTouchMode (true);
                AppExecutors.getInstance ( ).diskIO ( ).execute (new Runnable ( ) {
                    @Override
                    public void run() {
                        //final int favorite = getFav (numbers);
                        if (mItemClickListener != null) {
                            if (holder.mAddNumber.getText ( ) == null || holder.mAddNumber.getText ( ).length ( ) == 0) {
                                return;
                            }
                            int oldDaily = numbers.getDaily ( );
//                            undoMap = new HashMap<> ( );
                            String oldDailyString = String.valueOf (oldDaily);
//                            undoMap.put (UID, uidStr);
//                            undoMap.put (DAILY, oldDailyString);
//                            SaveData (activity, UNDO, undoMap);
//                            enableMenuItemUndo = true;
//                            activity.invalidateOptionsMenu ( );
//                            enableMenuItemRedo = false;
                            String numbersString = holder.mAddNumber.getText ( ).toString ( ).trim ( );
                            final int dailyNumber = Integer.parseInt (numbersString);
                            //final Numbers numbers1 = new Numbers (titleString,dailyNumber);
                            int finalDaily = oldDaily + dailyNumber;
                            mDB.numbersDao ( ).insertDaily (finalDaily, mNumberList.get (position).getId ( ));
                            String finalDailyStr = String.valueOf (finalDaily);
                            Map<String, Object> dailyPlusMap = new HashMap<> ( );
                            dailyPlusMap.put (DAILY , finalDailyStr);
                            fsUpdate (numberDocuRef, uidStr, dailyPlusMap);
                            AppExecutors.getInstance ( ).mainThread ( ).execute (new Runnable ( ) {
                                @Override
                                public void run() {
                                    holder.mAddNumber.setText ("");
                                    notifyDataSetChanged ( );
                                }
                            });
                        }
                    }
                });
            }
        });

        holder.mMinus_btn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService (Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow (holder.mPlus_btn.getWindowToken ( ), 0);
                holder.mAddNumber.setFocusable (false);
                holder.mAddNumber.setFocusableInTouchMode (true);
                AppExecutors.getInstance ( ).diskIO ( ).execute (new Runnable ( ) {
                    @Override
                    public void run() {
                        //final int favorite = getFav (numbers);
                        if (mItemClickListener != null) {
                            if (holder.mAddNumber.getText ( ) == null || holder.mAddNumber.getText ( ).length ( ) == 0) {
                                return;
                            }
                            int oldDaily = numbers.getDaily ( );
                            String numbersString = holder.mAddNumber.getText ( ).toString ( ).trim ( );
                            final int dailyNumber = Integer.parseInt (numbersString);
                            //final Numbers numbers1 = new Numbers (titleString,dailyNumber);
                            int finalDaily = oldDaily - dailyNumber;
                            if (finalDaily <= 0) {
                                AppExecutors.getInstance ( ).mainThread ( ).execute (new Runnable ( ) {
                                    @Override
                                    public void run() {
                                        holder.mAddNumber.setError (mContext.getResources ( ).getString (R.string.invalid_value));

                                    }
                                });

                            } else {
//                                undoMap = new HashMap<> ( );
                                String oldDailyString = String.valueOf (oldDaily);
//                                undoMap.put (UID, uidStr);
//                                undoMap.put (DAILY, oldDailyString);
//                                SaveData (activity, UNDO, undoMap);
//                                enableMenuItemUndo = true;
//                                activity.invalidateOptionsMenu ( );
//                                enableMenuItemRedo = false;
                                mDB.numbersDao ( ).insertDaily (finalDaily, mNumberList.get (position).getId ( ));
                                String finalDailyStr = String.valueOf (finalDaily);
                                Map<String, Object> dailyMinusMap = new HashMap<> ( );
                                dailyMinusMap.put (DAILY , finalDailyStr);
                                fsUpdate (numberDocuRef, uidStr, dailyMinusMap);

                                AppExecutors.getInstance ( ).mainThread ( ).execute (new Runnable ( ) {
                                    @Override
                                    public void run() {
                                        holder.mAddNumber.setText ("");
                                        notifyDataSetChanged ( );
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });

        if (numbers.getDone ( ) == 1) {
            holder.mTitleTextView.setPaintFlags (holder.mTitleTextView.getPaintFlags ( ) | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mTitleTextView.setTextColor (mContext.getResources ( ).getColor (R.color.red));
            holder.mNumberTextView.setTextColor (mContext.getResources ( ).getColor (R.color.red));
        }
        else if (numbers.getDone ( ) == 0) {
            holder.mTitleTextView.setPaintFlags (holder.mTitleTextView.getPaintFlags ( ) & (~Paint.STRIKE_THRU_TEXT_FLAG));
            if (SharedPreferencesManger.getThemePref (mContext)){
                holder.mTitleTextView.setTextColor (mContext.getResources ( ).getColor (R.color.clolrWhite));
                holder.mNumberTextView.setTextColor (mContext.getResources ( ).getColor (R.color.clolrWhite));
            }else {
                holder.mTitleTextView.setTextColor (mContext.getResources ( ).getColor (R.color.colorPrimaryLight));
                holder.mNumberTextView.setTextColor (mContext.getResources ( ).getColor (R.color.colorPrimaryLight));
            }

        }
    }

//    public void getUndo(){
//        AppExecutors.getInstance ( ).diskIO ( ).execute (new Runnable ( ) {
//            @Override
//            public void run() {
//                Log.d (TAG, "getUndo: save redo");
//                Map<String, String> undoDataMap = LoadHashMap (activity, UNDO);
//                String idRedo = (String) undoDataMap.get (UID);
//                int idRedoInt = Integer.parseInt (idRedo);
//                redoMap = new HashMap<> ( );
//                redoMap.put (UID, idRedo);
//                if (undoDataMap.get (DAILY) != null){
//                    int redoDailyNumber = mDB.numbersDao ().getDailyDB (idRedoInt);
//                    String redoDailyString = String.valueOf (redoDailyNumber);
//                    redoMap.put (DAILY, redoDailyString);
//                }
//                if (undoDataMap.get (DONE) != null){
//                    int redoDoneNumber = mDB.numbersDao ().getDoneDB (idRedoInt);
//                    redoMap.put (DONE, redoDoneNumber);
//                }
//                SaveData (activity, REDO, redoMap);
//
//                Log.d (TAG, "getUndo: retrive undo");
//                String id = undoDataMap.get (UID);
//                int idInt = Integer.parseInt (id);
//                if (undoDataMap.get (DAILY) != null){
//                    String daily = undoDataMap.get (DAILY);
//                    int dailyNumber = Integer.parseInt (daily);
//                    mDB.numbersDao ( ).insertDaily (dailyNumber, idInt);
//                    Map<String, Object> dailyMap = new HashMap<> ( );
//                    dailyMap.put (DAILY , daily);
//                    fsUpdate (numberDocuRef, id, dailyMap);
//                }
//                if (undoDataMap.get (DONE) != null){
//                    String done = undoDataMap.get (DONE);
//                    int doneNumber = Integer.parseInt (done);
//                    mDB.numbersDao ( ).insertIfDone (doneNumber, idInt);
//                    if (isConnected (activity) && LoadBoolean (activity, ISLOGIN)) {
//                        Map<String, Object> doneMap = new HashMap<> ( );
//                        doneMap.put (DONE, done);
//                        fsUpdate (numberDocuRef, id, doneMap);
//                    }
//                }
//                AppExecutors.getInstance ( ).mainThread ( ).execute (new Runnable ( ) {
//                    @Override
//                    public void run() {
//                        notifyDataSetChanged ( );
//                    }
//                });
//            }
//        });
//        enableMenuItemUndo = false;
//        enableMenuItemRedo = true;
//        activity.invalidateOptionsMenu ( );
//    }

//    public void getRedo(){
//        AppExecutors.getInstance ( ).diskIO ( ).execute (new Runnable ( ) {
//            @Override
//            public void run() {
//                Log.d (TAG, "getRedo: save undo");
//                Map<String, String> redoDataMap = LoadHashMap (activity, REDO);
//                String idUndo = redoDataMap.get (UID);
//                int idUndoInt = Integer.parseInt (idUndo);
//                undoMap = new HashMap<> ( );
//                undoMap.put (UID, idUndo);
//                if (redoDataMap.get (DAILY) != null){
//                    int undoDailyNumber = mDB.numbersDao ().getDailyDB (idUndoInt);
//                    String undoDailyString = String.valueOf (undoDailyNumber);
//                    undoMap.put (DAILY, undoDailyString);
//                }
//                if (redoDataMap.get (DONE) != null){
//                    int undoDoneNumber = mDB.numbersDao ().getDoneDB (idUndoInt);
//                    undoMap.put (DONE, undoDoneNumber);
//                }
//                SaveData (activity, UNDO, undoMap);
//
//                Log.d (TAG, "getRedo: retrive redo");
//                String id = redoDataMap.get (UID);
//                int idInt = Integer.parseInt (id);
//                if (redoDataMap.get (DAILY) != null){
//                    String daily = redoDataMap.get (DAILY);
//                    int dailyNumber = Integer.parseInt (daily);
//                    mDB.numbersDao ( ).insertDaily (dailyNumber, idInt);
//                    Map<String, Object> dailyMap = new HashMap<> ( );
//                    dailyMap.put (DAILY , daily);
//                    fsUpdate (numberDocuRef, id, dailyMap);
//                }
//                if (redoDataMap.get (DONE) != null){
//                    String done = redoDataMap.get (DONE);
//                    int doneNumber = Integer.parseInt (done);
//                    mDB.numbersDao ( ).insertIfDone (doneNumber, idInt);
//                    if (isConnected (activity) && LoadBoolean (activity, ISLOGIN)){
//                        Map<String, Object> doneMap = new HashMap<> ( );
//                        doneMap.put (DONE , done);
//                        fsUpdate (numberDocuRef, id, doneMap);
//                    }
//                }
//                AppExecutors.getInstance ( ).mainThread ( ).execute (new Runnable ( ) {
//                    @Override
//                    public void run() {
//                        notifyDataSetChanged ( );
//                    }
//                });
//            }
//        });
//        enableMenuItemUndo = true;
//        activity.invalidateOptionsMenu ( );
//        enableMenuItemRedo = false;
//    }

    @Override
    public int getItemCount() {
        if (mNumberList == null || mNumberList.isEmpty ( )) {
            return 0;
        }
        return mNumberList.size ( );
    }

    class DailyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitleTextView;
        TextView mNumberTextView;
        ImageView mPlus_btn;
        ImageView mMinus_btn;
        Button mClear_btn;
        AppCompatEditText mAddNumber;

        private int mPosition;
        LinearLayout container;

        public DailyViewHolder(@NonNull View itemView) {
            super (itemView);
            container = itemView.findViewById (R.id.container2);
            mTitleTextView = itemView.findViewById (R.id.title_daily);
            mNumberTextView = itemView.findViewById (R.id.number_daily);
            mPlus_btn = itemView.findViewById (R.id.btn_Enter);
            mMinus_btn = itemView.findViewById (R.id.btn_minus);
            mAddNumber = itemView.findViewById (R.id.edit_daily);
            mClear_btn = itemView.findViewById (R.id.clear);
            mAddNumber.setMaxWidth (7);
            mNumberTextView.setMaxWidth (7);

            mPlus_btn.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    int elementId = mNumberList.get (mPosition).getId ( );
                    mItemClickListener.onNumberSubmit (elementId);
                }
            });

            mClear_btn.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    int elementId = mNumberList.get (mPosition).getId ( );
                    mItemClickListener.onNumberClear (elementId);
                }
            });
            itemView.setOnClickListener (this);
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition ( ) != RecyclerView.NO_POSITION && mItemClickListener != null) {
                mItemClickListener.onItemClickListener (mNumberList.get (mPosition));
            }
        }

        public void setPosition(int position) {
            mPosition = position;
        }
    }

    public void setSearchItem(List<Numbers> newList, String searchString) {

        mNumberList = new ArrayList<> ( );
        mNumberList.addAll (newList);
        this.searchString = searchString;
        notifyDataSetChanged ( );
    }

    public void setSearchItem(List<Numbers> newList) {
        mNumberList = new ArrayList<> ( );
        mNumberList.addAll (newList);
        notifyDataSetChanged ( );
    }

    public interface ItemClickListener {
        void onItemClickListener(Numbers numbers);
        void onNumberSubmit(int itemId);
        void onNumberClear(int itemId);
    }

    public List<Numbers> getItems() {
        //mNumberListFull = new ArrayList<> (mNumberList);
        return mNumberList;
    }

    public void setItems(List<Numbers> itemList) {
        // mNumberListFull = itemList;
        mNumberList = itemList;
        //mFilteredNumberList.addAll(mNumberList);
        notifyDataSetChanged ( );
    }

}
