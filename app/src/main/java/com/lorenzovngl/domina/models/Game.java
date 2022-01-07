package com.lorenzovngl.domina.models;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

import com.lorenzovngl.domina.CPUPlayer;
import com.lorenzovngl.domina.Coder;
import com.lorenzovngl.domina.MoveEngine;
import com.lorenzovngl.domina.R;
import com.lorenzovngl.domina.activities.MainActivity;
import com.lorenzovngl.domina.controllers.PlayGamesController;
import com.lorenzovngl.domina.controllers.PrefsController;
import com.lorenzovngl.domina.utils.DialogUtils;
import com.lorenzovngl.domina.utils.StringUtils;

public class Game {

    private Checkerboard mCheckerboard;
    private Coder mCoder;
    private CPUPlayer mCPUPlayer;
    public Type mType;
    public int onePawnsCounter, twoPawnsCounter, oneLadiesCounter, twoLadiesCounter;
    private Context mContext;
    public MainActivity mActivity;
    private Pawn.Player mPlayerActive;

    public enum Type {
        PLAYER_VS_PLAYER, PLAYER_VS_CPU, RESUME
    }

    private enum Result {
        PLAYER_ONE_WINS, PLAYER_TWO_WINS, DRAW
    }

    public Game(Type type, MainActivity activity, Context context, RelativeLayout boardLayout){
        mContext = context;
        int player = -1;
        String config = Coder.INITIAL_CONFIG;
        if (type != Type.RESUME){
            mType = type;
        } else {
            mType = (PrefsController.getMatchMode(context) == PrefsController.PLAYER_VS_CPU) ?
                    Game.Type.PLAYER_VS_CPU : Game.Type.PLAYER_VS_PLAYER;
            config = PrefsController.getSavedGame(context);
            player = PrefsController.getSavedGameNextPlayer(context);
        }
        mActivity = activity;
        mCheckerboard = new Checkerboard(this, activity, context, boardLayout);
        mCoder = new Coder(this, config);
        mCheckerboard.build(config);
        if (type == Type.PLAYER_VS_CPU){
            mCPUPlayer = new CPUPlayer(activity, context, mCheckerboard);
        }
        mPlayerActive = null;
        if (type != Type.RESUME){
            changePlayer();
        } else {
            Log.i(StringUtils.getTag() + "STORED PLAYER", (player == Coder.PLAYER_ONE ? "PLAYER_ONE" : "PLAYER_TWO"));
            setPlayer((player == Coder.PLAYER_ONE ? Pawn.Player.PLAYER_ONE : Pawn.Player.PLAYER_TWO));
        }
        setCounters();
    }

    public Checkerboard getCheckerboard(){
        return mCheckerboard;
    }

    public Coder getCoder(){
        return mCoder;
    }

    public void setPlayerActive(Pawn.Player player){
        mPlayerActive = player;
    }

    public Pawn.Player getPlayerActive(){
        return mPlayerActive;
    }

    public Type getType(){
        return mType;
    }

    public void setCounters(){
        onePawnsCounter = mCoder.countItems(Coder.PAWN_ONE);
        oneLadiesCounter = mCoder.countItems(Coder.LADY_ONE);
        twoPawnsCounter = mCoder.countItems(Coder.PAWN_TWO);
        twoLadiesCounter = mCoder.countItems(Coder.LADY_TWO);
        mActivity.updateCounters(onePawnsCounter, oneLadiesCounter, twoPawnsCounter, twoLadiesCounter);
    }

    public void nextRound(){
        mCoder.addConfiguration(false);
        changePlayer();
        if (getWinner() != null){
            finish();
        }
        mCoder.saveMatch(mActivity);
    }

    private Result getWinner(){
        Result result = null;
        MoveEngine moveEngine = new MoveEngine(mActivity, this, mPlayerActive);
        if (moveEngine.findPossibleMoves(false, false, false, false).isEmpty()){
            if (oneLadiesCounter != twoLadiesCounter){
                result = (oneLadiesCounter > twoLadiesCounter) ? Result.PLAYER_ONE_WINS : Result.PLAYER_TWO_WINS;
            } else if (onePawnsCounter != twoPawnsCounter){
                result = (onePawnsCounter > twoPawnsCounter) ? Result.PLAYER_ONE_WINS : Result.PLAYER_TWO_WINS;
            } else {
                result = Result.DRAW;
            }
        } else if (onePawnsCounter + oneLadiesCounter == 0){
            result = Result.PLAYER_TWO_WINS;
        } else if (twoPawnsCounter + twoLadiesCounter == 0){
            result = Result.PLAYER_ONE_WINS;
        }
        return result;
    }

    public void finish(){
        String player1Name = PrefsController.getPlayerName(mContext, PrefsController.PLAYER1_NAME);
        String player2Name = (mType == Type.PLAYER_VS_PLAYER) ?
                PrefsController.getPlayerName(mContext, PrefsController.PLAYER2_NAME) : mContext.getResources().getString(R.string.cpu);
        if (getWinner() == Result.DRAW){
            DialogUtils.showSimpleDialog(mContext, R.string.game_over, R.string.draw);
        } else {
            String winner = (getWinner() == Result.PLAYER_ONE_WINS) ? player1Name : player2Name;
            AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
            alertDialog.setTitle(R.string.game_over);
            alertDialog.setMessage(mContext.getResources().getString(R.string.the_winner_is) + " " + winner + "!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mActivity.finish();
                        }
                    });
            alertDialog.show();
        }
    }

    public void changePlayer(){
        if (mPlayerActive == Pawn.Player.PLAYER_ONE){
            mPlayerActive = Pawn.Player.PLAYER_TWO;
            if (mType == Type.PLAYER_VS_CPU){
                mCPUPlayer.move();
            }
        } else {
            mPlayerActive = Pawn.Player.PLAYER_ONE;
        }
        mActivity.switchPlayer(mPlayerActive);
        Log.i(StringUtils.getTag(), "PLAYER " + ((mPlayerActive == Pawn.Player.PLAYER_ONE) ? "ONE" : "TWO"));
        mActivity.switchUndoButton(mCoder.canUndo());
        mActivity.switchRedoButton(mCoder.canRedo());
    }

    public void setPlayer(Pawn.Player player){
        mPlayerActive = player;
        mActivity.switchPlayer(mPlayerActive);
        Log.i(StringUtils.getTag(), "PLAYER " + ((mPlayerActive == Pawn.Player.PLAYER_ONE) ? "ONE" : "TWO"));
        mActivity.switchUndoButton(mCoder.canUndo());
        mActivity.switchRedoButton(mCoder.canRedo());
    }

    public void updateCountersForNewLady(Pawn.Player player){
        switch (player){
            case PLAYER_ONE:
                oneLadiesCounter++;
                mActivity.mOneLadiesCounterTV.setText(Integer.toString(oneLadiesCounter));
                onePawnsCounter--;
                mActivity.mOnePawnsCounterTV.setText(Integer.toString(onePawnsCounter));
                break;
            case PLAYER_TWO:
                twoLadiesCounter++;
                mActivity.mTwoLadiesCounterTV.setText(Integer.toString(twoLadiesCounter));
                twoPawnsCounter--;
                mActivity.mTwoPawnsCounterTV.setText(Integer.toString(twoPawnsCounter));
                break;
        }
    }
}
