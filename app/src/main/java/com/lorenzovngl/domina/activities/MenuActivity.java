package com.lorenzovngl.domina.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.app.AlertDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.lorenzovngl.domina.ObjectHolder;
import com.lorenzovngl.domina.R;
import com.lorenzovngl.domina.controllers.PlayGamesController;
import com.lorenzovngl.domina.controllers.PrefsController;
import com.lorenzovngl.domina.controllers.ThemeController;
import com.lorenzovngl.domina.utils.StringUtils;

public class MenuActivity extends AppCompatActivity {

    private Context mContext;
    private Button mButtonLoad;
    private ObjectHolder mObjectHolder;
    public static final String MENU_ACTION = "menu_action";
    public static final int PLAYER_VS_PLAYER = 0;
    public static final int PLAYER_VS_CPU = 1;
    public static final int LOAD_MATCH = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        final MenuActivity thisActivity = this;
        setContentView(R.layout.activity_menu);
        Button button1vCPU = (Button) findViewById(R.id.button_1vsCPU);
        Button button1v1 = (Button) findViewById(R.id.button_1vs1);
        mButtonLoad = (Button) findViewById(R.id.button_resume);
        Button buttonHowToPlay = (Button) findViewById(R.id.button_how_to_play);
        ImageView imageViewSettings = (ImageView) findViewById(R.id.settings_icon);
        ImageView imageViewAchieves = (ImageView) findViewById(R.id.trophy_icon);
        button1vCPU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlayersInfoButton(PLAYER_VS_CPU);
            }
        });
        button1v1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlayersInfoButton(PLAYER_VS_PLAYER);
            }
        });
        buttonHowToPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(thisActivity, RulesActivity.class);
                startActivity(intent);
            }
        });
        imageViewSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisActivity, SettingsActivity.class);
                startActivity(intent);
            }
        });
        imageViewAchieves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mObjectHolder.playGamesController.openAchievementsActivity();
            }
        });
        mObjectHolder = new ObjectHolder();
        //mObjectHolder.playGamesController = new PlayGamesController(this, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        RelativeLayout container = (RelativeLayout) findViewById(R.id.activity_menu_container);
        container.setBackgroundResource(ThemeController.getDrawableId(this, ThemeController.Drawable.MENU_GRADIENT));
        /*Log.d(StringUtils.getTag(), "onStart(): connecting");
        mObjectHolder.playGamesController.getApiClient().connect();*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*Log.d(StringUtils.getTag(), "onStop(): disconnecting");
        if (mObjectHolder.playGamesController.getApiClient().isConnected()) {
            mObjectHolder.playGamesController.getApiClient().disconnect();
        }*/
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (PrefsController.getSavedGame(this) == null) {
            mButtonLoad.setEnabled(false);
        } else {
            mButtonLoad.setEnabled(true);
            mButtonLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra(MENU_ACTION, LOAD_MATCH);
                    startActivity(intent);
                }
            });
        }
    }

    public void showPlayersInfoButton(final int mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.players);
        final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rootView = inflater.inflate(R.layout.dialog_players_info, null);
        final EditText editTextPlayer1Name = (EditText) rootView.findViewById(R.id.player_one_name);
        TextView textViewPlayer2Name = (TextView) rootView.findViewById(R.id.textview_player_two_name);
        final EditText editTextPlayer2Name = (EditText) rootView.findViewById(R.id.player_two_name);
        LinearLayout radioDifficultyLayout = (LinearLayout) rootView.findViewById(R.id.radio_difficulty_layout);
        RadioGroup radioDifficulty = (RadioGroup) rootView.findViewById(R.id.radio_difficulty);
        editTextPlayer1Name.setText(PrefsController.getPlayerName(mContext, PrefsController.PLAYER1_NAME));
        if (mode == PLAYER_VS_CPU) {
            textViewPlayer2Name.setVisibility(View.GONE);
            editTextPlayer2Name.setVisibility(View.GONE);
            switch (PrefsController.getDifficulty(mContext)) {
                case PrefsController.EASY:
                    ((RadioButton) rootView.findViewById(R.id.radio_easy)).setChecked(true);
                    break;
                case PrefsController.MEDIUM:
                    ((RadioButton) rootView.findViewById(R.id.radio_medium)).setChecked(true);
                    break;
                case PrefsController.HARD:
                    ((RadioButton) rootView.findViewById(R.id.radio_hard)).setChecked(true);
                    break;
            }
            radioDifficulty.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    switch (i) {
                        case R.id.radio_easy:
                            PrefsController.setDifficulty(mContext, PrefsController.EASY);
                            break;
                        case R.id.radio_medium:
                            PrefsController.setDifficulty(mContext, PrefsController.MEDIUM);
                            break;
                        case R.id.radio_hard:
                            PrefsController.setDifficulty(mContext, PrefsController.HARD);
                            break;
                    }
                }
            });
        } else {
            editTextPlayer2Name.setText(PrefsController.getPlayerName(mContext, PrefsController.PLAYER2_NAME));
            radioDifficultyLayout.setVisibility(View.GONE);
        }
        builder.setView(rootView);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                PrefsController.setPlayerName(mContext, PrefsController.PLAYER1_NAME, editTextPlayer1Name.getText().toString());
                if (mode == PLAYER_VS_PLAYER) {
                    PrefsController.setPlayerName(mContext, PrefsController.PLAYER2_NAME, editTextPlayer2Name.getText().toString());
                }
                dialog.dismiss();
                //mObjectHolder.playGamesController.unlockAchievement(R.string.achievement_play_your_first_game);
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(MENU_ACTION, mode);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
    }

}
