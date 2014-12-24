package com.android.gesture.builder;

import android.graphics.drawable.Drawable;

public class AppData {

	public CharSequence appName;
	public String appPackageName;
	public Drawable appIcon;

	AppData(CharSequence appName, String packageName, Drawable icon) {
		this.appName = appName;
		this.appPackageName = packageName;
		this.appIcon = icon;
	}

}