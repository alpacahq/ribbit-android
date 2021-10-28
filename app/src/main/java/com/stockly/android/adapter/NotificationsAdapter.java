package com.stockly.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stockly.android.R;
import com.stockly.android.databinding.DailyGiveAwayItemBinding;
import com.stockly.android.databinding.NotificationItemBinding;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.Holder> {

    private final LayoutInflater mLayoutInflater;
    Context context;


    public NotificationsAdapter(Context context) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(NotificationItemBinding.inflate(mLayoutInflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {


    }


    @Override
    public int getItemCount() {
        return 5;
    }


    public class Holder extends RecyclerView.ViewHolder {
        private final NotificationItemBinding mBinding;

        public Holder(@NonNull NotificationItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
