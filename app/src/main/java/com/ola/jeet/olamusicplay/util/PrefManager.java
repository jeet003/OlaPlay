package com.ola.jeet.olamusicplay.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Map;


public class PrefManager {
    private SharedPreferences sharedPreferences;
    private Context context;
    private static final String pref_name="Ola";

    public PrefManager(@NonNull Context context)
    {
        this.context=context;
        sharedPreferences=context.getSharedPreferences(pref_name,Context.MODE_PRIVATE);
    }


    public void setNeverAskBeforeStorageState(boolean value)
    {
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putBoolean("neverAskBeforeStorageState",value);
        editor.commit();
    }
    public boolean getNeverAskBeforeStorageState()
    {
        return sharedPreferences.getBoolean("neverAskBeforeStorageState",false);
    }
    public void setPlaylist(String value)
    {
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("playlist",value);
        editor.commit();
    }
    public String getPlaylist()
    {
        return sharedPreferences.getString("playlist","");
    }
    public void displayAll()
    {
        Log.d("map values","showing");
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }
    }
    public void clearAll()
    {
        context.getSharedPreferences(pref_name,0).edit().clear().commit();
        //sharedPreferences.edit().clear().commit();
    }
}
