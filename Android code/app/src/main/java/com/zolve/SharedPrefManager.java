package com.zolve;

import android.content.Context;
import android.content.SharedPreferences;

//here for this class we are using a singleton pattern

class SharedPrefManager {

    //the constants
    private static final String SHARED_PREF_NAME = "png_user_details";
    private static final String KEY_PHONE = "png_phone";

    private static SharedPrefManager mInstance;
    private Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) mInstance = new SharedPrefManager(context);
        return mInstance;
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    void userLogin(String phone) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PHONE, phone);
        editor.commit();
    }

    //this method will checker whether user is already logged in or not
    boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PHONE, null) != null;
    }

    String getLogin() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PHONE, null);
    }

    //this method will logout the user
    void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}