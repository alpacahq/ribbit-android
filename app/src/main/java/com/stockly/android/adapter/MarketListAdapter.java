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

import com.github.ramiz.nameinitialscircleimageview.NameInitialsCircleImageView;
import com.stockly.android.R;
import com.stockly.android.databinding.MarketListItemBinding;
import com.stockly.android.databinding.WatchListItemBinding;
import com.stockly.android.listners.ItemClickListener;
import com.stockly.android.models.Positions;
import com.stockly.android.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import coil.ComponentRegistry;
import coil.ImageLoader;
import coil.decode.SvgDecoder;
import coil.request.ImageRequest;

public class MarketListAdapter extends RecyclerView.Adapter<MarketListAdapter.Holder> {

    private final LayoutInflater mLayoutInflater;
    private final List<Positions> mWatchList = new ArrayList<>();
    private final ItemClickListener<Positions> mListener;

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int index = (int) view.getTag();
            mListener.onItemClick(mWatchList.get(index), index);
        }
    };

    public MarketListAdapter(Context context, ItemClickListener<Positions> listener) {
        this.mLayoutInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(MarketListItemBinding.inflate(mLayoutInflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
//        Positions watchList = mWatchList.get(position);
//        holder.mBinding.title.setText(watchList.symbol);
////        holder.mBinding.desc.setText(watchList.name);
//        if (watchList.name.contains("Stock") || watchList.name.contains("Class")) {
//            holder.mBinding.desc.setText(watchList.name.split("\\.", 2)[0] + ".");
//        } else {
//            holder.mBinding.desc.setText(watchList.name);
//        }
//        if (watchList.ticker != null) {
//            holder.mBinding.winPrice.setText("$" + CommonUtils.round(Double.parseDouble(String.valueOf(watchList.ticker.latestTrade.p)), 2));
//        }
////        loadSvg(context, BuildConfig.BASE_URL + "file/" + watchList.symbol + ".svg", holder.mBinding.winIcon);
////        updateImage(holder.mBinding.winIcon, watchList);
//        holder.itemView.setOnClickListener(onClickListener);
//        holder.itemView.setTag(position);
    }

    public void setData(List<Positions> watchList, boolean clear) {

        if (clear) mWatchList.clear();

        mWatchList.addAll(watchList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public void updateImage(NameInitialsCircleImageView imageView, Positions positions) {
        String[] str = positions.name.split("\\s+");
        String str1 = str[0];
        String str2 = str[1];
        String finalStr;
        if (!TextUtils.isEmpty(str2)) {
            finalStr = "" + str1.charAt(0) + str2.charAt(0);
        } else {
            finalStr = "" + str1.charAt(0);
        }
        NameInitialsCircleImageView.ImageInfo imageInfo = new NameInitialsCircleImageView.ImageInfo
                .Builder(finalStr)
                .setTextColor(R.color.colorWhite)
                .setTextFont(R.font.inter_semibold)
//                .setImageUrl(imageUrl)
//                .setCircleBackgroundColorRes(randomAndroidColor)
                .build();
        imageView.setImageInfo(imageInfo);

    }

    public class Holder extends RecyclerView.ViewHolder {
        private final MarketListItemBinding mBinding;

        public Holder(@NonNull MarketListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
