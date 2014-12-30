package com.android.gesture.builder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

@SuppressLint("NewApi")
public class Contacts extends Activity {
	
	Context mContext = null;

    /**获取库Phon表字段**/
    private static final String[] PHONES_PROJECTION = new String[] {
	    Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
   
    /**联系人显示名称**/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    
    /**电话号码**/
    private static final int PHONES_NUMBER_INDEX = 1;
    
    /**头像ID**/
    private static final int PHONES_PHOTO_ID_INDEX = 2;
   
    /**联系人的ID**/
    private static final int PHONES_CONTACT_ID_INDEX = 3;

	private ListView mListview = null;
	List<HashMap<String,Object>> mListData = new ArrayList<HashMap<String,Object>>();
	SimpleAdapter adapter = null;
	String type = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts);
		Intent in = getIntent();
		type = in.getStringExtra("type");
		mListview = (ListView)findViewById(R.id.contactList);
		mContext = this;
		System.out.println("123");
		getPhoneContacts();
		System.out.println("1234");
		adapter = new SimpleAdapter(this, mListData, R.layout.contacts_item,  
                new String[]{"image", "name", "number"}, new int[]{R.id.mimage, R.id.mname, R.id.mnumber});
		adapter.setViewBinder(new ViewBinder() {      
            public boolean setViewValue(View view, Object data,  
                    String textRepresentation) {  
                //判断是否为我们要处理的对象  
                if(view instanceof ImageView  && data instanceof Bitmap){  
                    ImageView iv = (ImageView) view;  
                    iv.setImageBitmap((Bitmap) data);  
                    return true;  
                }else return false;  
            }  
        });  
		mListview.setAdapter(adapter);
		
		mListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				HashMap<String, Object> map = (HashMap<String, Object>) Contacts.this.adapter.getItem(position);
				String phone = (String) map.get("number");
				String name = (String) map.get("name");
				Intent intent = new Intent(Contacts.this, CreateGestureActivity.class);
				if(type.equals("call")) intent.putExtra("name", "make phone call" + name + "(" + phone + ")");
				else if(type.equals("message")) intent.putExtra("name", "send message" + name + "(" + phone + ")");
				startActivity(intent);
			}
			
		});
		
	}
	
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	@SuppressLint("NewApi")
	private void getPhoneContacts() {
		HashMap<String,Object> map = null;
		System.out.println("!!!");
		ContentResolver resolver = mContext.getContentResolver();
		System.out.println("!!!!");
		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, Phone.DISPLAY_NAME + " COLLATE LOCALIZED");
		System.out.println("!!!!!");
		if (phoneCursor != null) {
		    while (phoneCursor.moveToNext()) {
		    	System.out.println("!!!!!!");
		    	map = new HashMap<String, Object>();
		    	//得到手机号码
		    	String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
		    	//当手机号码为空的或者为空字段 跳过当前循环
		    	if (TextUtils.isEmpty(phoneNumber)) continue;
		    	//得到联系人名称
		    	String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
		    	//得到联系人ID
		    	Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
		    	//得到联系人头像ID
		    	Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
		    	//得到联系人头像Bitamp
		    	Bitmap contactPhoto = null;
		    	//photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
		    	if(photoid > 0 ) {
		    		Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactid);
		    		InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
		    		contactPhoto = BitmapFactory.decodeStream(input);
		    	}else {
		    		contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.contact_photo);
		    	}
		    	map.put("name", contactName);
		    	map.put("number", phoneNumber);
		    	map.put("image", contactPhoto);
		    	mListData.add(map);
		    }
//		    Collections.sort(mListData, new ComparatorValues());
		    phoneCursor.close();
		}
	}

	
}
