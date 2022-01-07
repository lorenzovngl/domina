package com.lorenzovngl.domina.models;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.lorenzovngl.domina.MoveEngine;
import com.lorenzovngl.domina.activities.MainActivity;
import com.lorenzovngl.domina.R;

public class Square {

    public int posX, posY;
    private ImageView imageView;
    private int mColor;
    private Pawn mPawn;
    private MainActivity mActivity;
    private Checkerboard mCheckerboard;

    public enum Direction {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    public Square(Checkerboard checkerboard, int y, int x, MainActivity activity) {
        posX = x;
        posY = y;
        mPawn = null;
        mActivity = activity;
        mCheckerboard = checkerboard;
    }

    public int getX(){
        return posX;
    }

    public int getY(){
        return posY;
    }

    public Checkerboard getCheckerboard(){
        return mCheckerboard;
    }

    public void setPawn(Pawn pawn){
        mPawn = pawn;
    }

    public void removePawn(){
        mPawn = null;
    }

    public Pawn getPawn(){
        return mPawn;
    }

    public boolean isEmpty(){
        return (getPawn() == null);
    }

    public ImageView buildView(final Context context, final int color, int id) {
        mColor = color;
        imageView = new ImageView(context);
        imageView.setBackgroundColor(ContextCompat.getColor(context, color));
        imageView.setId(id);
        return imageView;
    }

    public boolean isPlayable(){
        return (mColor == R.color.darkSquareClassic);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public int getColor(){
        return mColor;
    }

    public void enableTarget(final Context context, final Pawn pawnToMove, final Pawn pawnToEat){
        final Square thisSquare = this;
        imageView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorMove));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoveEngine moveEngine = new MoveEngine(context, mCheckerboard.getMatch(), pawnToMove);
                moveEngine.move(new Move(pawnToMove.getSquare(), thisSquare, pawnToEat));
                imageView.setBackgroundColor(ContextCompat.getColor(mActivity, mColor));
                disable();
            }
        });
    }

    public void disable(){
        imageView.setBackgroundColor(ContextCompat.getColor(mActivity, mColor));
        imageView.setOnClickListener(null);
    }

    public Square getClose(Direction direction){
        Square result = null;
        int x = getX();
        int y = getY();
        switch (direction){
            case TOP_LEFT:
                result = mCheckerboard.getSquare(y - 1, x - 1);
                break;
            case TOP_RIGHT:
                result = mCheckerboard.getSquare(y - 1, x + 1);
                break;
            case BOTTOM_LEFT:
                result = mCheckerboard.getSquare(y + 1, x - 1);
                break;
            case BOTTOM_RIGHT:
                result = mCheckerboard.getSquare(y + 1, x + 1);
                break;
        }
        return result;
    }

}
