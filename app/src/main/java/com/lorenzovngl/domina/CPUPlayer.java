package com.lorenzovngl.domina;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.lorenzovngl.domina.controllers.PrefsController;
import com.lorenzovngl.domina.models.Checkerboard;
import com.lorenzovngl.domina.models.Move;
import com.lorenzovngl.domina.models.Pawn;
import com.lorenzovngl.domina.utils.DialogUtils;
import com.lorenzovngl.domina.utils.StringUtils;

import java.util.List;
import java.util.Random;

public class CPUPlayer {

    private Checkerboard mCheckerboard;
    private Context mContext;
    private Activity mActivity;

    public CPUPlayer(Activity activity, Context context, Checkerboard checkerboard){
        mCheckerboard = checkerboard;
        mContext = context;
        mActivity = activity;
    }

    /*
    * Strategia CPU
    *
    * Calcola tutte le mosse possibili.
    * Se sono disponibili mosse in cui si mangiano le pedine avversarie, scegline una a caso.
    * Scegli a caso una mossa tra quelle che, se fatte, mettono l'avversario in condizioni di mangiare.
    * Se non ci sono mosse come descritte alla riga sopra, effettua una mossa "suicida".
    * Se non ci sono mosse da fare, ha vinto l'avversario.
    *
    * Difficoltà facile: si scelgono mosse a caso e si mangia quando si può.
    * Difficoltà media: si evita di fare mosse suicidio.
    * Difficoltà difficile: se una pedina sta per essere mangiata cerca di scappare.
    *
    * */

    public void move(){
        int difficulty = PrefsController.getDifficulty(mContext);
        final MoveEngine moveEngine = new MoveEngine(mContext, mCheckerboard.getMatch(), Pawn.Player.PLAYER_TWO);
        List<Move> eatingMoves = moveEngine.findPossibleMoves(false, false, true, false);
        List<Move> escapingMoves = moveEngine.findPossibleMoves(false, true, false, false);
        List<Move> normalMovesNoSuicide = moveEngine.findPossibleMoves(false, false, false, true);
        List<Move> allMoves = moveEngine.findPossibleMoves(false, false, false, false);
        printList("Eating Moves", eatingMoves);
        printList("Escaping Moves", escapingMoves);
        printList("Normal Moves No Suicide", normalMovesNoSuicide);
        printList("All Moves", allMoves);
        Move choosenMoveHolder;
        Random rand = new Random();
        if (difficulty == PrefsController.EASY){
            if (!normalMovesNoSuicide.isEmpty()){
                choosenMoveHolder = normalMovesNoSuicide.get(rand.nextInt(normalMovesNoSuicide.size()));
            } else if (!allMoves.isEmpty()){
                choosenMoveHolder = allMoves.get(rand.nextInt(allMoves.size()));
                //DialogUtils.showSimpleDialog(mContext, "SUICIDE MOVE!", "SUICIDE MOVE!");
            } else {
                mCheckerboard.getMatch().finish();
                return;
            }
        } else if (difficulty == PrefsController.MEDIUM) {
            if (!eatingMoves.isEmpty()){
                choosenMoveHolder = eatingMoves.get(rand.nextInt(eatingMoves.size()));
            } else if (!normalMovesNoSuicide.isEmpty()){
                choosenMoveHolder = normalMovesNoSuicide.get(rand.nextInt(normalMovesNoSuicide.size()));
            } else if (!allMoves.isEmpty()){
                choosenMoveHolder = allMoves.get(rand.nextInt(allMoves.size()));
                //DialogUtils.showSimpleDialog(mContext, "SUICIDE MOVE!", "SUICIDE MOVE!");
            } else {
                mCheckerboard.getMatch().finish();
                return;
            }
        } else {
            if (!eatingMoves.isEmpty()) {
                choosenMoveHolder = eatingMoves.get(rand.nextInt(eatingMoves.size()));
            } else if (!escapingMoves.isEmpty()){
                choosenMoveHolder = escapingMoves.get(rand.nextInt(escapingMoves.size()));
            } else if (!normalMovesNoSuicide.isEmpty()){
                choosenMoveHolder = normalMovesNoSuicide.get(rand.nextInt(normalMovesNoSuicide.size()));
            } else if (!allMoves.isEmpty()){
                choosenMoveHolder = allMoves.get(rand.nextInt(allMoves.size()));
                //DialogUtils.showSimpleDialog(mContext, "SUICIDE MOVE!", "SUICIDE MOVE!");
            } else {
                mCheckerboard.getMatch().finish();
                return;
            }
        }
        final Move choosenMove = choosenMoveHolder;
        Log.i("CHOOSEN MOVE", "(" + choosenMove.getStartSquare().posX + ", " + choosenMove.getStartSquare().posY + ") -> (" +
                choosenMove.getEndSquare().posX + ", " + choosenMove.getEndSquare().posY + ")");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                moveEngine.move(choosenMove);
            }
        }, 1000);
    }

    public void printList(String name, List<Move> list){
        Log.i(StringUtils.getTag(), name);
        for (int i = 0; i < list.size(); i++){
            list.get(i).printLog();
        }
    }
}
