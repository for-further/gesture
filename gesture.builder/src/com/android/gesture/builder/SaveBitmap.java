package com.android.gesture.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

/** 
 * ����ͼƬ���� 
 *  
 *  
 */  
public class SaveBitmap {  
  
    private final static String CACHE = "/css";  
  
    /** 
     * ����ͼƬ�ķ��� ���浽sdcard 
     *  
     * @throws Exception 
     *  
     */  
    public static void saveImage(Bitmap bitmap, String imageName)  
            throws Exception {  
        String filePath = isExistsFilePath();  
        FileOutputStream fos = null;  
        File file = new File(filePath, imageName);  
        try {  
            fos = new FileOutputStream(file);  
            if (null != fos) {  
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);  //ѹ����90%����ʽΪpng
                fos.flush();  
                fos.close();  
            }  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
    /** 
     * ��ȡsd���Ļ���·���� һ���ڿ���sdCard�������Ŀ¼ 
     *  
     * @return SDPath 
     */  
    public static String getSDPath() {  
        File sdDir = null;  
        boolean sdCardExist = Environment.getExternalStorageState().equals(  
                android.os.Environment.MEDIA_MOUNTED); // �ж�sd���Ƿ����  
        if (sdCardExist) {  
            sdDir = Environment.getExternalStorageDirectory();// ��ȡ��Ŀ¼  
        } else {  
            Log.e("ERROR", "û���ڴ濨");  
        }  
        return sdDir.toString();  
    }  
  
    /** 
     * ��ȡ�����ļ���Ŀ¼ ��������ڴ��� �����򴴽��ļ��� 
     *  
     * @return filePath 
     */  
    private static String isExistsFilePath() {  
        String filePath = getSDPath() + CACHE;  
        File file = new File(filePath);  
        if (!file.exists()) {  
            file.mkdirs();  //�����ļ�
        }  
        return filePath;  
    }  
    /** 
     * ��ȡSDCard�ļ� 
     *  
     * @return Bitmap 
     */  
    public static Bitmap getImageFromSDCard(String imageName) {  
        String filepath = getSDPath() + CACHE  + "/" + imageName;  
        File file = new File(filepath);  
        if (file.exists()) {  
            Bitmap bm = BitmapFactory.decodeFile(filepath);  
            return bm;  
        }  
        return null;  
    }  
}  