package com.jeesun.twentyone.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by simon on 2017/12/28.
 */

public class AndroidFileUtils {
    private static final String TAG = AndroidFileUtils.class.getName();

    public static boolean fileCopy(String srcPath,String toPath, boolean targetIsDir) throws IOException {
        //如果原文件不存在
        File srcFile = new File(srcPath);
        File toFile = new File(toPath);
        if (!srcFile.exists()){
            Log.i(TAG, "源文件不存在");
            return false;
        }else{
            if (targetIsDir){
                if (!toFile.exists()){
                    Log.i(TAG, "目标文件夹不存在");
                    if (!toFile.mkdirs()){
                        Log.i(TAG, "目标文件夹创建失败");
                        return false;
                    }else{
                        Log.i(TAG, "目标文件夹创建成功");
                    }
                }
                String srcFileName = getFileName(srcPath);
                toFile = new File(toPath + "/" + srcFileName);
            }
            if (toFile.exists()){
                Log.i(TAG, "目标文件存在");
                if (toFile.delete()){
                    Log.i(TAG, "目标文件删除成功");
                    copy(srcFile, toFile);
                }else{
                    Log.i(TAG, "目标文件删除失败");
                    return false;
                }
            }else{
                copy(srcFile, toFile);
            }
        }
        return true;
    }

    private static void copy(File srcFile, File toFile) throws IOException{
        //获得原文件流
        FileInputStream inputStream = new FileInputStream(srcFile);
        byte[] data = new byte[1024];
        //输出流
        FileOutputStream outputStream =new FileOutputStream(toFile);
        //开始处理流
        while (inputStream.read(data) != -1) {
            outputStream.write(data);
        }
        inputStream.close();
        outputStream.close();
    }

    public static String getFileName(String pathandname){

        int start=pathandname.lastIndexOf("/");
        if(start!=-1){
            return pathandname.substring(start+1,pathandname.length());
        }else{
            return null;
        }

    }
}
