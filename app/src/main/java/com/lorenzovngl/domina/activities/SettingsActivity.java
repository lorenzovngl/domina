package com.lorenzovngl.domina.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.lorenzovngl.domina.R;
import com.lorenzovngl.domina.controllers.PrefsController;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle(R.string.settings);
        final Context context = this;
        ImageView themeClassicIV = (ImageView) findViewById(R.id.theme_classic);
        ImageView themeModernIV = (ImageView) findViewById(R.id.theme_modern);
        final ImageView themeClassicCheckedIV = (ImageView) findViewById(R.id.theme_classic_checked);
        final ImageView themeModernCheckedIV = (ImageView) findViewById(R.id.theme_modern_checked);
        if (PrefsController.getTheme(this) == PrefsController.THEME_CLASSIC){
            themeModernCheckedIV.setVisibility(View.INVISIBLE);
        } else {
            themeClassicCheckedIV.setVisibility(View.INVISIBLE);
        }
        themeClassicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeClassicCheckedIV.setVisibility(View.VISIBLE);
                themeModernCheckedIV.setVisibility(View.INVISIBLE);
                PrefsController.setTheme(context, PrefsController.THEME_CLASSIC);
            }
        });
        themeModernIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeClassicCheckedIV.setVisibility(View.INVISIBLE);
                themeModernCheckedIV.setVisibility(View.VISIBLE);
                PrefsController.setTheme(context, PrefsController.THEME_MODERN);
            }
        });
        Button buttonClose = (Button) findViewById(R.id.button_close);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
