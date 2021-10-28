package com.stockly.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stockly.android.databinding.PagerItemBinding;


import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for an SnapHelper dot items on Main Screen
 *
 * <p>Adapters provide a binding from an app-specific data set to views that are displayed
 * within a {@link RecyclerView}.</p>
 */
public class PagerAdapter extends RecyclerView.Adapter<PagerAdapter.Holder> {
    private final LayoutInflater mLayoutInflater;
    private List<String> mList;

    /**
     * Constructor of Adapter.
     * This should be called at the time of initializing adapter.
     */
    public PagerAdapter(Context context, List<String> list) {
        mLayoutInflater = LayoutInflater.from(context);
        mList = list;
    }

    /**
     * Binds the given View to the position. The View can be a View previously retrieved via
     * Binds or created by mLayoutInflater using layout data binding behavior.
     * {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}.
     */
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(PagerItemBinding.inflate(mLayoutInflater, parent, false));
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the item at
     * the given position.
     */
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        if (position == 0) {
            SpannableString s = new SpannableString(mList.get(position));
            s.setSpan(new StyleSpan(Typeface.BOLD), 7, 23, 0);
            s.setSpan(new ForegroundColorSpan(Color.BLACK), 7, 23, 0);
            s.setSpan(new AbsoluteSizeSpan(35), 41, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.mBinding.title.setText(s);
        } else {
            holder.mBinding.title.setText(mList.get(position));
        }
    }

    /**
     * getItemCount is a function that returns size of list that will be used by the adapter.
     *
     * @return itemCount
     */
    @Override
    public int getItemCount() {
        return mList.size();
    }


    /**
     * Holder A class that extends ViewHolder that will be used by the adapter.
     * Holder class used for initializing binding for layout/xml file.
     *
     * @see androidx.recyclerview.widget.RecyclerView.ViewHolder
     */
    static class Holder extends RecyclerView.ViewHolder {
        private final PagerItemBinding mBinding;

        Holder(@NonNull PagerItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
