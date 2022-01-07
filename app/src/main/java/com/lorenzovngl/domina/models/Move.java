package com.lorenzovngl.domina.models;


import android.util.Log;

import com.lorenzovngl.domina.utils.StringUtils;

public class Move {

    private Square startSquare, endSquare;
    private Pawn pawnToMove, pawnToEat;
    private Square.Direction direction;

    public Move(Square start, Square end, Pawn oneToEat) {
        startSquare = start;
        pawnToMove = startSquare.getPawn();
        endSquare = end;
        pawnToEat = oneToEat;
        if (startSquare.getX() > endSquare.getX()) {
            if (startSquare.getY() > endSquare.getY()) {
                direction = Square.Direction.TOP_RIGHT;
            } else {
                direction = Square.Direction.BOTTOM_RIGHT;
            }
        } else {
            if (startSquare.getY() > endSquare.getY()) {
                direction = Square.Direction.TOP_LEFT;
            } else {
                direction = Square.Direction.BOTTOM_LEFT;
            }
        }
    }

    public Square getStartSquare() {
        return startSquare;
    }

    public Square getEndSquare() {
        return endSquare;
    }

    public Pawn getPawnToEat() {
        return pawnToEat;
    }

    private Square.Direction getDirection() {
        return direction;
    }

    public boolean isEating() {
        return (pawnToEat != null);
    }

    public boolean isSuicide() {
        return (isSuicideSub(Square.Direction.TOP_LEFT, Square.Direction.BOTTOM_RIGHT)
                || isSuicideSub(Square.Direction.TOP_RIGHT, Square.Direction.BOTTOM_LEFT)
                || isSuicideSub(Square.Direction.BOTTOM_RIGHT, Square.Direction.TOP_LEFT)
                || isSuicideSub(Square.Direction.BOTTOM_LEFT, Square.Direction.TOP_RIGHT));
    }

    private boolean isSuicideSub(Square.Direction startSq, Square.Direction endSq) {
        try {
            Square possibleEaterSquare = endSquare.getClose(startSq);
            Square possibleEaterDestination = endSquare.getClose(endSq);
            Pawn possibleEater = possibleEaterSquare.getPawn();
            /*Log.i("IS_SUICIDE", startSq + ": " + possibleEaterSquare.getX() + ", " + possibleEaterSquare.getY() +
                    " " + Boolean.toString(possibleEater != null) + " " + possibleEater.areOpponents(pawnToMove) + " " +
                    (possibleEaterDestination.isEmpty() || possibleEaterDestination == startSquare));*/
            if (possibleEater != null && possibleEater.areOpponents(pawnToMove) &&
                    (possibleEaterDestination.isEmpty() || possibleEaterDestination == startSquare)) {
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException e) {

        }
        return false;
    }

    public void printLog() {
        Log.d(StringUtils.getTag(), "(" + getStartSquare().getX() + ", " + getStartSquare().getY() + ") -> (" +
                getEndSquare().getX() + ", " + getEndSquare().getY() + ") " +
                (isEating() ? "Eating" : "") + " " + (isSuicide() ? "Suicide" : ""));
    }

}