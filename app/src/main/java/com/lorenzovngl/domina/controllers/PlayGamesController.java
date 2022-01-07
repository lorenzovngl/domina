package com.lorenzovngl.domina.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.lorenzovngl.domina.utils.StringUtils;

public class PlayGamesController implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private Context mContext;
    private Activity mActivity;
    private GoogleApiClient mGoogleApiClient;

    public PlayGamesController(Context context, Activity activity){
        mContext = context;
        mActivity = activity;
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
    }

    public GoogleApiClient getApiClient(){
        return mGoogleApiClient;
    }

    public void openAchievementsActivity(){
        mActivity.startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), 0);
    }

    public void unlockAchievement(int achievementId){
        Games.Achievements.unlock(mGoogleApiClient, mContext.getString(achievementId));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(mContext, "Connessione effettuata", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(mContext, "Connessione sospesa", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //DialogUtils.showSimpleDialog(mContext, "Connessione fallita", Integer.toString(connectionResult.getErrorCode()));
        if (connectionResult.hasResolution()){
            try {
                connectionResult.startResolutionForResult(mActivity, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.e(StringUtils.getTag(), "Exception while starting resolution activity", e);
            }
        } else {
            Log.i(StringUtils.getTag(), "No resolution");
        }
    }
}
