package com.example.simplecopy.adapters;


import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplecopy.AppExecutors;
import com.example.simplecopy.R;
import com.example.simplecopy.data.AppDatabase;
import com.example.simplecopy.data.Numbers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CopyAdapter extends RecyclerView.Adapter<CopyAdapter.CopyViewHolder> {

    private static final String TAG = CopyAdapter.class.getSimpleName ( );

    private List<Numbers> mNumberList;
    private Context mContext;
    private ItemClickListener mItemClickListener;
    private String searchString="";
    private AppDatabase mDB;


    public CopyAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        this.mItemClickListener = listener;
    }

    @Override
    public CopyAdapter.CopyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from (mContext)
                .inflate (R.layout.list_item, viewGroup,false);
            return new CopyViewHolder (view);

    }

    @Override
    public void onBindViewHolder(@NonNull final CopyAdapter.CopyViewHolder holder, final int position) {
        final Numbers numbers = mNumberList.get (position);

        String title = numbers.getTitle ( );
        holder.mTitleTextView.setText (title);
        holder.setPosition (position);
        long numberLong = numbers.getNumber ( );
        String numberStr = String.valueOf (numberLong);
        holder.mNumberTextView.setText (numberStr);

        mDB = AppDatabase.getInstance (mContext.getApplicationContext ( ));

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


        // Favorite system

        if (numbers.getFavorite () == 0){
            holder.mFavorite.setImageResource (R.drawable.star_border);
        }
        else if (numbers.getFavorite () == 1){
            holder.mFavorite.setImageResource (R.drawable.ic_star);
        }

        holder.mFavorite.setOnClickListener (new View.OnClickListener ( ) {

            @Override
            public void onClick(View v) {
                AppExecutors.getInstance ().diskIO ().execute (new Runnable ( ) {
                    @Override
                    public void run() {
                        //final int favorite = getFav (numbers);
                if (mItemClickListener != null){

                    if (numbers.getFavorite () == 0){
                        mDB.numbersDao ().insertFavorite (1, mNumberList.get (position).getId ( ));
                        Log.d (TAG, "numbers checked ");

                    } else if (numbers.getFavorite () == 1){
                        mDB.numbersDao ().insertFavorite (0, mNumberList.get (position).getId ( ));
                        Log.d (TAG, "numbers unchecked ");
                    }
                }AppExecutors.getInstance ().mainThread ().execute (new Runnable ( ) {
                            @Override
                            public void run() {
                                notifyDataSetChanged ();

                            }
                        });

                    }

                });
            }

        });

//        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation (mContext, R.anim.layout_fall_down);
//        holder.container.setLayoutAnimation (animationController);

        //holder.container.setAnimation (AnimationUtils.loadAnimation (mContext, R.anim.item_fall_down));

    }


    public void setSearchItem(List<Numbers> newList, String searchString) {
        this.searchString=searchString;
        mNumberList = new ArrayList<> ();
        mNumberList.addAll (newList);
        notifyDataSetChanged ( );
    }


    @Override
    public int getItemCount() {
        if (mNumberList == null || mNumberList.isEmpty ()) {
            return 0;
        }
        return mNumberList.size ( );
    }


    class CopyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        TextView mTitleTextView;
        TextView mNumberTextView;
        ImageView mEdit_btn;
        ImageView mDelete_btn;
        ImageView mFavorite;

        private int mPosition;
        LinearLayout container;

        CopyViewHolder(View itemView) {
            super (itemView);
            container = itemView.findViewById (R.id.container);
            mTitleTextView = itemView.findViewById (R.id.title);
            mNumberTextView = itemView.findViewById (R.id.number);
            mEdit_btn = itemView.findViewById (R.id.btn_edit);
            mDelete_btn = itemView.findViewById (R.id.btn_delete);
            mFavorite = itemView.findViewById (R.id.btn_fav);
            //mFavorite = itemView.findViewById (R.id.star_button);

            mEdit_btn.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    int elementId = mNumberList.get (mPosition).getId ( );
                    mItemClickListener.onNumberEdit (elementId);
                }
            });

            mDelete_btn.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition ( ) != RecyclerView.NO_POSITION && mItemClickListener != null) {
                        mItemClickListener.onNumberDelete (mNumberList.get (mPosition));
                    }
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

    public interface ItemClickListener {
        void onItemClickListener(Numbers numbers);
        void onNumberEdit(int itemId);
        void onNumberDelete(Numbers numbers);
    }

    public List<Numbers> getItems() {
        return mNumberList;
    }


    public void setItems(List<Numbers> itemList) {
        mNumberList = itemList;
        notifyDataSetChanged ( );
    }

}
