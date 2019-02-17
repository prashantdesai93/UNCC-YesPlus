package com.ideas2app.uncc_yesplus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences settings = getSharedPreferences(LoginActivity.SHARED_PREFS_NAME, 0);
                boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

                if(hasLoggedIn)
                {
                    Intent i = new Intent(SplashActivity.this, EventActivity.class);
                    finish();
                    startActivity(i);
                } else {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    finish();
                    startActivity(i);
                }
            }
        }, 3000);
    }
}
