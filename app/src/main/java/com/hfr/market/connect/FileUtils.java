package com.hfr.market.connect;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {
    private static final String TAG = "FileUtils";
    private static Context mContext;

    public void init(Context context) {
        mContext = context;
    }

    public static String readFileData(String fileName) {
        BufferedReader reader = null;
        try {
            InputStream is = mContext.getClass().getClassLoader().
                    getResourceAsStream(fileName);
            InputStreamReader streamReader = new InputStreamReader(is);
            reader = new BufferedReader(streamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.valueOf(reader);
    }

    public String readSDFile(String fileName) {
        String path = getPath() + fileName;
        Log.i(TAG, "readSDFile path = " + path);
        File mParaFile = new File(path);
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = null;
        if (mParaFile.exists()) {
            try {
                reader = new BufferedReader(new FileReader(path));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.e(TAG, "File didn't exists ");
        }
        return String.valueOf(stringBuilder);
    }

    public void writeSDFile(String fileName, String data) {
        String path = getPath() + fileName;
        Log.i(TAG, "writeSDFile path = " + path);
        File mParaFile = new File(path);
        BufferedWriter writer = null;
        if (mParaFile.exists()) {
            try {
                writer = new BufferedWriter(new FileWriter(path));
                writer.write(data);
                writer.flush();
                Log.i(TAG, "data = " + data.length());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.e(TAG, "File didn't exists ");
        }
    }

    private String getPath() {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = mContext.getExternalCacheDir().getPath();
        } else {
            cachePath = mContext.getCacheDir().getPath();
        }
//        Log.i(TAG, "cachePath = " + cachePath);
        return cachePath;
    }

}
