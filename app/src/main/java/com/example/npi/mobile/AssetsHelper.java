package com.example.npi.mobile;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AssetsHelper {

    public static String getServerUrl(Context context) {
        Properties properties = new Properties();
        try {
            String property;
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("app.properties");
            properties.load(inputStream);
            property = properties.getProperty("server-url");
            return property;
        } catch (IOException e) {
            Log.e("AssetsPropertyReader",e.toString());
        }
        return null;
    }
}