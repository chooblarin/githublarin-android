package com.chooblarin.githublarin.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.databinding.FragmentSettingsBinding;
import com.chooblarin.githublarin.debug.DebugScreenActivity;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SettingsFragment extends BaseFragment {

    FragmentSettingsBinding binding;

    @Override
    protected void setupComponent() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setAppVersionName();
        setupDebugScreen();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.unbind();
    }

    private void setAppVersionName() {
        Context context = getActivity().getApplicationContext();
        String versionName;
        try {
            versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            versionName = getString(R.string.unknown_version);
        }
        binding.textAppVersion.setText(versionName);
    }

    private void setupDebugScreen() {
        RxView.clicks(binding.textAppVersion)
                .buffer(250L, TimeUnit.MILLISECONDS)
                .map(List::size)
                .filter(count -> count >= 5)
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .subscribe(_ignored -> {
                    Toast.makeText(getContext(), "Debug screen", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), DebugScreenActivity.class));
                });
    }
}
