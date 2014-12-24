package com.android.gesture.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;



public class AppList extends Activity{
	private ListView AppListView;
	private List<AppData> AppList = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_app);
		
		AppList = initAppList();
		
		AppListView = (ListView) this.findViewById(R.id.app_List);
		AppListView.setAdapter(new AppListAdapter(this, AppList));
		AppListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long id) {
				
				String res = AppList.get(pos).appPackageName;
				CharSequence ans = AppList.get(pos).appName;
				System.out.println(res + " " + ans );
				Intent intent = new Intent(AppList.this, CreateGestureActivity.class);
				intent.putExtra("name", ans.toString());
				startActivity(intent);
				

			}
		});
	}
	
	// 获取应用列表信息
		public List<AppData> initAppList() {

			List<AppData> appDataList = new ArrayList<AppData>();
			// 获取PackageManager的对象
			PackageManager pm = getPackageManager();
			// 得到系统安装的所有程序包的PackageInfo对象
			List<PackageInfo> packs = pm.getInstalledPackages(0);
			for (PackageInfo pi : packs) {
				AppData appData = new AppData(pi.applicationInfo.loadLabel(pm),
						pi.applicationInfo.packageName,
						pi.applicationInfo.loadIcon(pm));
				
				appDataList.add(appData);

			}
			return appDataList;
		}
}
