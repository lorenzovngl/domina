package com.lorenzovngl.domina.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.lorenzovngl.domina.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final SplashActivity thisActivity = this;
        TextView textViewVersion = (TextView) findViewById(R.id.textview_version);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            textViewVersion.setText("v" + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(thisActivity, MenuActivity.class);
                startActivity(intent);
                thisActivity.finish();
            }
        }, 2000);
    }

}
