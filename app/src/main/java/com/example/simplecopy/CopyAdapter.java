package com.example.simplecopy;


import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplecopy.data.AppDatabase;
import com.example.simplecopy.data.Numbers;
import com.example.simplecopy.data.NumbersDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CopyAdapter extends RecyclerView.Adapter<CopyAdapter.CopyViewHolder> {

    private static final String TAG = CopyAdapter.class.getSimpleName ( );

    private List<Numbers> mNumberList;
    private Context mContext;
    private ItemClickListener mItemClickListener;
    private String searchString="";
    private MainViewModel mainViewModel;
    private boolean mClickedFav = false;
    private int lastSelectedPosition = -1;
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


        mainViewModel = ViewModelProviders.of ((FragmentActivity) mContext).get (MainViewModel.class);





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

//        if (mainViewModel.isFavorite (mNumberList.get (position).getId ( )) == 1)
//            holder.mFavorite.setImageResource (R.drawable.ic_star);
//        else
//            holder.mFavorite.setImageResource (R.drawable.star_border);

//        AppExecutors.getInstance ().diskIO ().execute (new Runnable ( ) {
//            @Override
//            public void run() {
//
//
//            }
//        });

        holder.mFavorite.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                AppExecutors.getInstance ().diskIO ().execute (new Runnable ( ) {
                    @Override
                    public void run() {
                if (mItemClickListener != null){
                    if (numbers.getFavorite () == 0){
                        //Toast.makeText (mContext, "checked", Toast.LENGTH_SHORT).show ( );
                        
                        holder.mFavorite.setChecked (true);
                        //mainViewModel.insertFavorite (1, mNumberList.get (position).getId ( ));

                        mDB.numbersDao ().insertFavorite (1, mNumberList.get (position).getId ( ));

                    } else if (numbers.getFavorite () == 1){
                        //Toast.makeText (mContext, "unChecked", Toast.LENGTH_SHORT).show ( );

                        holder.mFavorite.setChecked (false);
                        //mainViewModel.insertFavorite (0, mNumberList.get (position).getId ( ));
                        mDB.numbersDao ().insertFavorite (0, mNumberList.get (position).getId ( ));
                    }
                }
                    }
                });


            }
        });





       // final int favorite = numbers.getFavorite ();

//        holder.mFavorite.setOnLikeListener (new OnLikeListener ( ) {
//            @Override
//            public void liked(LikeButton likeButton) {
//                if (mainViewModel.isFavorite (position) != 1){
//                    addToFavorite(mainViewModel.isFavorite (position));
//                }
//
//            }
//
//            @Override
//            public void unLiked(LikeButton likeButton) {
//
//                if ( holder.mFavorite.isLiked ()){
//                    holder.mFavorite.setLiked(false);
//                }
//            }
//        });

        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation (mContext, R.anim.layout_fall_down);

        holder.container.setLayoutAnimation (animationController);

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
        CheckBox mFavorite;

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

//            mFavorite.setOnClickListener (new View.OnClickListener ( ) {
//                @Override
//                public void onClick(View v) {
//                    if (getAdapterPosition ( ) != RecyclerView.NO_POSITION && mItemClickListener != null) {
//                        int elementId = mNumberList.get (mPosition).getId ( );
//                        mItemClickListener.onNumberFav (elementId);
//                    }
//
//                }
//            });
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

        //void onNumberFav(int id);

    }

    public List<Numbers> getItems() {
        return mNumberList;
    }


    public void setItems(List<Numbers> itemList) {
        mNumberList = itemList;
        notifyDataSetChanged ( );
    }

    public void setSearchItem(List<Numbers> newList) {
        mNumberList = newList;
        notifyDataSetChanged ( );
    }


}
