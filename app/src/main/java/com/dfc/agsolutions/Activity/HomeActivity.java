package com.dfc.agsolutions.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dfc.agsolutions.AppUtils.Myapplication;
import com.dfc.agsolutions.Model.DeletModel;
import com.dfc.agsolutions.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {

    ImageView s_home, u_home, s_his, u_his, s_in, u_in, s_profile, u_profile;
    public String user_type;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    RelativeLayout home, his, in, profile, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();

        s_home = findViewById(R.id.s_home);
        u_home = findViewById(R.id.u_home);
        s_his = findViewById(R.id.s_his);
        u_his = findViewById(R.id.u_his);
        s_in = findViewById(R.id.s_in);
        u_in = findViewById(R.id.u_in);
        s_profile = findViewById(R.id.s_profile);
        u_profile = findViewById(R.id.u_profile);
        home = findViewById(R.id.home);
        his = findViewById(R.id.his);
        in = findViewById(R.id.in);
        profile = findViewById(R.id.profile);
        logout = findViewById(R.id.logout);

        if (!Myapplication.isNetworkAvailable()) {
            Myapplication.noInternet(HomeActivity.this);
        }

        user_type = sp.getString("userType", "");

        if (user_type.equals("1")) {
            HomeScreenDriver homeScreendriver = new HomeScreenDriver();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, homeScreendriver);
            transaction.commit();
        } else if (user_type.equals("2")) {
            in.setVisibility(View.GONE);
            his.setVisibility(View.GONE);

            HomeScreen homeScreen = new HomeScreen();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, homeScreen);
            transaction.commit();

        } else {
            in.setVisibility(View.GONE);
            his.setVisibility(View.GONE);

            HomeScreen homeScreen = new HomeScreen();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, homeScreen);
            transaction.commit();

        }

        logout.setOnClickListener(v -> logoutAction());

        findViewById(R.id.home).setOnClickListener(v -> {
            try {

                if (user_type.equals("1")) {
                    HomeScreenDriver homeScreendriver = new HomeScreenDriver();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment_container, homeScreendriver);
                    transaction.commit();
                } else if (user_type.equals("2")) {
                    in.setVisibility(View.GONE);
                    his.setVisibility(View.GONE);

                    HomeScreen homeScreen = new HomeScreen();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment_container, homeScreen);
                    transaction.commit();

                } else {
                    in.setVisibility(View.GONE);
                    his.setVisibility(View.GONE);

                    HomeScreen homeScreen = new HomeScreen();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment_container, homeScreen);
                    transaction.commit();

                }

                s_home.setVisibility(View.VISIBLE);
                s_his.setVisibility(View.GONE);
                s_in.setVisibility(View.GONE);
                s_profile.setVisibility(View.GONE);
                u_home.setVisibility(View.GONE);
                u_his.setVisibility(View.VISIBLE);
                u_in.setVisibility(View.VISIBLE);
                u_profile.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                Toast.makeText(HomeActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }

        });

        findViewById(R.id.his).setOnClickListener(v -> {
            try {


                HistoryFragment historyFragment = new HistoryFragment();
                FragmentManager fragmentManager1 = getSupportFragmentManager();
                FragmentTransaction transaction1 = fragmentManager1.beginTransaction();
                transaction1.replace(R.id.fragment_container, historyFragment);
                transaction1.commit();

                s_home.setVisibility(View.GONE);
                s_his.setVisibility(View.VISIBLE);
                s_in.setVisibility(View.GONE);
                s_profile.setVisibility(View.GONE);
                u_home.setVisibility(View.VISIBLE);
                u_his.setVisibility(View.GONE);
                u_in.setVisibility(View.VISIBLE);
                u_profile.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                Log.e("TAG", "his: " + e);
            }

        });
        findViewById(R.id.in).setOnClickListener(v -> {
            try {


                s_home.setVisibility(View.GONE);
                s_his.setVisibility(View.GONE);
                s_in.setVisibility(View.VISIBLE);
                s_profile.setVisibility(View.GONE);
                u_home.setVisibility(View.VISIBLE);
                u_his.setVisibility(View.VISIBLE);
                u_in.setVisibility(View.GONE);
                u_profile.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.e("TAG", "in: " + e);
            }

        });
        findViewById(R.id.profile).setOnClickListener(v -> {
            try {


                ProfileFragment profileFragment = new ProfileFragment();
                FragmentManager fragmentManager1 = getSupportFragmentManager();
                FragmentTransaction transaction1 = fragmentManager1.beginTransaction();
                transaction1.replace(R.id.fragment_container, profileFragment);
                transaction1.commit();

                s_home.setVisibility(View.GONE);
                s_his.setVisibility(View.GONE);
                s_in.setVisibility(View.GONE);
                s_profile.setVisibility(View.VISIBLE);
                u_home.setVisibility(View.VISIBLE);
                u_his.setVisibility(View.VISIBLE);
                u_in.setVisibility(View.VISIBLE);
                u_profile.setVisibility(View.GONE);
            } catch (Exception e) {
                Log.e("TAG", "profile: " + e);
            }

        });
        findViewById(R.id.in).setOnClickListener(v -> {
            try {


                Comming_SoonActivity profileFragment = new Comming_SoonActivity();
                FragmentManager fragmentManager1 = getSupportFragmentManager();
                FragmentTransaction transaction1 = fragmentManager1.beginTransaction();
                transaction1.replace(R.id.fragment_container, profileFragment);
                transaction1.commit();

                s_home.setVisibility(View.GONE);
                s_his.setVisibility(View.GONE);
                s_in.setVisibility(View.VISIBLE);
                s_profile.setVisibility(View.GONE);
                u_home.setVisibility(View.VISIBLE);
                u_his.setVisibility(View.VISIBLE);
                u_in.setVisibility(View.GONE);
                u_profile.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.e("TAG", "in: " + e);
            }

        });

    }

    private void logoutAction() {

        // Show alert dialog to confirm deletion
        new AlertDialog.Builder(HomeActivity.this)
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.are_you_sure_you_want_to_logut))
            .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                ed.clear();
                ed.commit();
                // Handle logout button click
                startActivity(new Intent(HomeActivity.this, ActivityCheckMobileNumber.class));
                HomeActivity.this.finish();
            })
            .setNegativeButton(getString(R.string.cancel), null)
            .show();

//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(HomeActivity.this, R.style.SheetDialog);
//        bottomSheetDialog.setContentView(R.layout.item_deactivate);
//        bottomSheetDialog.show();

//        RelativeLayout logout = findViewById(R.id.logout);
//        if (logout != null) {
//            logout.setOnClickListener(v -> {
//                ed.clear();
//                ed.commit();

//            });
//        }

//        TextView tvDeleteMyAccountAction = bottomSheetDialog.findViewById(R.id.tvDeleteMyAccountAction);
//        if (tvDeleteMyAccountAction != null) {
//            tvDeleteMyAccountAction.setOnClickListener(v -> deleteUserAccount());
//        }

    }

    public void deleteUserAccount() {
        Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.dialog_progress);
        dialog.setCancelable(false);
        dialog.show();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                .header("Authorization", "Bearer " + sp.getString("token", ""))
                .method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(getString(R.string.commn_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build();
        Api loginService = retrofit.create(Api.class);
        Call<DeletModel> call = loginService.get_deleteaccount();
        call.enqueue(new Callback<DeletModel>() {
            @Override
            public void onResponse(@NonNull Call<DeletModel> call,
                @NonNull Response<DeletModel> response) {

                DeletModel deleteModelResponse = response.body();
                if (deleteModelResponse != null && Objects.equals(deleteModelResponse.getCode(), "200")) {
                    ed.clear();
                    ed.commit();
                    Toast.makeText(HomeActivity.this, "Your Account is Deleted!!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(HomeActivity.this, ActivityIntroScreen.class));
                    HomeActivity.this.finish();

                } else {
                    Toast.makeText(HomeActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<DeletModel> call,
                @NonNull Throwable t) {
                dialog.dismiss();
                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}