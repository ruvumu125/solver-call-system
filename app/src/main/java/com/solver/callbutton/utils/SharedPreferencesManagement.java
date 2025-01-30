package com.solver.callbutton.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SharedPreferencesManagement {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE=0;

    private static final String PREF_NAME="SHARED_PREFERENCES_MANAGEMENT";
    private static final String ALLOWED_TO_VISIT="IS_ALLOWED_TO_VISIT";

    public static final String SAVED_DATE="SAVED_DATE";

    public SharedPreferencesManagement(Context context){

        this.context=context;
        sharedPreferences=context.getSharedPreferences(PREF_NAME,PRIVATE_MODE );
        editor=sharedPreferences.edit();

    }
    public void createEnregistrement(String date_enregistrement){

        editor.putBoolean(ALLOWED_TO_VISIT,true);
        editor.putString(SAVED_DATE,date_enregistrement);
        editor.apply();
    }
    public boolean isAllowed(){

        return sharedPreferences.getBoolean(ALLOWED_TO_VISIT,false);
    }
    public HashMap<String,String> getDetailsData(){

        HashMap<String,String> user = new HashMap<>();
        user.put(SAVED_DATE,sharedPreferences.getString(SAVED_DATE,null));
        return user;
    }
    public void remove_data(){

        editor.clear();
        editor.commit();
    }
}

