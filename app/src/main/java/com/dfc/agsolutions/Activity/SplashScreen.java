package com.dfc.agsolutions.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import com.dfc.agsolutions.Model.MyResponseData;
import com.dfc.agsolutions.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor ed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();

        /* 1 = driver
        2 = admin
        3 = manager */

        new Handler().postDelayed(() -> {

            boolean isUserLoggedIn = sp.getBoolean("firstTime", false);

            if (isUserLoggedIn) {
                getLogin();
            } else {
                startActivity(new Intent(SplashScreen.this, ActivityIntroScreen.class));
                finish();
            }
        }, 1000);

    }

    private void countryDialogLogout() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.item_deactivate);
        bottomSheetDialog.show();

        TextView logout = bottomSheetDialog.findViewById(R.id.logout);
        if (logout != null) {
            logout.setOnClickListener(v -> finishAffinity());
        }

    }

    public void getLogin() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api loginService = retrofit.create(Api.class);

        Call<MyResponseData> call = loginService.get_login(sp.getString("mobile", ""),
                sp.getString("password", ""));

        call.enqueue(new Callback<MyResponseData>() {
            @Override
            public void onResponse(@NonNull Call<MyResponseData> call,
                                   @NonNull Response<MyResponseData> response) {
                Log.e("response..", response.toString());

                if (response.isSuccessful()) {

                    MyResponseData responseData = response.body();
                    if (responseData != null) {

                        // Access the data from the response
                        String token = responseData.getData().getToken();
                        ed.putString("token", token);
                        ed.commit();
                        String user_status = responseData.getData().getUser().getUser_status();

                        Log.e("user_status", "user_status: " + user_status);

                        if (user_status.equals("Active")) {

                            startActivity(new Intent(SplashScreen.this, ActivityHome.class));
                            finish();
                        } else {
                            countryDialogLogout();
                        }
                        Log.e("TAG", "token:----- " + token);

                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<MyResponseData> call,
                                  @NonNull Throwable t) {
                Log.e("Error: ", "" + t);
            }

        });

    }

}