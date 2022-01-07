package com.lorenzovngl.domina;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.ViewGroup;

import com.lorenzovngl.domina.models.Checkerboard;
import com.lorenzovngl.domina.models.Game;
import com.lorenzovngl.domina.models.Move;
import com.lorenzovngl.domina.models.Pawn;
import com.lorenzovngl.domina.models.Square;
import com.lorenzovngl.domina.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MoveEngine {

    private Checkerboard mCheckerboard;
    private Game mGame;
    private Pawn mSelectedPawn;
    private Pawn.Player mPlayer;
    private Context mContext;

    public MoveEngine(Context context, Game game, Pawn selectedPawn) {
        mGame = game;
        mCheckerboard = game.getCheckerboard();
        mSelectedPawn = selectedPawn;
        mContext = context;
    }

    public MoveEngine(Context context, Game game, Pawn.Player player) {
        mGame = game;
        mCheckerboard = game.getCheckerboard();
        mPlayer = player;
        mContext = context;
    }

    public List<Move> findPossibleMoves(boolean enable, boolean onlyEscapeMoves, boolean onlyEatMoves, boolean noSuicideMoves) {
        Log.i(StringUtils.getTag(), Boolean.toString(enable));
        List<Move> moves = new ArrayList<>();
        if (mSelectedPawn != null) {
            moves.addAll(findPossibleMovesForPawn(mSelectedPawn, enable, onlyEscapeMoves, onlyEatMoves, noSuicideMoves));
        } else {
            for (int i = 0; i < Checkerboard.GRID_LENGTH; i++) {
                for (int j = 0; j < Checkerboard.GRID_LENGTH; j++) {
                    Pawn pawn = mCheckerboard.getSquare(i, j).getPawn();
                    if (pawn != null && pawn.player == mPlayer) {
                        moves.addAll(findPossibleMovesForPawn(pawn, enable, onlyEscapeMoves, onlyEatMoves, noSuicideMoves));
                    }
                }
            }
        }
        return moves;
    }

    private List<Move> findPossibleMovesForPawn(Pawn pawn, boolean enable, boolean onlyEscapeMoves, boolean onlyEatMoves, boolean noSuicideMoves) {
        List<Move> moves = new ArrayList<>();
        if (!pawn.isLady()) {
            int i = (pawn.player == Pawn.Player.PLAYER_ONE) ? pawn.getSquare().posY - 1 : pawn.getSquare().posY + 1;
            for (int j = pawn.getSquare().posX - 1; j <= pawn.getSquare().posX + 1; j++) {
                Move move = findPossibleMovesForPawnSub(pawn, i, j, enable, onlyEscapeMoves, onlyEatMoves, noSuicideMoves);
                if (move != null) {
                    moves.add(move);
                }
            }
        } else {
            for (int i = pawn.getSquare().posY - 1; i <= pawn.getSquare().posY + 1; i++) {
                for (int j = pawn.getSquare().posX - 1; j <= pawn.getSquare().posX + 1; j++) {
                    Move move = findPossibleMovesForPawnSub(pawn, i, j, enable, onlyEscapeMoves, onlyEatMoves, noSuicideMoves);
                    if (move != null) {
                        moves.add(move);
                    }
                }
            }
        }
        return moves;
    }

    private Move findPossibleMovesForPawnSub(Pawn pawn, int i, int j, boolean enable, boolean onlyEscapeMoves, boolean onlyEatMoves, boolean noSuicideMoves) {
        Move move = null;
        Square square = mCheckerboard.getSquare(i, j);
        if (square != null && square.isPlayable()) {
            if (square.getPawn() == null) {
                if (!onlyEatMoves) {
                    move = new Move(pawn.getSquare(), square, null);
                    if (enable) {
                        square.enableTarget(mContext, pawn, null);
                    }
                }
            } else if (pawn.canEat(square.getPawn())) {
                int newI = (pawn.getSquare().posY > square.posY) ? i - 1 : i + 1;
                int newJ = (pawn.getSquare().posX > square.posX) ? j - 1 : j + 1;
                Square newSquare = mCheckerboard.getSquare(newI, newJ);
                if (newSquare != null && newSquare.getPawn() == null) {
                    move = new Move(pawn.getSquare(), newSquare, square.getPawn());
                    if (enable) {
                        newSquare.enableTarget(mContext, pawn, square.getPawn());
                    }
                }
            } else if (square.getPawn().canEat(pawn)){
                if (onlyEscapeMoves){
                    // Need to escape if possible
                    int columnToEscape = (pawn.getSquare().posX > square.posX) ? pawn.getSquare().posX + 1 : pawn.getSquare().posX - 1;
                    Square squareToEscape = mCheckerboard.getSquare(square.posY, columnToEscape);
                    int newI = (pawn.getSquare().posY < square.posY) ? i - 1 : i + 1;
                    int newJ = (pawn.getSquare().posX < square.posX) ? j - 1 : j + 1;
                    Square newSquare = mCheckerboard.getSquare(newI, newJ);
                    if (newSquare != null && newSquare.getPawn() != null && squareToEscape != null && squareToEscape.getPawn() == null) {
                        move = new Move(pawn.getSquare(), squareToEscape, square.getPawn());
                        /*if (enable) {
                            squareToEscape.enableTarget(mContext, pawn, square.getPawn());
                        }*/
                    }
                }
            }
        }
        if (noSuicideMoves && move != null && !move.isEating() && move.isSuicide()) {
            move.printLog();
            move = null;
        }
        if (noSuicideMoves && move != null)
            move.printLog();
        return move;
    }

    public void move(final Move move) {
        final Pawn pawnToMove = move.getStartSquare().getPawn();
        Square destination = move.getEndSquare();
        Pawn pawnToEat = move.getPawnToEat();
        Log.i(StringUtils.getTag(), "Move from " + pawnToMove.getSquare().getX() + ", " + pawnToMove.getSquare().getY() +
        " to " + destination.getX() + ", " + destination.getY());
        final ViewGroup sceneRoot = (ViewGroup) pawnToMove.getImageView().getParent();
        // TODO in Lollipop (21) le transition si comportano in modo strano e sbagliato
        if (Build.VERSION.SDK_INT >= 21) {
            TransitionSet transitionSet = new TransitionSet();
            Transition transitionMove = mGame.mActivity.getWindow().getSharedElementEnterTransition();
            transitionMove.addTarget(pawnToMove.getImageView());
            transitionMove.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    Log.i(StringUtils.getTag(), "Transition ended!");
                    if (Build.VERSION.SDK_INT >= 19){
                        transition.removeListener(this);
                        finishMove(move, pawnToMove);
                    }
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
            transitionSet.addTransition(transitionMove);
            if (pawnToEat != null){
                Transition transitionEat = mGame.mActivity.getWindow().getEnterTransition();
                transitionEat.addTarget(pawnToEat.getImageView());
                transitionSet.addTransition(transitionEat);
            }
            try {
                TransitionManager.beginDelayedTransition(sceneRoot, transitionSet);
                Log.i(StringUtils.getTag(), "Transition started!");
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        pawnToMove.getSquare().removePawn();
        pawnToMove.setSquare(destination);
        mCheckerboard.unselectAll();
        if (pawnToEat != null) {
            mCheckerboard.eatPawn(pawnToEat);
        }
        switch (pawnToMove.player) {
            case PLAYER_ONE:
                if (destination.posY == 0) {
                    pawnToMove.makeLady(mContext);
                }
                break;
            case PLAYER_TWO:
                if (destination.posY == 7) {
                    pawnToMove.makeLady(mContext);
                }
                break;
        }
        if (Build.VERSION.SDK_INT < 21){
            finishMove(move, pawnToMove);
        }
    }

    private void finishMove(Move move, final Pawn pawnToMove){
        if (!move.isEating()) {
            mGame.nextRound();
        } else if (findPossibleMovesForPawn(pawnToMove, false, false, true, false).isEmpty()) {
            mGame.nextRound();
        } else if (mGame.getType() == Game.Type.PLAYER_VS_CPU && mGame.getPlayerActive() == Pawn.Player.PLAYER_TWO) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    move(findPossibleMovesForPawn(pawnToMove, false, false, true, false).get(0));
                }
            }, 700);
        }
    }

}
