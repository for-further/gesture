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

    /**��ȡ��Phon���ֶ�**/
    private static final String[] PHONES_PROJECTION = new String[] {
	    Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
   
    /**��ϵ����ʾ����**/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    
    /**�绰����**/
    private static final int PHONES_NUMBER_INDEX = 1;
    
    /**ͷ��ID**/
    private static final int PHONES_PHOTO_ID_INDEX = 2;
   
    /**��ϵ�˵�ID**/
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
                //�ж��Ƿ�Ϊ����Ҫ����Ķ���  
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
		// ��ȡ�ֻ���ϵ��
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, Phone.DISPLAY_NAME + " COLLATE LOCALIZED");
		System.out.println("!!!!!");
		if (phoneCursor != null) {
		    while (phoneCursor.moveToNext()) {
		    	System.out.println("!!!!!!");
		    	map = new HashMap<String, Object>();
		    	//�õ��ֻ�����
		    	String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
		    	//���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
		    	if (TextUtils.isEmpty(phoneNumber)) continue;
		    	//�õ���ϵ������
		    	String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
		    	//�õ���ϵ��ID
		    	Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
		    	//�õ���ϵ��ͷ��ID
		    	Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
		    	//�õ���ϵ��ͷ��Bitamp
		    	Bitmap contactPhoto = null;
		    	//photoid ����0 ��ʾ��ϵ����ͷ�� ���û�и���������ͷ�������һ��Ĭ�ϵ�
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
