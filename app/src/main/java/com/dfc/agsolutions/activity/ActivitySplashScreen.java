package com.dfc.agsolutions.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

import com.dfc.agsolutions.model.MyResponseData;
import com.dfc.agsolutions.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressLint("CustomSplashScreen")
public class ActivitySplashScreen extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor ed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();

        new Handler().postDelayed(() -> {
            boolean isUserLoggedIn = sp.getBoolean("firstTime", false);
            if (isUserLoggedIn) {
                getLogin();
            } else {
                startActivity(new Intent(ActivitySplashScreen.this, ActivityIntroScreen.class));
                finish();
            }
        }, 1000);

    }

    public void getLogin() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Api loginService = retrofit.create(Api.class);
        Call<MyResponseData> call = loginService.getLogin(sp.getString("mobile", ""),
            sp.getString("password", ""));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MyResponseData> call,
                @NonNull Response<MyResponseData> response) {
                if (response.isSuccessful()) {
                    MyResponseData responseData = response.body();
                    if (responseData != null) {
                        // Access the data from the response
                        String token = responseData.getData().getToken();
                        ed.putString("token", token);
                        ed.commit();
                        String user_status = responseData.getData().getUser().getUser_status();
                        if (user_status.equals("Active")) {
                            startActivity(new Intent(ActivitySplashScreen.this, HomeActivity.class));
                            finish();
                        } else {
                            countryDialogLogout();
                        }
                    }
                } else {
                    Toast.makeText(ActivitySplashScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MyResponseData> call,
                @NonNull Throwable t) {
                startActivity(new Intent(ActivitySplashScreen.this, ActivityIntroScreen.class));
                finish();
            }
        });
    }

    private void countryDialogLogout() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.item_de_activate);
        bottomSheetDialog.show();

        TextView tvDeleteMyAccountAction = bottomSheetDialog.findViewById(R.id.tvDeleteMyAccountAction);
      if (tvDeleteMyAccountAction != null) {
        tvDeleteMyAccountAction.setOnClickListener(v -> finishAffinity());
      }
    }

}