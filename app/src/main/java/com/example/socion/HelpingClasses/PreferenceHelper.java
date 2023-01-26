package com.example.socion.HelpingClasses;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {
    private final SharedPreferences sharedPreferences;

    public PreferenceHelper(Context context,String name){
        sharedPreferences = context.getSharedPreferences(name,Context.MODE_PRIVATE);
    }
    public void putBoolean(String key,Boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public Boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key,false);
    }

    public void putString(String key,String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }
    public String getString(String key){
        return sharedPreferences.getString(key,null);
    }
    public void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}