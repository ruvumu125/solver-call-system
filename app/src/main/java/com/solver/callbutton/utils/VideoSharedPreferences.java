package com.solver.callbutton.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.solver.callbutton.Model.Video;

public class VideoSharedPreferences {

    private SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE=0;
    private static final String PREF_NAME="video_shared_preferences_solver_office";

    public VideoSharedPreferences(Context context){

        this.context=context;
        sharedPreferences=context.getSharedPreferences(PREF_NAME,context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

    }
    public void saveListInLocal(Video list, String key) {

        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();

    }
    public Video getVideo(String key){

        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        return gson.fromJson(json, Video.class);

    }
    public void logout(){

        editor.clear();
        editor.commit();
    }
}
