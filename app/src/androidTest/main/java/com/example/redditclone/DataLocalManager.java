package com.example.redditclone;

import android.content.Context;

import java.util.Set;

public class DataLocalManager {

    private static final String PREF_FIRST_INSTALL = "PREF_FIRST_INSTALL";
    private static final String PREF_USER_NAME = "PREF_USER_NAME";

    private static DataLocalManager instance;
    private MySharedPreferrences mySharedPreferrences;

    public static void init(Context context) {
        instance = new DataLocalManager();
        instance.mySharedPreferrences = new MySharedPreferrences(context);
    }

    public static DataLocalManager getInstance() {
        if (instance == null){
            instance = new DataLocalManager();
        }
        return instance;
    }

    public static void setFirstInstall(boolean isFirst){
        DataLocalManager.getInstance().mySharedPreferrences.putBooleanValue(PREF_FIRST_INSTALL, isFirst);

    }

    public static boolean getFirstInstalled(){
       return DataLocalManager.getInstance().mySharedPreferrences.getBooleanValue(PREF_FIRST_INSTALL);
    }


    public static void setUserName(Set<String> userName){
        DataLocalManager.getInstance().mySharedPreferrences.putStringSetValue(PREF_USER_NAME, userName);

    }

    public static Set<String> getUserName(){
        return DataLocalManager.getInstance().mySharedPreferrences.getStringSetValue(PREF_USER_NAME);


    }
}
