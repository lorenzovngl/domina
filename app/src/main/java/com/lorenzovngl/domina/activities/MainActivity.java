package com.lorenzovngl.domina.activities;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.lorenzovngl.domina.Coder;
import com.lorenzovngl.domina.R;
import com.lorenzovngl.domina.controllers.PrefsController;
import com.lorenzovngl.domina.controllers.ThemeController;
import com.lorenzovngl.domina.models.Game;
import com.lorenzovngl.domina.models.Pawn;

public class MainActivity extends AppCompatActivity {
    
    private Game mGame;
    public TextView mPlayerOneNameTV, mPlayerTwoNameTV,
            mOnePawnsCounterTV, mTwoPawnsCounterTV,
            mOneLadiesCounterTV, mTwoLadiesCounterTV;
    public ImageView mPlayerActiveIV;
    private Menu mMenu;
    public RelativeLayout mBoardLayout;
    private MainActivity mActivity;
    private Context mContext;
    private int mDisplayWidth;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout container = (LinearLayout) findViewById(R.id.points_layout);
        container.setBackgroundResource(ThemeController.getDrawableId(this, ThemeController.Drawable.MENU_GRADIENT));
        mActivity = this;
        mContext = this;
        ((TextView) findViewById(R.id.textview_pl1_name)).setText(PrefsController.getPlayerName(this, PrefsController.PLAYER1_NAME));
        mOnePawnsCounterTV = (TextView) findViewById(R.id.one_pawns_counter);
        mTwoPawnsCounterTV = (TextView) findViewById(R.id.two_pawns_counter);
        mOneLadiesCounterTV = (TextView) findViewById(R.id.one_ladies_counter);
        mTwoLadiesCounterTV = (TextView) findViewById(R.id.two_ladies_counter);
        mPlayerActiveIV = (ImageView) findViewById(R.id.player_active);
        mPlayerOneNameTV = (TextView) findViewById(R.id.textview_pl1_name);
        mPlayerTwoNameTV = (TextView) findViewById(R.id.textview_pl2_name);
        ImageView player1Img = (ImageView) findViewById(R.id.imageview_pl1);
        player1Img.setImageDrawable(ThemeController.getDrawable(this, ThemeController.Drawable.PLAYER_ONE_ICON));
        ImageView player2Img = (ImageView) findViewById(R.id.imageview_pl2);
        final View rootView = getWindow().getDecorView().getRootView();
        setBoardsColors();
        Bundle extras = getIntent().getExtras();
        final int matchTypeInt = extras.getInt(MenuActivity.MENU_ACTION);
        Game.Type matchTypeHolder = null;
        switch (matchTypeInt){
            case MenuActivity.PLAYER_VS_CPU:
                matchTypeHolder = Game.Type.PLAYER_VS_CPU;
                mPlayerTwoNameTV.setText(R.string.cpu);
                player2Img.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.cpu));
                break;
            case MenuActivity.PLAYER_VS_PLAYER:
                matchTypeHolder = Game.Type.PLAYER_VS_PLAYER;
                mPlayerTwoNameTV.setText(PrefsController.getPlayerName(this, PrefsController.PLAYER2_NAME));
                player2Img.setImageDrawable(ThemeController.getDrawable(this, ThemeController.Drawable.PLAYER_TWO_ICON));
                break;
            case MenuActivity.LOAD_MATCH:
                matchTypeHolder = Game.Type.RESUME;
                switch (PrefsController.getMatchMode(this)){
                    case PrefsController.PLAYER_VS_CPU:
                        mPlayerTwoNameTV.setText(R.string.cpu);
                        player2Img.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.cpu));
                        break;
                    case PrefsController.PLAYER_VS_PLAYER:
                        mPlayerTwoNameTV.setText(PrefsController.getPlayerName(this, PrefsController.PLAYER2_NAME));
                        player2Img.setImageDrawable(ThemeController.getDrawable(this, ThemeController.Drawable.PLAYER_TWO_ICON));
                }
                break;
        }
        final Game.Type matchType = matchTypeHolder;
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mBoardLayout = (RelativeLayout) findViewById(R.id.board_layout);
                        mGame = new Game(matchType, mActivity, mContext, mBoardLayout);
                        DisplayMetrics metrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        mDisplayWidth = metrics.widthPixels;
                        mPlayerActiveIV.getLayoutParams().width = (int) (mDisplayWidth / 2.5);
                        mPlayerActiveIV.setScaleType(ImageView.ScaleType.FIT_XY);
                        updateCounters();
                        rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        mMenu = menu;
        mMenu.findItem(R.id.menu_undo).setEnabled(false);
        mMenu.findItem(R.id.menu_redo).setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_undo:
                mGame.getCoder().undo();
                mGame.changePlayer();
                switchUndoButton(mGame.getCoder().canUndo());
                break;
            case R.id.menu_redo:
                mGame.getCoder().redo();
                mGame.changePlayer();
                switchRedoButton(mGame.getCoder().canRedo());
                break;
            /*case R.id.menu_new:
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Modalità di gioco");
                alertDialog.setMessage("Scegli la modalità con cui giocare");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "1 VS CPU",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mGame = new Game(Game.Type.PLAYER_VS_CPU, Coder.INITIAL_CONFIG, mActivity, mContext, mBoardLayout);
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "1 VS 1",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mGame = new Game(Game.Type.PLAYER_VS_PLAYER, Coder.INITIAL_CONFIG, mActivity, mContext, mBoardLayout);
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                break;
            case R.id.menu_save:
                mGame.getCoder().saveMatch(this);
                Toast.makeText(this, "Partita salvata", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_load:
                mGame.getCoder().loadMatch(this);
                Toast.makeText(this, "Partita caricata", Toast.LENGTH_SHORT).show();
                break;*/

        }
        return super.onOptionsItemSelected(item);
    }

    public void switchUndoButton(boolean enabled){
        int resId = enabled ? R.drawable.undo : R.drawable.undo_disabled;
        try {
            mMenu.findItem(R.id.menu_undo).setIcon(resId);
            mMenu.findItem(R.id.menu_undo).setEnabled(enabled);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void switchRedoButton(boolean enabled){
        int resId = enabled ? R.drawable.redo : R.drawable.redo_disabled;
        try {
            mMenu.findItem(R.id.menu_redo).setIcon(resId);
            mMenu.findItem(R.id.menu_redo).setEnabled(enabled);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void switchPlayer(Pawn.Player player){
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mPlayerActiveIV.getLayoutParams();
        int pxs = (int) (getResources().getDisplayMetrics().widthPixels / 2.1);
        if (player == Pawn.Player.PLAYER_ONE){
            params.rightMargin = pxs;
            params.leftMargin = 0;
        } else {
            params.leftMargin = pxs;
            params.rightMargin = 0;
        }
        if (Build.VERSION.SDK_INT >= 19) {
            TransitionManager.beginDelayedTransition((ViewGroup) mPlayerActiveIV.getParent());
        }
        mPlayerActiveIV.setLayoutParams(params);
    }

    public void updateCounters(int onePawns, int oneLadies, int twoPawns, int twoLadies){
        mOnePawnsCounterTV.setText(String.valueOf(onePawns));
        mOneLadiesCounterTV.setText(String.valueOf(oneLadies));
        mTwoPawnsCounterTV.setText(String.valueOf(twoPawns));
        mTwoLadiesCounterTV.setText(String.valueOf(twoLadies));
    }

    public void updateCounters(){
        mOnePawnsCounterTV.setText(String.valueOf(mGame.getCoder().countItems(Coder.PAWN_ONE)));
        mOneLadiesCounterTV.setText(String.valueOf(mGame.getCoder().countItems(Coder.LADY_ONE)));
        mTwoPawnsCounterTV.setText(String.valueOf(mGame.getCoder().countItems(Coder.PAWN_TWO)));
        mTwoLadiesCounterTV.setText(String.valueOf(mGame.getCoder().countItems(Coder.LADY_TWO)));
    }

    private void setBoardsColors(){
        LinearLayout playerOneBoard = (LinearLayout) findViewById(R.id.player_one_board);
        LinearLayout playerTwoBoard = (LinearLayout) findViewById(R.id.player_two_board);
        ImageView menOneIcon = (ImageView) findViewById(R.id.men_one_counter_iv);
        ImageView kingsOneIcon = (ImageView) findViewById(R.id.kings_one_counter_iv);
        ImageView menTwoIcon = (ImageView) findViewById(R.id.men_two_counter_iv);
        ImageView kingsTwoIcon = (ImageView) findViewById(R.id.kings_two_counter_iv);
        playerOneBoard.setBackground(ThemeController.getDrawable(this, ThemeController.Drawable.BOARD_ONE));
        playerTwoBoard.setBackground(ThemeController.getDrawable(this, ThemeController.Drawable.BOARD_TWO));
        menOneIcon.setImageDrawable(ThemeController.getDrawable(this, ThemeController.Drawable.PAWN_ONE));
        kingsOneIcon.setImageDrawable(ThemeController.getDrawable(this, ThemeController.Drawable.CROWN_ONE));
        menTwoIcon.setImageDrawable(ThemeController.getDrawable(this, ThemeController.Drawable.PAWN_TWO));
        kingsTwoIcon.setImageDrawable(ThemeController.getDrawable(this, ThemeController.Drawable.CROWN_TWO));
        mPlayerOneNameTV.setTextColor(ThemeController.getColor(this, ThemeController.Color.BOARD_ONE_TEXT));
        mPlayerTwoNameTV.setTextColor(ThemeController.getColor(this, ThemeController.Color.BOARD_TWO_TEXT));
    }
}
