package com.chooblarin.githublarin.ui.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.databinding.FragmentSettingsBinding;

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
}
