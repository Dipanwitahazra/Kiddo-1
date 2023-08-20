package com.parental.control.panjacreation.kiddo.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parental.control.panjacreation.kiddo.models.RecognitionModel;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class DBhandler {
    SharedPreferences sharedPreferences;

    public DBhandler(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
    }

    public void saveRegistered(List<RecognitionModel> registered){
        Gson gson = new Gson();
        String json = gson.toJson(registered);
        sharedPreferences.edit().putString(Constants.STORED_REC_KEY, json).apply();

    }

    public List<RecognitionModel> loadRegistered() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(Constants.STORED_REC_KEY, null);
        if (json != null) {
            Type type = new TypeToken<List<RecognitionModel>>() {
            }.getType();
            return gson.fromJson(json, type);
        }
        return new LinkedList<>();
    }
}
