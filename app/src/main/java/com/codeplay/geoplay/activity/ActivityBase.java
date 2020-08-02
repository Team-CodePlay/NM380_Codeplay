package com.codeplay.geoplay.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.Locale;

public abstract class ActivityBase extends AppCompatActivity {

    int q = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        String selLang = PreferenceManager.getDefaultSharedPreferences(this).getString("language", "en");
        changeLocale(this, selLang);

        Resources res = getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(selLang)); // API 17+ only.
        // Use conf.locale = new Locale(...) if targeting lower versions
        res.updateConfiguration(conf, dm);


//        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        Locale locale = new Locale(selLang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());

        super.onCreate(savedInstanceState);
    }

    public static void changeLocale(Context context, String locale) {
        Resources res = context.getResources();
        Configuration conf = res.getConfiguration();
        conf.locale = new Locale(locale);
        res.updateConfiguration(conf, res.getDisplayMetrics());
    }
}