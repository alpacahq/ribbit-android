package com.stockly.android.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.ramiz.nameinitialscircleimageview.NameInitialsCircleImageView;
import com.stockly.android.BuildConfig;
import com.stockly.android.R;
import com.stockly.android.databinding.MyStocksItemBinding;
import com.stockly.android.databinding.WatchListItemBinding;
import com.stockly.android.listners.ItemClickListener;
import com.stockly.android.models.Assets;
import com.stockly.android.models.Positions;
import com.stockly.android.models.User;
import com.stockly.android.models.WatchList;
import com.stockly.android.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import coil.ComponentRegistry;
import coil.ImageLoader;
import coil.decode.SvgDecoder;
import coil.request.ImageRequest;

/**
 * Adapter for an watchListed items on Profile Screen
 *
 * <p>Adapters provide a binding from an app-specific data set to views that are displayed
 * within a {@link RecyclerView}.</p>
 */
public class MyWatchListAdapter extends RecyclerView.Adapter<MyWatchListAdapter.Holder> {

    private final LayoutInflater mLayoutInflater;
    private final Context context;
    private List<Positions> mWatchList = new ArrayList<>();
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
            mListener.onItemClick(mWatchList.get(index), index);
        }
    };

    /**
     * Constructor of Adapter.
     * This should be called at the time of initializing adapter.
     */
    public MyWatchListAdapter(Context context, ItemClickListener<Positions> listener) {
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
        return new Holder(WatchListItemBinding.inflate(mLayoutInflater, parent, false));
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the item at
     * the given position.
     * Set data by DataBinding to xml by setWatchList.
     */
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Positions watchList = mWatchList.get(position);
        holder.mBinding.setWatchList(watchList);
        holder.mBinding.winIcon.setImageInfo(watchList.getImageInfo());
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
        if (clear) mWatchList.clear();

        mWatchList.addAll(watchList);
        notifyDataSetChanged();
    }

    /**
     * getItemCount is a function that returns size of list that will be used by the adapter.
     *
     * @return itemCount
     */
    @Override
    public int getItemCount() {
        return mWatchList.size();
    }


    /**
     * Holder A class that extends ViewHolder that will be used by the adapter.
     * Holder class used for initializing binding for layout/xml file.
     *
     * @see androidx.recyclerview.widget.RecyclerView.ViewHolder
     */
    public class Holder extends RecyclerView.ViewHolder {
        private final WatchListItemBinding mBinding;

        public Holder(@NonNull WatchListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
