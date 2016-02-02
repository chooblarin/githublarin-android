package com.chooblarin.githublarin.debug;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DebugScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        throw new IllegalStateException("This is stub class if production build");
    }
}
