package com.pyliavskyi.apz_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {


    SharedPreferences pref;

    SharedPreferences.Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "Pref";
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_CHAT_ID = "chatId";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_CHAT_TOKEN = "chatToken";
    public static final String KEY_INSTITUTION_ID = "institutionId";
    public static final String KEY_ROLE = "role";
    public static final String KEY_LANG = "lang";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.commit();
    }

    public void createLoginSession(String chatId, String token, String chatToken, String institutionId, String role, String lang) {

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_CHAT_ID, chatId);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_CHAT_TOKEN, chatToken);
        editor.putString(KEY_INSTITUTION_ID, institutionId);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_LANG, lang);

        // commit changes
        editor.commit();
    }

    public void putChatId(String chatId) {
        editor.putString(KEY_CHAT_ID, chatId);
        editor.commit();
    }

    public void putToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public void putChatToken(String chatToken) {
        editor.putString(KEY_CHAT_TOKEN, chatToken);
        editor.commit();
    }

    public void putInstitutionId(String institutionId) {
        editor.putString(KEY_INSTITUTION_ID, institutionId);
        editor.commit();
    }

    public void putRole(String role) {
        editor.putString(KEY_ROLE, role);
        editor.commit();
    }

    public void putLang(String lang) {
        editor.putString(KEY_LANG, lang);
        editor.commit();
    }


    public void checkLogin() {

        if (!this.isLoggedIn()) {

            Intent i = new Intent(_context, RegistrationActivity.class);

            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            _context.startActivity(i);
        }
    }


    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> userDetails = new HashMap<String, String>();

        userDetails.put(KEY_CHAT_ID, pref.getString(KEY_CHAT_ID, null));
        userDetails.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));
        userDetails.put(KEY_CHAT_TOKEN, pref.getString(KEY_CHAT_TOKEN, null));
        userDetails.put(KEY_INSTITUTION_ID, pref.getString(KEY_INSTITUTION_ID, null));
        userDetails.put(KEY_ROLE, pref.getString(KEY_ROLE, null));
        userDetails.put(KEY_LANG, pref.getString(KEY_LANG, null));

        return userDetails;
    }

    public void logoutUser() {

        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, RegistrationActivity.class);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        _context.startActivity(i);
    }


    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}

