package com.dfc.agsolutions.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import com.dfc.agsolutions.R;

public class ActivityIntroScreen extends
        AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen);

        findViewById(R.id.getStart).setOnClickListener(v -> {
            startActivity(new Intent(ActivityIntroScreen.this,
                    ActivityCheckMobileNumber.class));
            finish();
        });

    }

}