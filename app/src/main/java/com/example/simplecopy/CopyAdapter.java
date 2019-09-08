package com.example.simplecopy;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplecopy.data.Numbers;

import java.util.ArrayList;
import java.util.List;

public class CopyAdapter extends RecyclerView.Adapter<CopyAdapter.CopyViewHolder> {

    private static final String TAG = CopyAdapter.class.getSimpleName ( );

    private List<Numbers> mNumberList;
    private List<Numbers> mNumberListFull = new ArrayList<> ();
    private Context mContext;
    private ItemClickListener mItemClickListener;


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
    public void onBindViewHolder(@NonNull CopyAdapter.CopyViewHolder holder, int position) {
        final Numbers numbers = mNumberList.get (position);

            String title = numbers.getTitle ( );
            holder.mTitleTextView.setText (title);
            holder.setPosition (position);

            long numberLong = numbers.getNumber ( );
            String numberStr = String.valueOf (numberLong);

            holder.mNumberTextView.setText (numberStr);
            //holder.container.setAnimation (AnimationUtils.loadAnimation (mContext, R.anim.fade_scale_animation));

    }


    @Override
    public int getItemCount() {
        if (mNumberList == null || mNumberList.isEmpty ()) {
            return 0;
        }
        return mNumberList.size ( );
    }



    class CopyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitleTextView;
        TextView mNumberTextView;
        ImageView mEdit_btn;
        ImageView mDelete_btn;

        private int mPosition;
        LinearLayout container;


        CopyViewHolder(View itemView) {
            super (itemView);
            container = itemView.findViewById (R.id.container);
            mTitleTextView = itemView.findViewById (R.id.title);
            mNumberTextView = itemView.findViewById (R.id.number);
            mEdit_btn = itemView.findViewById (R.id.btn_edit);
            mDelete_btn = itemView.findViewById (R.id.btn_delete);


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

    public void setSearchItem(List<Numbers> newList) {
        mNumberList = newList;
        notifyDataSetChanged ( );
    }


}
