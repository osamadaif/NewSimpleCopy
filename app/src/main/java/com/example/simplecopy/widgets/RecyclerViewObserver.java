package com.example.simplecopy.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplecopy.extras.Util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RecyclerViewObserver extends RecyclerView {

    private List<View> mEmptyViews = Collections.emptyList ();
    private AdapterDataObserver mObserver = new AdapterDataObserver ( ) {
        @Override
        public void onChanged() {
            toggleViews();

        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            toggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            toggleViews();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            toggleViews();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            toggleViews();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            toggleViews();
        }
    };

    private void toggleViews() {
        if (getAdapter () != null && !mEmptyViews.isEmpty ()){
            if (getAdapter ().getItemCount () == 0 ){

                Util.showViews (mEmptyViews);
                setVisibility (View.GONE);
            } else {

                setVisibility (View.VISIBLE);
                Util.hideViews (mEmptyViews);
            }
        }
    }

    public RecyclerViewObserver(@NonNull Context context) {
        super (context);
    }

    public RecyclerViewObserver(@NonNull Context context, @Nullable AttributeSet attrs) {
        super (context, attrs);
    }

    public RecyclerViewObserver(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter (adapter);
        if (adapter != null){
            adapter.registerAdapterDataObserver (mObserver);
        }
        mObserver.onChanged ();
    }

//    public void hideIfEmpty(View ...views) {
//        mNonEmptyViews = Arrays.asList (views);
//    }

    public void showIfEmpty(View ...emptyViews) {
        mEmptyViews = Arrays.asList (emptyViews);
    }
}
