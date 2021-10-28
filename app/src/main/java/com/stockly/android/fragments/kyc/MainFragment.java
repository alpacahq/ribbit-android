package com.stockly.android.fragments.kyc;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.stockly.android.R;
import com.stockly.android.adapter.PagerAdapter;
import com.stockly.android.databinding.FragmentMainBinding;
import com.stockly.android.fragments.NetworkFragment;
import com.stockly.android.utils.ActivityUtils;
import com.stockly.android.utils.DotsIndicatorDecoration;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * MainFragment
 * This Fragment class is Main Screen of having create account and
 * login options, having pager recycler to show messages and social media icons.
 */
@AndroidEntryPoint
public class MainFragment extends NetworkFragment {

    FragmentMainBinding mBinding;
    PagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //status bar color
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#F9F9F9"));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentMainBinding.bind(view);

        List<String> textList = new ArrayList<>();
        textList.add("Invest commission free in \nU.S. stocks!*\n\n*Relevant SEC and FINRA fees may apply");
        textList.add("This app is operated by Alpaca Securities, LLC, an SEC registered broker-dealer & member FINRA/SIPC");

        mBinding.pagerRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new PagerAdapter(getActivity(), textList);
//        mBinding.pagerRecycler.setHasFixedSize(true);
        mBinding.pagerRecycler.setAdapter(adapter);

        final int radius = getResources().getDimensionPixelSize(R.dimen._3sdp);
        final int dotsHeight = getResources().getDimensionPixelSize(R.dimen._7sdp);
        final int color = ContextCompat.getColor(requireActivity(), R.color.colorDots);
        final int colorInactive = ContextCompat.getColor(requireActivity(), R.color.colorInActiveDots);
        mBinding.pagerRecycler.addItemDecoration(new DotsIndicatorDecoration(radius, radius, dotsHeight, colorInactive, color));
        new PagerSnapHelper().attachToRecyclerView(mBinding.pagerRecycler);

        // bottom sheet to adjust size for smaller screens
        BottomSheetBehavior<RelativeLayout> bottomSheet = BottomSheetBehavior.from(mBinding.bottomSheet);

        mBinding.bottomSheet.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int height = mBinding.parent.getMeasuredHeight();
            int mainHeight = mBinding.main.getMeasuredHeight();
            int peekHeight = height - mainHeight;

            bottomSheet.setPeekHeight(peekHeight);
        });

        mBinding.signUp.setOnClickListener(view1 -> {
            ActivityUtils.launchFragment(requireActivity(), SignUpFragment.class);
//                requireActivity().finish();
        });
        mBinding.login.setOnClickListener(view12 -> {
            ActivityUtils.launchFragment(requireActivity(), LoginFragment.class);
            requireActivity().finish();
        });

    }


}