package com.stockly.android.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stockly.android.databinding.RewardsItemBinding;
import com.stockly.android.databinding.SearchItemBinding;
import com.stockly.android.listners.ChangeListener;
import com.stockly.android.listners.ItemClickListener;
import com.stockly.android.models.Assets;
import com.stockly.android.models.Positions;
import com.stockly.android.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for an Searching items on Search Ticker Screen
 *
 * <p>Adapters provide a binding from an app-specific data set to views that are displayed
 * within a {@link RecyclerView}.</p>
 */
public class SearchTickersAdapter extends RecyclerView.Adapter<SearchTickersAdapter.Holder> {

    private final LayoutInflater mLayoutInflater;
    private final Context context;
    private List<Positions> mAssetsList = new ArrayList<>();
    private final ItemClickListener<Positions> mListener;
    private final ChangeListener<Positions> mCheckedChangeListener;
    private User mUser;

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
            mListener.onItemClick(mAssetsList.get(index), index);

        }
    };

    /**
     * Constructor of Adapter.
     * This should be called at the time of initializing adapter.
     * Checked change listener is implemented to check user action and perform
     * check/uncheck of checkbox
     */
    public SearchTickersAdapter(Context context, ItemClickListener<Positions> listener, ChangeListener<Positions> checkedListener) {
        this.mLayoutInflater = LayoutInflater.from(context);
        mListener = listener;
        mCheckedChangeListener = checkedListener;
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
        return new Holder(SearchItemBinding.inflate(mLayoutInflater, parent, false));
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the item at
     * the given position.
     * Set data by DataBinding to xml by setAsset.
     * Favourite click listener
     * If user's kyc not approved then favourite icon will rollback to default state and
     * popup message will be shown to user.
     */
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        Positions assets = mAssetsList.get(position);
        holder.mBinding.setAsset(assets);
        holder.itemView.setOnClickListener(onClickListener);
        holder.itemView.setTag(position);
        holder.mBinding.favourite.setOnClickListener(v -> {
            if (!mUser.account_status.equalsIgnoreCase("APPROVED")) {
                Toast.makeText(context, "KYC not approved. Please wait until approved.", Toast.LENGTH_SHORT).show();
                holder.mBinding.favourite.setChecked(false);
            } else {
                mCheckedChangeListener.checkedChangeListener(mAssetsList.get(position), position, holder.mBinding.favourite.isChecked());
            }
        });
        holder.mBinding.favourite.setTag(position);
    }

    /**
     * getItemCount is a function that returns size of list that will be used by the adapter.
     *
     * @return itemCount
     */
    @Override
    public int getItemCount() {
        return mAssetsList.size();
    }

    /**
     * SetData method
     * This Method used set data to adapter after getting from api call.
     * <p>
     * Clear is used to clear previous data from array if required
     * NotifyDataSetChanged. Notify any registered observers that the item at <code>position</code> has changed.
     */
    public void setData(List<Positions> list, boolean isClear, User user) {
        if (isClear) mAssetsList.clear();

        mUser = user;
        mAssetsList.addAll(list);
        notifyDataSetChanged();
    }


    /**
     * Holder A class that extends ViewHolder that will be used by the adapter.
     * Holder class used for initializing binding for layout/xml file.
     *
     * @see androidx.recyclerview.widget.RecyclerView.ViewHolder
     */
    public class Holder extends RecyclerView.ViewHolder {
        private final SearchItemBinding mBinding;

        public Holder(@NonNull SearchItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
