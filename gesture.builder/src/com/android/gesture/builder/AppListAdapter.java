package com.android.gesture.builder;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppListAdapter extends BaseAdapter {

	private Activity mContext;
	private LayoutInflater mInflater;
	List<AppData> mAppDataList = null;

	public AppListAdapter(Activity context, List<AppData> appDataList) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mAppDataList = appDataList;
	}

	@Override
	public int getCount() {
		return mAppDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_item, null);

			holder.appName = (TextView) convertView
					.findViewById(R.id.appName);
			holder.appPackageName = (TextView) convertView
					.findViewById(R.id.appPackageName);
			holder.appIcon = (ImageView) convertView
					.findViewById(R.id.app_icon);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		/*
		 * data
		 */

		holder.appName.setText(mAppDataList.get(position).appName + "");
		holder.appPackageName.setText(mAppDataList.get(position).appPackageName
				+ "");
		holder.appIcon.setImageDrawable(mAppDataList.get(position).appIcon);

		return convertView;
	}

	static final class ViewHolder {
		public TextView appName;
		public TextView appPackageName;
		public ImageView appIcon;
	}
}
