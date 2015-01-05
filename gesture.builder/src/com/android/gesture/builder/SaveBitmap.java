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
 * 保存图片的类 
 *  
 *  
 */  
public class SaveBitmap {  
  
    private final static String CACHE = "/css";  
  
    /** 
     * 保存图片的方法 保存到sdcard 
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
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);  //压缩至90%，格式为png
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
     * 获取sd卡的缓存路径， 一般在卡中sdCard就是这个目录 
     *  
     * @return SDPath 
     */  
    public static String getSDPath() {  
        File sdDir = null;  
        boolean sdCardExist = Environment.getExternalStorageState().equals(  
                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在  
        if (sdCardExist) {  
            sdDir = Environment.getExternalStorageDirectory();// 获取根目录  
        } else {  
            Log.e("ERROR", "没有内存卡");  
        }  
        return sdDir.toString();  
    }  
  
    /** 
     * 获取缓存文件夹目录 如果不存在创建 否则则创建文件夹 
     *  
     * @return filePath 
     */  
    private static String isExistsFilePath() {  
        String filePath = getSDPath() + CACHE;  
        File file = new File(filePath);  
        if (!file.exists()) {  
            file.mkdirs();  //创建文件
        }  
        return filePath;  
    }  
    /** 
     * 获取SDCard文件 
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