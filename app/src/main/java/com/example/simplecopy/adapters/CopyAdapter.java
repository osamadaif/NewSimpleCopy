package com.example.simplecopy.adapters;


import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplecopy.utils.AppExecutors;
import com.example.simplecopy.R;
import com.example.simplecopy.data.local.database.AppDatabase;
import com.example.simplecopy.data.model.Numbers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CopyAdapter extends RecyclerView.Adapter<CopyAdapter.CopyViewHolder> {

    private static final String TAG = CopyAdapter.class.getSimpleName ( );

    private List<Numbers> mNumberList;
    private Context mContext;
    private ItemClickListener mItemClickListener;
    private String searchString = "";
    private AppDatabase mDB;
    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;
    public static boolean isBtnVisible = true;


    public CopyAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        this.mItemClickListener = listener;
        selected_items = new SparseBooleanArray ( );
    }

    @Override
    public CopyAdapter.CopyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from (viewGroup.getContext ( ))
                .inflate (R.layout.list_item, viewGroup, false);
        return new CopyViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CopyAdapter.CopyViewHolder holder, final int position) {
        final Numbers numbers = mNumberList.get (position);

        String title = numbers.getTitle ( );
        holder.mTitleTextView.setText (title);
        holder.setPosition (position);
        String numberStr = numbers.getNumber ( );
        holder.mNumberTextView.setText (numberStr);

        mDB = AppDatabase.getInstance (mContext.getApplicationContext ( ));

        // Spannable HighLight Work
        String sTitle = title.toLowerCase (Locale.getDefault ( ));
        if (sTitle.contains (searchString)) {
            int startPos = sTitle.indexOf (searchString);
            int endPos = startPos + searchString.length ( );

            Spannable spanString = Spannable.Factory.getInstance ( ).newSpannable (holder.mTitleTextView.getText ( ));
            spanString.setSpan (new ForegroundColorSpan (Color.parseColor ("#F9AA33")), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.mTitleTextView.setText (spanString);
        }

        String sNumber = numberStr.toLowerCase (Locale.getDefault ( ));
        if (sNumber.contains (searchString)) {
            int startPos = sNumber.indexOf (searchString);
            int endPos = startPos + searchString.length ( );

            Spannable spanString = Spannable.Factory.getInstance ( ).newSpannable (holder.mNumberTextView.getText ( ));
            spanString.setSpan (new ForegroundColorSpan (Color.parseColor ("#F9AA33")), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.mNumberTextView.setText (spanString);
        }

        // Favorite system
        if (numbers.getFavorite ( ) == 0) {
            holder.mFavorite.setImageResource (R.drawable.star_border);
        } else if (numbers.getFavorite ( ) == 1) {
            holder.mFavorite.setImageResource (R.drawable.ic_star);
        }
        holder.mFavorite.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                AppExecutors.getInstance ( ).diskIO ( ).execute (new Runnable ( ) {
                    @Override
                    public void run() {
                        //final int favorite = getFav (numbers);
                        if (mItemClickListener != null) {

                            if (numbers.getFavorite ( ) == 0) {
                                mDB.numbersDao ( ).insertFavorite (1, mNumberList.get (position).getId ( ));
                                Log.d (TAG, "numbers checked ");

                            } else if (numbers.getFavorite ( ) == 1) {
                                mDB.numbersDao ( ).insertFavorite (0, mNumberList.get (position).getId ( ));
                                Log.d (TAG, "numbers unchecked ");
                            }
                        }
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
        holder.container.setActivated (selected_items.get (position, false));
        holder.mEdit_btn.setVisibility (isBtnVisible ? View.VISIBLE : View.INVISIBLE);
        holder.mDelete_btn.setVisibility (isBtnVisible ? View.VISIBLE : View.INVISIBLE);
        holder.mFavorite.setVisibility (isBtnVisible ? View.VISIBLE : View.INVISIBLE);
        toggleCheckedIcon (holder, position);
    }


    public void setSearchItem(List<Numbers> newList, String searchString) {
        this.searchString = searchString;
        mNumberList = new ArrayList<> ( );
        mNumberList.addAll (newList);
        notifyDataSetChanged ( );
    }

    public void setSearchItem(List<Numbers> newList) {
        mNumberList = new ArrayList<> ( );
        mNumberList.addAll (newList);
        notifyDataSetChanged ( );
    }


    @Override
    public int getItemCount() {
        if (mNumberList == null || mNumberList.isEmpty ( )) {
            return 0;
        }
        return mNumberList.size ( );
    }

    @Override
    public long getItemId(int position) {
        if (position < mNumberList.size ( )) {
            return mNumberList.get (position).getId ( );
        }
        return RecyclerView.NO_ID;
    }

    public int getId(int position) {
        return mNumberList.get (position).getId ( );
    }

    class CopyViewHolder extends RecyclerView.ViewHolder {
        TextView mTitleTextView;
        TextView mNumberTextView;
        ImageView mEdit_btn;
        ImageView mDelete_btn;
        ImageView mFavorite;
        RelativeLayout lyt_checked;

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
            lyt_checked = itemView.findViewById (R.id.lyt_checked_number);
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

            container.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition ( ) != RecyclerView.NO_POSITION && mItemClickListener != null) {
                        mItemClickListener.onItemClickListener (v, mNumberList.get (mPosition), mPosition, mNumberList.get (mPosition).getId ( ));
                    }
                }
            });

            container.setOnLongClickListener (new View.OnLongClickListener ( ) {
                @Override
                public boolean onLongClick(View v) {
                    if (mItemClickListener == null) return false;
                    mItemClickListener.onItemLongClick (v, mNumberList.get (mPosition), mPosition, mNumberList.get (mPosition).getId ( ));
                    return true;
                }
            });
        }

        public void setPosition(int position) {
            mPosition = position;
        }
    }

    public interface ItemClickListener {
        void onItemClickListener(View view, Numbers numbers, int pos, int itemId);
        void onNumberEdit(int itemId);
        void onNumberDelete(Numbers numbers);
        void onItemLongClick(View view, Numbers numbers, int pos, int itemId);
    }

    public int getSelectedItemCount() {
        return selected_items.size ( );
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<> (selected_items.size ( ));
        for (int i = 0; i < selected_items.size ( ); i++) {
            items.add (selected_items.keyAt (i));
        }
        return items;
    }

    private void toggleCheckedIcon(CopyViewHolder holder, int position) {
        if (selected_items.get (position, false)) {
//            holder.mFavorite.setVisibility (View.GONE);
            holder.lyt_checked.setVisibility (View.VISIBLE);
            holder.container.setBackgroundResource (R.color.colorAccent_transparent);
            if (current_selected_idx == position) resetCurrentIndex ( );
        } else {
            holder.lyt_checked.setVisibility (View.GONE);
//            holder.mFavorite.setVisibility (View.VISIBLE);
            holder.container.setBackgroundColor (selected_items.get(position) ? 0x00000000
                    : Color.TRANSPARENT);
            if (current_selected_idx == position) resetCurrentIndex ( );
        }
    }



    public void toggleSelection(int pos) {
        current_selected_idx = pos;
        if (selected_items.get (pos, false)) {
            selected_items.delete (pos);
        } else {
            selected_items.put (pos, true);
        }
        notifyItemChanged (pos);
    }

    public void clearSelections() {
        selected_items.clear ( );
        notifyDataSetChanged ( );
    }

    public void removeSelected(List<Integer> selectedItemPosition) {
        AppExecutors.getInstance ( ).diskIO ( ).execute (new Runnable ( ) {
            @Override
            public void run() {
                mDB.numbersDao ( ).deleteItemByIds (selectedItemPosition);
            }
        });

        resetCurrentIndex ( );
    }

    private void resetCurrentIndex() {
        current_selected_idx = -1;
    }

    public List<Numbers> getItems() {
        return mNumberList;
    }

    public void setItems(List<Numbers> itemList) {
        mNumberList = itemList;
        notifyDataSetChanged ( );
    }
}
