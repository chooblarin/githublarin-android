package com.chooblarin.githublarin;

import android.content.Context;
import android.os.StrictMode;

import com.chooblarin.githublarin.di.AppComponent;
import com.chooblarin.githublarin.di.AppModule;
import com.chooblarin.githublarin.di.DaggerAppComponent;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.leakcanary.LeakCanary;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class Application extends android.app.Application {

    public static Application get(Context context) {
        return (Application) context.getApplicationContext();
    }

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initAppComponent();
        Fresco.initialize(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/NotoSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }

        LeakCanary.install(this);
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    private void initAppComponent() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }
}
