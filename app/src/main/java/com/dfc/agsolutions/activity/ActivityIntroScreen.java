package com.dfc.agsolutions.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.dfc.agsolutions.R;

public class ActivityIntroScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen);
        findViewById(R.id.getStart).setOnClickListener(v -> startCheckMobileNumber());
    }

    // method to start the next activity
    private void startCheckMobileNumber() {
        startActivity(new Intent(ActivityIntroScreen.this, ActivityCheckMobileNumber.class));
        finish();
    }

}