package com.stockly.android.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.stockly.android.MainActivity;
import com.stockly.android.R;
import com.stockly.android.adapter.DailyGiveAwayAdapter;
import com.stockly.android.adapter.RewardsAdapter;
import com.stockly.android.databinding.FragmentDailyGiveAwayBinding;
import com.stockly.android.databinding.FragmentRewardsBinding;
import com.stockly.android.listners.ItemClickListener;
import com.stockly.android.utils.ActivityUtils;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * RewardsFragment
 * It represent number of rewards won by user's previously
 * and can spend it many ways.
 */
@AndroidEntryPoint
public class RewardsFragment extends BaseFragment {
    private FragmentRewardsBinding mBinding;
    private RewardsAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleBackPressActivity(this, MainActivity.class);
        //status bar color
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#F0FF927A"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rewards, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentRewardsBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar, true);


        mAdapter = new RewardsAdapter(requireActivity(), new ItemClickListener<String>() {
            @Override
            public void onItemClick(String s, int position) {
//                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireActivity(),R.style.ThemeOverlay_App_MaterialAlertDialog);
//                 dialogBuilder.setView(view);
//                  dialogBuilder.show();
                FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
                Fragment prev = requireActivity().getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                DialogFragment dialogFragment = new RewardsDialogFragment();
                dialogFragment.show(ft, "dialog");
            }
        });
        mBinding.items.setAdapter(mAdapter);

    }
}
