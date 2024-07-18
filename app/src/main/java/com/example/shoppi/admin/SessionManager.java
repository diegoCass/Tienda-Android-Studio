package com.example.shoppi.admin;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "LoginSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setUserId(int userId) {
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1); // -1 como valor predeterminado si no se encuentra
    }
}
