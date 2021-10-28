package com.stockly.android.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.stockly.android.R;
import com.stockly.android.adapter.RewardsAdapter;
import com.stockly.android.adapter.RewardsDialogAdapter;
import com.stockly.android.databinding.DialogRewardBinding;
import com.stockly.android.databinding.FragmentRewardsBinding;
import com.stockly.android.listners.ItemClickListener;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * RewardsDialogFragment
 * It represents awards won by user by participating in a daily give away
 * contest. It can be accepted in many ways after scratch card.
 */
@AndroidEntryPoint
public class RewardsDialogFragment extends DialogFragment {
    private DialogRewardBinding mBinding;
    private RewardsDialogAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.dialog_reward, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = DialogRewardBinding.bind(view);


        mAdapter = new RewardsDialogAdapter(requireActivity(), new ItemClickListener<String>() {
            @Override
            public void onItemClick(String s, int position) {

            }
        });
        mBinding.items.setAdapter(mAdapter);

    }
}
