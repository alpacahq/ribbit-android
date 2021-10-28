package com.stockly.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stockly.android.databinding.RewardDialogItemBinding;
import com.stockly.android.databinding.RewardsItemBinding;
import com.stockly.android.listners.ItemClickListener;

public class RewardsDialogAdapter extends RecyclerView.Adapter<RewardsDialogAdapter.Holder> {

    private final LayoutInflater mLayoutInflater;
    private final Context context;

    private final ItemClickListener<String> mListener;

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int index = (int) view.getTag();
            mListener.onItemClick(null, index);
        }
    };

    public RewardsDialogAdapter(Context context, ItemClickListener<String> listener) {
        this.mLayoutInflater = LayoutInflater.from(context);
        mListener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(RewardDialogItemBinding.inflate(mLayoutInflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        holder.itemView.setOnClickListener(onClickListener);
        holder.itemView.setTag(position);
    }


    @Override
    public int getItemCount() {
        return 6;
    }


    public class Holder extends RecyclerView.ViewHolder {
        private final RewardDialogItemBinding mBinding;

        public Holder(@NonNull RewardDialogItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
