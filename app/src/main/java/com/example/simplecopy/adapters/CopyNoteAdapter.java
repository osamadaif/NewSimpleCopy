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
import com.example.simplecopy.data.NotesData;
import com.example.simplecopy.data.Numbers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CopyNoteAdapter extends RecyclerView.Adapter<CopyNoteAdapter.CopyViewHolder> {

    private static final String TAG = CopyNoteAdapter.class.getSimpleName ( );

    private List<NotesData> mNumberList;
    private Context mContext;
    private ItemClickListener mItemClickListener;
    private String searchString="";
    private AppDatabase mDB;


    public CopyNoteAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        this.mItemClickListener = listener;
    }

    @Override
    public CopyNoteAdapter.CopyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from (mContext)
                .inflate (R.layout.list_note, viewGroup,false);
            return new CopyViewHolder (view);

    }

    @Override
    public void onBindViewHolder(@NonNull final CopyNoteAdapter.CopyViewHolder holder, final int position) {
        final NotesData notesData = mNumberList.get (position);

        holder.setPosition (position);
        String title = notesData.getTitle ( );
        holder.mTitleTextView.setText (title);

        String summary = notesData.getNote ();
        if (notesData.getNote ().length () >= 26){
            String supSummary = summary.substring (0,25);
            holder.mSummary.setText (supSummary + "...");
        }else if (notesData.getNote ().length () == 25){
            String supSummary = summary.substring (0,25);
            holder.mSummary.setText (supSummary);
        }
        else {
            int b = notesData.getNote ().length ();
            String supSummary = summary.substring (0,b);
            holder.mSummary.setText (supSummary);
        }



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


        // Favorite system

        if (notesData.getFavorite () == 0){
            holder.mFavorite.setImageResource (R.drawable.star_border);
        }
        else if (notesData.getFavorite () == 1){
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

                    if (notesData.getFavorite () == 0){
                        mDB.NotesDao ().insertFavorite (1, mNumberList.get (position).getId ( ));
                        Log.d (TAG, "numbers checked ");

                    } else if (notesData.getFavorite () == 1){
                        mDB.NotesDao ().insertFavorite (0, mNumberList.get (position).getId ( ));
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


    public void setSearchItem(List<NotesData> newList, String searchString) {
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
        TextView mSummary;
        ImageView mEdit_btn;
        ImageView mDelete_btn;
        ImageView mFavorite;

        private int mPosition;
        LinearLayout container;

        CopyViewHolder(View itemView) {
            super (itemView);
            container = itemView.findViewById (R.id.container_note);
            mTitleTextView = itemView.findViewById (R.id.title_notes);
            mSummary = itemView.findViewById (R.id.summary);
            mEdit_btn = itemView.findViewById (R.id.btn_edit_note);
            mDelete_btn = itemView.findViewById (R.id.btn_delete_note);
            mFavorite = itemView.findViewById (R.id.btn_fav_note);
            //mFavorite = itemView.findViewById (R.id.star_button);

            mEdit_btn.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    int elementId = mNumberList.get (mPosition).getId ( );
                    mItemClickListener.onNoteEdit (elementId);
                }
            });

            mDelete_btn.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition ( ) != RecyclerView.NO_POSITION && mItemClickListener != null) {
                        mItemClickListener.onNoteDelete (mNumberList.get (mPosition));
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
        void onItemClickListener(NotesData notesData);
        void onNoteEdit(int itemId);
        void onNoteDelete(NotesData notesData);
    }

    public List<NotesData> getItems() {
        return mNumberList;
    }


    public void setItems(List<NotesData> itemList) {
        mNumberList = itemList;
        notifyDataSetChanged ( );
    }

}
