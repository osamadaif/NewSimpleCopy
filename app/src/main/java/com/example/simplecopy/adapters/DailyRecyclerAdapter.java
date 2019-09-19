package com.example.simplecopy.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplecopy.AppExecutors;
import com.example.simplecopy.R;
import com.example.simplecopy.data.AppDatabase;
import com.example.simplecopy.data.Numbers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DailyRecyclerAdapter extends RecyclerView.Adapter<DailyRecyclerAdapter.DailyViewHolder> {
    private static final String TAG = DailyRecyclerAdapter.class.getSimpleName ( );

    private List<Numbers> mNumberList;
    private Context mContext;
    private ItemClickListener mItemClickListener;
    private String searchString="";
    private AppDatabase mDB;

    public DailyRecyclerAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        this.mItemClickListener = listener;
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
                AppExecutors.getInstance ().diskIO ().execute (new Runnable ( ) {
                    @Override
                    public void run() {

                        if (mItemClickListener != null ){
                            if (holder.mNumberTextView.getText ( ) == null || holder.mNumberTextView.getText ( ).length () == 0)
                            {
                                return;
                            }
                            final int dailyNumber = 0;
                            mDB.numbersDao ().insertDaily (dailyNumber, mNumberList.get (position).getId () );
                            AppExecutors.getInstance ().mainThread ().execute (new Runnable ( ) {
                                @Override
                                public void run() {
                                    notifyDataSetChanged ();

                                }
                            });
                        }




                    }

                });
            }
        });






        // Spannable HighLight Work
        String sTitle = title.toLowerCase (Locale.getDefault ());
        if (sTitle.contains (searchString)){
            int startPos = sTitle.indexOf(searchString);
            int endPos = startPos + searchString.length();

            Spannable spanString = Spannable.Factory.getInstance().newSpannable(holder.mTitleTextView.getText());
            spanString.setSpan(new ForegroundColorSpan (Color.parseColor ("#F9AA33")), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.mTitleTextView.setText(spanString);
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

    class DailyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitleTextView;
        TextView mNumberTextView;
        Button mEnter_btn;
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

         }

         @Override
        public void onClick(View v) {

        }

        public void setPosition(int position) {
            mPosition = position;
        }
    }

    public void setSearchItem(List<Numbers> newList, String searchString) {
        this.searchString=searchString;
        mNumberList = new ArrayList<> ();
        mNumberList.addAll (newList);
        notifyDataSetChanged ( );
    }

    public void setSearchItem(List<Numbers> newList) {
        mNumberList = new ArrayList<> ();
        mNumberList.addAll (newList);
        notifyDataSetChanged ( );
    }


    public interface ItemClickListener {
        void onNumberSubmit(int itemId);
        void onNumberClear(int itemId);

    }

    public List<Numbers> getItems() {
        return mNumberList;
    }

    public void setItems(List<Numbers> itemList) {
        mNumberList = itemList;
        notifyDataSetChanged ( );
    }
}