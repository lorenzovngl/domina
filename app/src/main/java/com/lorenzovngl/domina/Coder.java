package com.lorenzovngl.domina;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.lorenzovngl.domina.activities.MainActivity;
import com.lorenzovngl.domina.controllers.PrefsController;
import com.lorenzovngl.domina.models.Checkerboard;
import com.lorenzovngl.domina.models.Game;
import com.lorenzovngl.domina.models.Pawn;
import com.lorenzovngl.domina.models.Square;
import com.lorenzovngl.domina.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Coder {

    private Checkerboard mCheckerboard;
    private Game mGame;
    private List<String> mConfigurations;
    private int currentConfig;
    public static final String STORED_CONFIG = "stored_config";
    public static final String NEXT_PLAYER = "next_player";
    public static final String INITIAL_CONFIG = "33333333333300000000111111111111";
    public static final int PLAYER_ONE = 0;
    public static final int PLAYER_TWO = 1;
    public static final char EMPTY_SQUARE = '0';
    public static final char PAWN_ONE = '1';
    public static final char LADY_ONE = '2';
    public static final char PAWN_TWO = '3';
    public static final char LADY_TWO = '4';

    public Coder(Game game, String config){
        mGame = game;
        mCheckerboard = game.getCheckerboard();
        mConfigurations = new ArrayList<>();
        mConfigurations.add(config);
        currentConfig = mConfigurations.size() - 1;
    }

    public void addConfiguration(boolean reset){
        if (reset){
            mConfigurations = new ArrayList<>();
            currentConfig = -1;
        }
        for (int i = currentConfig + 1; i < mConfigurations.size(); i++){
            mConfigurations.remove(i);
        }
        mConfigurations.add(codeCheckerboard(mCheckerboard));
        currentConfig++;
    }

    public boolean canUndo(){
        return currentConfig > 0;
    }

    public boolean canRedo(){
        return currentConfig < mConfigurations.size() - 1;
    }

    public void undo(){
        currentConfig--;
        mCheckerboard.build(mConfigurations.get(currentConfig));
        mGame.setCounters();
    }

    public void redo(){
        currentConfig++;
        mCheckerboard.build(mConfigurations.get(currentConfig));
        mGame.setCounters();
    }

    public static String codeCheckerboard(Checkerboard checkerboard){
        String checkerCode = "";
        for (int i = 0; i < Checkerboard.GRID_LENGTH; i++){
            for (int j = 0; j < Checkerboard.GRID_LENGTH; j++){
                Square square = checkerboard.getSquare(i, j);
                if (square.isPlayable()){
                    Pawn pawn = square.getPawn();
                    if (pawn == null){
                        checkerCode = checkerCode.concat(String.valueOf(EMPTY_SQUARE));
                    } else if (pawn.player == Pawn.Player.PLAYER_ONE){
                        if (!pawn.isLady()){
                            checkerCode = checkerCode.concat(String.valueOf(PAWN_ONE));
                        } else {
                            checkerCode = checkerCode.concat(String.valueOf(LADY_ONE));
                        }
                    } else {
                        if (!pawn.isLady()){
                            checkerCode = checkerCode.concat(String.valueOf(PAWN_TWO));
                        } else {
                            checkerCode = checkerCode.concat(String.valueOf(LADY_TWO));
                        }
                    }
                }
            }
        }
        return checkerCode;
    }

    public void saveMatch(MainActivity activity){
        PrefsController.setSavedGame(activity.getBaseContext(), mConfigurations.get(currentConfig),
                (mGame.getPlayerActive() == Pawn.Player.PLAYER_ONE) ? PLAYER_ONE : PLAYER_TWO);
        int type = (mGame.mType == Game.Type.PLAYER_VS_CPU) ? PrefsController.PLAYER_VS_CPU : PrefsController.PLAYER_VS_PLAYER;
        PrefsController.setMatchMode(activity.getApplicationContext(), type);
    }

    public int countItems(char itemType){
        String config = mConfigurations.get(currentConfig);
        Log.i(StringUtils.getTag(), config);
        int count = 0;
        for (int i = 0; i < config.length(); i++){
            char c = config.charAt(i);
            if (c == itemType){
                count++;
            }
        }
        return count;
    }

}
