package com.lorenzovngl.domina.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.lorenzovngl.domina.Coder;
import com.lorenzovngl.domina.models.Pawn;
import com.lorenzovngl.domina.utils.StringUtils;

public class PrefsController {

    private static final String PREFS_FILENAME = "prefs";
    private static final String DIFFICULTY = "difficulty";
    public static final int EASY = 0;
    public static final int MEDIUM = 1;
    public static final int HARD = 2;
    public static final String PLAYER1_NAME = "player_1_name";
    public static final String PLAYER2_NAME = "player_2_name";
    public static final String MATCH_MODE = "match_mode";
    public static final int PLAYER_VS_CPU = 0;
    public static final int PLAYER_VS_PLAYER = 1;
    private static final String THEME = "theme";
    public static final int THEME_CLASSIC = 0;
    public static final int THEME_MODERN = 1;


    public static void setDifficulty(Context context, int difficulty){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(DIFFICULTY, difficulty);
        editor.apply();
    }

    public static int getDifficulty(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
        return sharedPref.getInt(DIFFICULTY, EASY);
    }

    public static void setPlayerName(Context context, String player, String name){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(player, name);
        editor.apply();
    }

    public static String getPlayerName(Context context, String player){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
        return sharedPref.getString(player, "");
    }

    public static void setMatchMode(Context context, int mode){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(MATCH_MODE, mode);
        editor.apply();
    }

    public static int getMatchMode(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
        return sharedPref.getInt(MATCH_MODE, PLAYER_VS_PLAYER);
    }

    public static void setSavedGame(Context context, String config, int player){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Coder.STORED_CONFIG, config);
        editor.putInt(Coder.NEXT_PLAYER, player);
        editor.apply();
    }

    public static String getSavedGame(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
        return sharedPref.getString(Coder.STORED_CONFIG, null);
    }

    public static int getSavedGameNextPlayer(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
        return sharedPref.getInt(Coder.NEXT_PLAYER, Coder.PLAYER_ONE);
    }

    public static void setTheme(Context context, int theme){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(THEME, theme);
        editor.apply();
    }

    public static int getTheme(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
        return sharedPref.getInt(THEME, THEME_CLASSIC);
    }

}
