package com.example.simplecopy.adapters;

import androidx.appcompat.app.AlertDialog;
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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplecopy.utils.AppExecutors;
import com.example.simplecopy.R;
import com.example.simplecopy.data.local.database.AppDatabase;
import com.example.simplecopy.data.model.Numbers;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DailyRecyclerAdapter extends RecyclerView.Adapter<DailyRecyclerAdapter.DailyViewHolder>   {
    private static final String TAG = DailyRecyclerAdapter.class.getSimpleName ( );

    private List<Numbers> mNumberList;
    //private List<Numbers> mFilteredNumberList= new ArrayList<> ();
    private Context mContext;
    private ItemClickListener mItemClickListener;
    private String searchString="";
    private AppDatabase mDB;

    public DailyRecyclerAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        this.mItemClickListener = listener;
        //mNumberListFull = new ArrayList<> (mNumberList);
    }

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from (mContext)
                .inflate (R.layout.list_daily_wallet, viewGroup,false);
        return new DailyViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DailyViewHolder holder, final int position) {
        final Numbers numbers = mNumberList.get (position);
        mDB = AppDatabase.getInstance (mContext.getApplicationContext ( ));

        String title = numbers.getTitle ( );
        holder.mTitleTextView.setText (title);
        holder.setPosition (position);

        int numberInt = numbers.getDaily ( );
        String numberStr = String.valueOf (numberInt);
        holder.mNumberTextView.setText (numberStr);





        holder.mClear_btn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null ){
                    if (numbers.getDaily () == 0 || holder.mNumberTextView.getText ( ) == "0")
                    {
                        return;
                    }
                AlertDialog.Builder builder = new AlertDialog.Builder (mContext);
                builder.setMessage (R.string.clear_dialog_msg);
                builder.setPositiveButton (R.string.clear, new DialogInterface.OnClickListener ( ) {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked the "Delete" button, so delete the Number.
                        AppExecutors.getInstance ().diskIO ().execute (new Runnable ( ) {
                            @Override
                            public void run() {


                                    final int dailyNumber = 0;
                                    mDB.numbersDao ().insertDaily (dailyNumber, mNumberList.get (position).getId () );
                                    AppExecutors.getInstance ().mainThread ().execute (new Runnable ( ) {
                                        @Override
                                        public void run() {
                                            notifyDataSetChanged ();

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

        String sTitle = title.toLowerCase (Locale.getDefault ());
        if (searchString != null && !searchString.isEmpty () && sTitle.contains (searchString )){
            Log.d (TAG,"searchString != null");
            int startPos = sTitle.indexOf(searchString);
            int endPos = startPos + searchString.length();

            if (startPos != -1){
                Log.d (TAG,"start pos != -1");
                Spannable spanString = Spannable.Factory.getInstance().newSpannable(holder.mTitleTextView.getText());
                spanString.setSpan(new ForegroundColorSpan (Color.parseColor ("#F9AA33")), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                holder.mTitleTextView.setText(spanString);
            } else {
                Log.d (TAG,"start pos == -1");
                holder.mTitleTextView.setText (title);
                //holder.mTitleTextView.setTextColor(Color.parseColor ("#4A6572"));
            }

        }
        else {
            Log.d (TAG,"searchString == null");
            holder.mTitleTextView.setText (title);
            //holder.mTitleTextView.setTextColor(Color.parseColor ("#4A6572"));
        }


        String sNumber = numberStr.toLowerCase (Locale.getDefault ());
        if (sNumber.contains (searchString)){
            int startPos = sNumber.indexOf(searchString);
            int endPos = startPos + searchString.length();

            Spannable spanString = Spannable.Factory.getInstance().newSpannable(holder.mNumberTextView.getText());
            spanString.setSpan(new ForegroundColorSpan (Color.parseColor ("#F9AA33")), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.mNumberTextView.setText(spanString);
        }

        holder.mEnter_btn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(holder.mEnter_btn.getWindowToken(), 0);
                holder.mAddNumber.setFocusable(false);
                holder.mAddNumber.setFocusableInTouchMode(true);
                AppExecutors.getInstance ().diskIO ().execute (new Runnable ( ) {
                    @Override
                    public void run() {
                        //final int favorite = getFav (numbers);
                        if (mItemClickListener != null){
                            if (holder.mAddNumber.getText ( ) == null || holder.mAddNumber.getText ( ).length () == 0)
                            {
                                return;
                            }
                            int oldDaily = numbers.getDaily ( );
                            String numbersString = holder.mAddNumber.getText ().toString ().trim ();
                            final int dailyNumber = Integer.parseInt (numbersString);
                            //final Numbers numbers1 = new Numbers (titleString,dailyNumber);

                            int finalDaily = oldDaily + dailyNumber;

                            mDB.numbersDao ().insertDaily (finalDaily, mNumberList.get (position).getId () );
                            //Toast.makeText (this, getString (R.string.Saved), Toast.LENGTH_SHORT).show ( );

                            AppExecutors.getInstance ().mainThread ().execute (new Runnable ( ) {
                                @Override
                                public void run() {
                                    holder.mAddNumber.setText ("");
                                    notifyDataSetChanged ();

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
                InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(holder.mEnter_btn.getWindowToken(), 0);
                holder.mAddNumber.setFocusable(false);
                holder.mAddNumber.setFocusableInTouchMode(true);
                AppExecutors.getInstance ().diskIO ().execute (new Runnable ( ) {
                    @Override
                    public void run() {
                        //final int favorite = getFav (numbers);
                        if (mItemClickListener != null){
                            if (holder.mAddNumber.getText ( ) == null || holder.mAddNumber.getText ( ).length () == 0)
                            {
                                return;
                            }
                            int oldDaily = numbers.getDaily ( );
                            String numbersString = holder.mAddNumber.getText ().toString ().trim ();
                            final int dailyNumber = Integer.parseInt (numbersString);
                            //final Numbers numbers1 = new Numbers (titleString,dailyNumber);

                            int finalDaily = oldDaily - dailyNumber;
                            if (finalDaily <= 0){
                                AppExecutors.getInstance ().mainThread ().execute (new Runnable ( ) {
                                    @Override
                                    public void run() {
                                        holder.mAddNumber.setError (mContext.getResources ( ).getString (R.string.invalid_value));

                                    }
                                });

                            } else {

                            mDB.numbersDao ().insertDaily (finalDaily, mNumberList.get (position).getId () );
                            //Toast.makeText (this, getString (R.string.Saved), Toast.LENGTH_SHORT).show ( );

                            AppExecutors.getInstance ().mainThread ().execute (new Runnable ( ) {
                                @Override
                                public void run() {
                                    holder.mAddNumber.setText ("");
                                    notifyDataSetChanged ();
                                }
                            });
                            }
                        }
                    }
                });
            }
        });

        if (numbers.getDone () == 1){
            holder.mTitleTextView.setPaintFlags (holder.mTitleTextView.getPaintFlags ()| Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mTitleTextView.setTextColor (mContext.getResources().getColor (R.color.red));
            holder.mNumberTextView.setTextColor (mContext.getResources().getColor (R.color.red));

        }
        else if (numbers.getDone () == 0){
            holder.mTitleTextView.setPaintFlags (holder.mTitleTextView.getPaintFlags ()& (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.mTitleTextView.setTextColor (mContext.getResources().getColor (R.color.colorPrimaryLight));
            holder.mNumberTextView.setTextColor (mContext.getResources().getColor (R.color.colorPrimaryLight));
        }


    }

    @Override
    public int getItemCount() {
        if (mNumberList == null || mNumberList.isEmpty ()) {
            return 0;
        }
        return mNumberList.size ( );
    }

//    @Override
//    public long getItemId(int position) {
//        if (position < mNumberList.size ()){
//            return  mNumberList.get (position).getId ();
//        }
//        return RecyclerView.NO_ID;
//    }



    class DailyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitleTextView;
        TextView mNumberTextView;
        ImageView mEnter_btn;
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
             mEnter_btn = itemView.findViewById (R.id.btn_Enter);
             mMinus_btn = itemView.findViewById (R.id.btn_minus);
             mAddNumber = itemView.findViewById (R.id.edit_daily);
             mClear_btn = itemView.findViewById (R.id.clear);
             mAddNumber.setMaxWidth(7);
             mNumberTextView.setMaxWidth(7);

             mEnter_btn.setOnClickListener (new View.OnClickListener ( ) {
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

        mNumberList = new ArrayList<> ();
        mNumberList.addAll (newList);
        this.searchString=searchString;
        notifyDataSetChanged ( );
    }

    public void setSearchItem(List<Numbers> newList) {
        mNumberList = new ArrayList<> ();
        mNumberList.addAll (newList);
        notifyDataSetChanged ( );
    }

//    @Override
//    public Filter getFilter() {
//        return filter;
//    }
//
//    private Filter filter = new Filter ( ) {
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            List<Numbers> filteredList = new ArrayList<> ();
//
//            if (constraint == null || constraint.length () == 0){
//                filteredList.addAll(mFilteredNumberList);
//            }else {
//                String filterPattern = constraint.toString ().toLowerCase ().trim ();
//
//                for (Numbers number : mFilteredNumberList){
//                    if (number.getTitle ().toLowerCase ().contains (filterPattern)){
//
////                        int startPos = filterPattern.indexOf(constraint.toString ());
////                        int endPos = startPos + constraint.length();
////
////                        Spannable spanString = Spannable.Factory.getInstance().newSpannable(holder.mTitleTextView.getText());
////                        spanString.setSpan(new ForegroundColorSpan (Color.parseColor ("#F9AA33")), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////
////                        holder.mTitleTextView.setText(spanString);
//                        filteredList.add (number);
//                    }
//                }
//            }
//            FilterResults filterResults = new FilterResults ();
//            filterResults.values = filteredList;
//            return filterResults;
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//            mNumberList.clear ();
//            mNumberList.addAll((List <Numbers>) results.values);
//            notifyDataSetChanged ();
//        }
//    };


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
