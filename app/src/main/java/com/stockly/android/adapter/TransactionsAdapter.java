package com.stockly.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.stockly.android.R;
import com.stockly.android.databinding.TransactionsItemBinding;
import com.stockly.android.listners.ItemClickListener;
import com.stockly.android.models.BankAccount;
import com.stockly.android.models.Payment;
import com.stockly.android.utils.CommonUtils;
import com.stockly.android.utils.DateUtilz;

import java.util.ArrayList;
import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.Holder> {

    private final LayoutInflater mLayoutInflater;
    private final Context context;
    private List<Payment> mData = new ArrayList<>();
    private final ItemClickListener<Payment> mListener;

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
            mListener.onItemClick(mData.get(index), index);
        }
    };

    /**
     * Constructor of Adapter.
     * This should be called at the time of initializing adapter.
     */
    public TransactionsAdapter(Context context, ItemClickListener<Payment> listener) {
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
        return new Holder(TransactionsItemBinding.inflate(mLayoutInflater, parent, false));
    }


    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the item at
     * the given position.
     * Set data by DataBinding to xml by setPayment.
     */
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Payment payment = mData.get(position);
        holder.mBinding.setPayment(payment);
        holder.itemView.setOnClickListener(onClickListener);
        holder.itemView.setTag(position);
    }


    /**
     * getItemCount is a function that returns size of list that will be used by the adapter.
     *
     * @return itemCount
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * SetData method
     * This Method used set data to adapter after getting from api call.
     * <p>
     * Clear is used to clear previous data from array if required
     * NotifyDataSetChanged. Notify any registered observers that the item at <code>position</code> has changed.
     */
    public void setData(List<Payment> data, boolean isClear) {
        if (isClear) {
            mData.clear();
        }

        mData.addAll(data);
        notifyDataSetChanged();
    }


    /**
     * Holder A class that extends ViewHolder that will be used by the adapter.
     * Holder class used for initializing binding for layout/xml file.
     *
     * @see androidx.recyclerview.widget.RecyclerView.ViewHolder
     */
    public class Holder extends RecyclerView.ViewHolder {
        private final TransactionsItemBinding mBinding;

        public Holder(@NonNull TransactionsItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
