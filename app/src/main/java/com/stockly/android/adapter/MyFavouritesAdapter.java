package com.stockly.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stockly.android.databinding.FavouritListItemBinding;
import com.stockly.android.listners.ItemClickListener;
import com.stockly.android.models.Positions;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for an Favourite Items on Home Screen
 *
 * <p>Adapters provide a binding from an app-specific data set to views that are displayed
 * within a {@link RecyclerView}.</p>
 */
public class MyFavouritesAdapter extends RecyclerView.Adapter<MyFavouritesAdapter.Holder> {

    private final LayoutInflater mLayoutInflater;
    private final Context context;
    private List<Positions> mFavouriteList = new ArrayList<>();
    private final ItemClickListener<Positions> mListener;

    /**
     * Click Listener for Adapter
     * <p>
     * Click listener for item view to listen to click event of item.
     * So can perform action by passing data object and position to
     * onItemClick interface implemented to listen callback in fragment.
     */
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int index = (int) view.getTag();
            mListener.onItemClick(mFavouriteList.get(index), index);
        }
    };

    /**
     * Constructor of Adapter.
     * This should be called at the time of initializing adapter.
     */
    public MyFavouritesAdapter(Context context, ItemClickListener<Positions> listener) {
        this.mLayoutInflater = LayoutInflater.from(context);
        mListener = listener;
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
        return new Holder(FavouritListItemBinding.inflate(mLayoutInflater, parent, false));
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the item at
     * the given position.
     * Set data by DataBinding to xml by setFavourites.
     */
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Positions favourite = mFavouriteList.get(position);
        holder.mBinding.setFavourites(favourite);
        holder.mBinding.stockIcon.setImageInfo(favourite.getImageInfo());
        holder.itemView.setOnClickListener(onClickListener);
        holder.itemView.setTag(position);
    }

    /**
     * SetData method
     * This Method used set data to adapter after getting from api call.
     * <p>
     * Clear is used to clear previous data from array if required
     * NotifyDataSetChanged. Notify any registered observers that the item at <code>position</code> has changed.
     */
    public void setData(List<Positions> watchList, boolean clear) {

        if (clear) mFavouriteList.clear();

        mFavouriteList.addAll(watchList);
        notifyDataSetChanged();
    }

    /**
     * getItemCount is a function that returns size of list that will be used by the adapter.
     *
     * @return itemCount
     */
    @Override
    public int getItemCount() {
        return mFavouriteList.size();
    }

    /**
     * Holder A class that extends ViewHolder that will be used by the adapter.
     * Holder class used for initializing binding for layout/xml file.
     *
     * @see androidx.recyclerview.widget.RecyclerView.ViewHolder
     */
    public class Holder extends RecyclerView.ViewHolder {
        private final FavouritListItemBinding mBinding;

        public Holder(@NonNull FavouritListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
