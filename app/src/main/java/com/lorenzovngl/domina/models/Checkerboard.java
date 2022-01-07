package com.lorenzovngl.domina.models;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lorenzovngl.domina.Coder;
import com.lorenzovngl.domina.R;
import com.lorenzovngl.domina.activities.MainActivity;
import com.lorenzovngl.domina.controllers.ThemeController;

import java.util.ArrayList;
import java.util.List;

public class Checkerboard {

    private Game mGame;
    private MainActivity mActivity;
    private Context mContext;
    private RelativeLayout mBoardLayout;
    private Square[][] mGrid;
    private Pawn mSelectedPawn;
    private List<Pawn> mPlayerOnePawns, mPlayerTwoPawns;
    public static int GRID_LENGTH = 8;

    public Checkerboard(Game game, MainActivity activity, Context context, RelativeLayout boardLayout){
        mGame = game;
        mActivity = activity;
        mContext = context;
        mBoardLayout = boardLayout;
    }

    public void build(String config){
        mBoardLayout.removeAllViews();
        mSelectedPawn = null;
        mPlayerOnePawns = new ArrayList<>();
        mPlayerTwoPawns = new ArrayList<>();
        int gridWidth = mBoardLayout.getMeasuredWidth();
        mGrid = new Square[8][];
        int idsArray[][] = {{R.id.square_1A, R.id.square_1B, R.id.square_1C, R.id.square_1D, R.id.square_1E, R.id.square_1F, R.id.square_1G, R.id.square_1H},
                {R.id.square_2A, R.id.square_2B, R.id.square_2C, R.id.square_2D, R.id.square_2E, R.id.square_2F, R.id.square_2G, R.id.square_2H},
                {R.id.square_3A, R.id.square_3B, R.id.square_3C, R.id.square_3D, R.id.square_3E, R.id.square_3F, R.id.square_3G, R.id.square_3H},
                {R.id.square_4A, R.id.square_4B, R.id.square_4C, R.id.square_4D, R.id.square_4E, R.id.square_4F, R.id.square_4G, R.id.square_4H},
                {R.id.square_5A, R.id.square_5B, R.id.square_5C, R.id.square_5D, R.id.square_5E, R.id.square_5F, R.id.square_5G, R.id.square_5H},
                {R.id.square_6A, R.id.square_6B, R.id.square_6C, R.id.square_6D, R.id.square_6E, R.id.square_6F, R.id.square_6G, R.id.square_6H},
                {R.id.square_7A, R.id.square_7B, R.id.square_7C, R.id.square_7D, R.id.square_7E, R.id.square_7F, R.id.square_7G, R.id.square_7H},
                {R.id.square_8A, R.id.square_8B, R.id.square_8C, R.id.square_8D, R.id.square_8E, R.id.square_8F, R.id.square_8G, R.id.square_8H}};
        View anchorTopView = null;
        View anchorLeftView = null;
        for (int i = 0; i < 8; i++) {
            mGrid[i] = new Square[8];
            for (int j = 0; j < 8; j++) {
                int color;
                if (i % 2 == 0) {
                    //color = (j % 2 == 0) ? R.color.lightSquareClassic : R.color.darkSquareClassic;
                    if (j % 2 == 0){
                        color = ThemeController.getColorId(mContext, ThemeController.Color.LIGHT_SQUARE);
                    } else {
                        color = ThemeController.getColorId(mContext, ThemeController.Color.DARK_SQUARE);
                    }
                } else {
                    //color = (j % 2 == 0) ? R.color.darkSquareClassic : R.color.lightSquareClassic;
                    if (j % 2 != 0){
                        color = ThemeController.getColorId(mContext, ThemeController.Color.LIGHT_SQUARE);
                    } else {
                        color = ThemeController.getColorId(mContext, ThemeController.Color.DARK_SQUARE);
                    }
                }
                mGrid[i][j] = new Square(this, i, j, mActivity);
                ImageView imageView = mGrid[i][j].buildView(mContext, color, idsArray[i][j]);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(gridWidth / 8, gridWidth / 8);
                if (i == 0){
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                }
                else {
                    params.addRule(RelativeLayout.BELOW, anchorTopView.getId());
                }
                if (j == 0){
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                } else {
                    params.addRule(RelativeLayout.RIGHT_OF, anchorLeftView.getId());
                }
                mBoardLayout.addView(imageView, params);
                anchorLeftView = imageView;
            }
            anchorTopView = anchorLeftView;
        }
        int k = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (mGrid[i][j].getColor() == ThemeController.getColorId(mContext, ThemeController.Color.DARK_SQUARE)) {
                    char c = config.charAt(k);
                    if (c != Coder.EMPTY_SQUARE){
                        Pawn pawn = null;
                        if (c == Coder.PAWN_ONE || c == Coder.LADY_ONE){
                            pawn = new Pawn(mGrid[i][j], mActivity, Pawn.Player.PLAYER_ONE);
                            mPlayerOnePawns.add(pawn);
                        } else if (c == Coder.PAWN_TWO || c == Coder.LADY_TWO) {
                            pawn = new Pawn(mGrid[i][j], mActivity, Pawn.Player.PLAYER_TWO);
                            mPlayerTwoPawns.add(pawn);
                        }
                        mGrid[i][j].setPawn(pawn);
                        ImageView imageView = pawn.buildView(mContext, mGrid[i][j], gridWidth / 8, gridWidth / 8);
                        if (c == Coder.LADY_ONE || c == Coder.LADY_TWO) {
                            pawn.makeLady(mContext);
                        }
                        mBoardLayout.addView(imageView);
                    }
                    k++;
                }
            }
        }
    }

    public Game getMatch(){
        return mGame;
    }

    public Square getSquare(int i, int j){
        try {
            return mGrid[i][j];
        } catch (ArrayIndexOutOfBoundsException e){
            return null;
        }

    }

    public void setSelectedPawn(Pawn pawn){
        mSelectedPawn = pawn;
    }

    public Pawn getSelectedPawn(){
        return mSelectedPawn;
    }

    public void unselectAll(){
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                Pawn pawn =  mGrid[i][j].getPawn();
                if (pawn != null){
                    pawn.unselect();
                }
            }
        }
    }

    public void disableTargetSquares(){
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                if (mGrid[i][j].getPawn() == null){
                    mGrid[i][j].disable();
                }
            }
        }
    }

    public void eatPawn(Pawn pawn){
        pawn.getSquare().removePawn();
        switch (pawn.player){
            case PLAYER_ONE:
                mPlayerOnePawns.remove(pawn);
                if (pawn.isLady()){
                    mGame.oneLadiesCounter--;
                } else {
                    mGame.onePawnsCounter--;
                }
                break;
            case PLAYER_TWO:
                mPlayerTwoPawns.remove(pawn);
                if (pawn.isLady()){
                    mGame.twoLadiesCounter--;
                } else {
                    mGame.twoPawnsCounter--;
                }
                break;
        }
        pawn.delete();
        mActivity.updateCounters(mGame.onePawnsCounter, mGame.oneLadiesCounter,
                mGame.twoPawnsCounter, mGame.twoLadiesCounter);
    }

}
