package com.stockly.android.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stockly.android.R;

/**
 * NetworkCallFragment
 * To make network call to api server.
 * It is used where Network class not used as parent.
 * So its empty fragment helped to make network call happen in dialog fragment.
 */
public class NetworkCallFragment extends NetworkFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_network_call;
    }
}