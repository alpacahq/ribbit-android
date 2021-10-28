package com.stockly.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.stockly.android.R;
import com.stockly.android.databinding.DailyGiveAwayItemBinding;

/**
 * Adapter for an Daily Give Away
 *
 * <p>Adapters provide a binding from an app-specific data set to views that are displayed
 * within a {@link RecyclerView}.</p>
 */

public class DailyGiveAwayAdapter extends RecyclerView.Adapter<DailyGiveAwayAdapter.Holder> {

    private final LayoutInflater mLayoutInflater;
    Context context;

    /**
     * Constructor of Adapter.
     * This should be called at the time of initializing adapter.
     */
    public DailyGiveAwayAdapter(Context context) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    /**
     * Binds the given View to the position. The View can be a View previously retrieved via
     * Binds or created by mLayoutInflater using layout data binding behavior.
     * {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}.
     */
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(DailyGiveAwayItemBinding.inflate(mLayoutInflater, parent, false));
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the item at
     * the given position.
     */
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.mBinding.numberEarned.setTextColor(Color.parseColor("#92ACB5"));
        holder.mBinding.numberEarned.setText(R.string._zero);
        holder.mBinding.numberEarned.setBackground(AppCompatResources.getDrawable(context, R.drawable.strike_textview));
        holder.mBinding.title.setTextColor(Color.parseColor("#92ACB5"));
        holder.mBinding.title.setText(R.string.invite_a_friend);
        holder.mBinding.title.setBackground(AppCompatResources.getDrawable(context, R.drawable.strike_textview));
        holder.mBinding.desc.setTextColor(Color.parseColor("#92ACB5"));
        holder.mBinding.desc.setText(R.string.free_stock);
        holder.mBinding.desc.setBackground(AppCompatResources.getDrawable(context, R.drawable.strike_textview));
        holder.mBinding.icon.setImageResource(R.drawable.ic_check_circle);


    }

    /**
     * getItemCount is a function that returns size of list that will be used by the adapter.
     *
     * @return itemCount
     */

    @Override
    public int getItemCount() {
        return 10;
    }

    /**
     * Holder A class that extends ViewHolder that will be used by the adapter.
     * Holder class used for initializing binding for layout/xml file.
     *
     * @see androidx.recyclerview.widget.RecyclerView.ViewHolder
     */
    public class Holder extends RecyclerView.ViewHolder {
        private final DailyGiveAwayItemBinding mBinding;

        public Holder(@NonNull DailyGiveAwayItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
