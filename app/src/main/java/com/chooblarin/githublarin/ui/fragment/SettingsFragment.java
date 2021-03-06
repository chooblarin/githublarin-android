package com.chooblarin.githublarin.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.databinding.FragmentSettingsBinding;
import com.github.pedrovgs.lynx.LynxActivity;
import com.github.pedrovgs.lynx.LynxConfig;
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
        String versionNameString;
        try {
            String versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
            versionNameString = getString(R.string.app_version, versionName);

        } catch (Exception e) {
            versionNameString = getString(R.string.unknown_version);
        }
        binding.textAppVersion.setText(versionNameString);
    }

    private void setupDebugScreen() {
        RxView.clicks(binding.textAppVersion)
                .buffer(1L, TimeUnit.SECONDS)
                .map(List::size)
                .filter(count -> 3 <= count)
                .compose(bindUntilEvent(FragmentEvent.PAUSE))
                .subscribe(_ignored -> {
                    // startActivity(new Intent(getActivity(), DebugScreenActivity.class));
                    openLynxActivity();
                });
    }

    private void openLynxActivity() {
        LynxConfig lynxConfig = new LynxConfig();
        lynxConfig.setMaxNumberOfTracesToShow(4000);

        Intent lynxActivityIntent = LynxActivity.getIntent(getContext(), lynxConfig);
        startActivity(lynxActivityIntent);
    }
}
