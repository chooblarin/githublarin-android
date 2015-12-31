package com.chooblarin.githublarin.ui.fragment;

import android.content.Context;

import com.trello.rxlifecycle.components.support.RxFragment;

public abstract class BaseFragment extends RxFragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setupComponent();
    }

    protected abstract void setupComponent();
}
