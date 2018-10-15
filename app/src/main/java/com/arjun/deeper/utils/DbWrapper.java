package com.arjun.deeper.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.arjun.deeper.DeeperApplication;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DbWrapper {

    private volatile static DbWrapper instance;

    private volatile static SharedPreferences.Editor editor;

    private volatile static SharedPreferences sharedPreferences;

    public static DbWrapper getInstance() {
        if (instance == null) {
            synchronized (DbWrapper.class) {
                instance = new DbWrapper();
                Context context = DeeperApplication.getContext();
                sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
            }
        }
        instance.open();
        return instance;
    }

    private static void saveInFile(String fileName, String key, String value) {
        Context context = DeeperApplication.getContext();
        sharedPreferences = context.getSharedPreferences(fileName, context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static String getFromFile(String fileName, String key, String defaulValue) {
        Context context = DeeperApplication.getContext();
        sharedPreferences = context.getSharedPreferences(fileName, context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaulValue);
    }

    private String getArray(StackTraceElement[] arrayStr) {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement element : arrayStr) {
            builder.append(" ");
            builder.append(element.getClassName());
            builder.append(" ");
            builder.append(element.getMethodName());
        }
        return builder.toString();
    }

    private DbWrapper open() {
        return this;
    }

    public void close() {
        editor.apply();
    }

    public DbWrapper save(String key, String value) {
        editor.putString(key, value);
        return this;
    }

    public static String get(String key, String defaultValue) {
        getInstance();
        return sharedPreferences.getString(key, defaultValue);
    }

    public DbWrapper save(String key, String[] value) {
        editor.putStringSet(key, new HashSet<>(Arrays.asList(value)));
        return this;
    }

    public static Set<String> getStringSet(String key, HashSet<String> defaultValue) {
        getInstance();
        return sharedPreferences.getStringSet(key, defaultValue);
    }

    public DbWrapper save(String key, boolean value) {
        editor.putBoolean(key, value);
        return this;
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        getInstance();
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public DbWrapper save(String key, float value) {
        editor.putFloat(key, value);
        return this;
    }

    public static float getFloat(String key, float defaultValue) {
        getInstance();
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public DbWrapper save(String key, int value) {
        editor.putInt(key, value);
        return this;
    }

    public static int getInt(String key, int defaultValue) {
        getInstance();
        return sharedPreferences.getInt(key, defaultValue);
    }

    public DbWrapper save(String key, long value) {
        editor.putLong(key, value);
        return this;
    }

    public static long getLong(String key, long defaultValue) {
        getInstance();
        return sharedPreferences.getLong(key, defaultValue);
    }

    public static boolean keyFound(String key) {
        getInstance();
        return sharedPreferences.contains(key);
    }

    public DbWrapper delete(String key) {
        editor.remove(key);
        return this;
    }
}