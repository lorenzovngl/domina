package com.lorenzovngl.domina.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lorenzovngl.domina.MoveEngine;
import com.lorenzovngl.domina.activities.MainActivity;
import com.lorenzovngl.domina.R;
import com.lorenzovngl.domina.controllers.PrefsController;
import com.lorenzovngl.domina.controllers.ThemeController;

public class Pawn {

    private Square mSquare;
    private int mColor;
    private ImageView mImageView;
    private MainActivity mActivity;
    private Checkerboard mCheckerboard;
    private Game mGame;
    public Player player;
    public Type type;
    private int mWidth, mHeight;

    public enum Type {
        PAWN, LADY
    }

    public enum Player {
        PLAYER_ONE, PLAYER_TWO
    }

    public Pawn(Square square, MainActivity activity, Player player) {
        mSquare = square;
        mActivity = activity;
        mCheckerboard = square.getCheckerboard();
        mGame = mCheckerboard.getMatch();
        this.player = player;
        switch (player){
            case PLAYER_ONE:
                mColor = ThemeController.getColorId(mActivity.getApplicationContext(), ThemeController.Color.PAWN_ONE);
                break;
            case PLAYER_TWO:
                mColor = ThemeController.getColorId(mActivity.getApplicationContext(), ThemeController.Color.PAWN_TWO);
                break;
        }
        type = Type.PAWN;
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public void select(){
        mImageView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorActive));
        mCheckerboard.setSelectedPawn(this);
        MoveEngine moveEngine = new MoveEngine(mActivity, mCheckerboard.getMatch(), this);
        moveEngine.findPossibleMoves(true, false, false, false);
    }

    public void unselect(){
        mImageView.setBackgroundColor(0);
        if (mCheckerboard.getSelectedPawn() == this){
            mCheckerboard.setSelectedPawn(null);
        }
        mCheckerboard.disableTargetSquares();
    }

    public void toggleSelect(){
        mCheckerboard.unselectAll();
        if (mCheckerboard.getSelectedPawn() != this){
            select();
        }
    }

    public boolean areOpponents(Pawn opponent){
        return player != opponent.player;
    }

    public boolean canEat(Pawn opponent){
        return (this.areOpponents(opponent) && (this.isLady() || !opponent.isLady()));
    }

    public void setSquare(Square square){
        mSquare = square;
        square.setPawn(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mWidth, mHeight);
        params.addRule(RelativeLayout.ALIGN_LEFT, square.getImageView().getId());
        params.addRule(RelativeLayout.ALIGN_TOP, square.getImageView().getId());
        mImageView.setLayoutParams(params);
    }

    public Square getSquare(){
        return mSquare;
    }

    public ImageView buildView(Context context, Square square, int height, int width) {
        mWidth = width;
        mHeight = height;
        Drawable circle = null;
        mImageView = new ImageView(context);
        if (type == Type.LADY){
            makeLady(context);
        } else {
            switch (player){
                case PLAYER_ONE:
                    circle = ThemeController.getDrawable(context, ThemeController.Drawable.PAWN_ONE);
                    break;
                case PLAYER_TWO:
                    circle = ThemeController.getDrawable(context, ThemeController.Drawable.PAWN_TWO);
                    break;
            }
            mImageView.setImageDrawable(circle);
        }
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGame.getPlayerActive() == player){
                    toggleSelect();
                }
            }
        });
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mWidth, mHeight);
        params.addRule(RelativeLayout.ALIGN_LEFT, square.getImageView().getId());
        params.addRule(RelativeLayout.ALIGN_TOP, square.getImageView().getId());
        mImageView.setLayoutParams(params);
        return mImageView;
    }

    public void makeLady(Context context){
        Drawable circle = null;
        if (!isLady()){
            mGame.updateCountersForNewLady(player);
        }
        switch (player){
            case PLAYER_ONE:
                circle = ThemeController.getDrawable(context, ThemeController.Drawable.PAWN_ONE);
                break;
            case PLAYER_TWO:
                circle = ThemeController.getDrawable(context, ThemeController.Drawable.PAWN_TWO);
                break;
        }
        mImageView.setImageDrawable(circle);
        type = Type.LADY;
    }

    public boolean isLady(){
        return type == Type.LADY;
    }

    public void delete(){
        // TODO NullPointerException in molte segnalazioni... ????
        try {
            ViewGroup parent = (ViewGroup) mImageView.getParent();
            parent.removeView(mImageView);
        } catch (NullPointerException e){
            // Firebase potrebbe tracciare questa eccezione
            mImageView.setVisibility(View.GONE);
        }
    }

}
