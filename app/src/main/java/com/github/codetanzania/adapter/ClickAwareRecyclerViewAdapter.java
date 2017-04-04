package com.github.codetanzania.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class ClickAwareRecyclerViewAdapter<T> extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected final OnItemClickListener<T> mClickListener;

    public ClickAwareRecyclerViewAdapter(OnItemClickListener<T> mClickListener) {
        this.mClickListener = mClickListener;
    }
}
