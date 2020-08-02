package com.codeplay.jatayu;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class AppClass extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		FirebaseAnalytics.getInstance(this);
		FirebaseCrashlytics.getInstance();
	}
}
