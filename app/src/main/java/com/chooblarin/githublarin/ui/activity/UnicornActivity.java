package com.chooblarin.githublarin.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.databinding.ActivityUnicornBinding;

public class UnicornActivity extends BaseActivity {

    public static Intent createIntent(Context context) {
        return new Intent(context, UnicornActivity.class);
    }

    private ActivityUnicornBinding binding;

    @Override
    protected void setupComponent() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_unicorn);
        setupToolbar(binding.toolbarUnicorn);
    }

    private void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);
    }
}
