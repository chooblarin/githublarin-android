package com.chooblarin.githublarin.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.databinding.FragmentPublicActivityBinding;

public class PublicActivityFragment extends BaseFragment {

    public static PublicActivityFragment newInstance() {
        return new PublicActivityFragment();
    }

    FragmentPublicActivityBinding binding;

    @Override
    protected void setupComponent() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_public_activity, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.unbind();
    }
}
