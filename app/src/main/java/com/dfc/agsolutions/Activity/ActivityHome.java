package com.dfc.agsolutions.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityHome extends AppCompatActivity {

    ImageView s_home, u_home, s_his, u_his, s_in, u_in, s_profile, u_profile;
    public String user_type;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    ProgressDialog dialog;
    RelativeLayout home,his,in,profile,logout;
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


        if (Myapplication.isNetworkAvailable()) {


        } else {
            Myapplication.noInternet(ActivityHome.this);

        }

        user_type = sp.getString("user_type", "");

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

        logout.setOnClickListener(v -> {
            countryDialoglogout();
        });

        findViewById(R.id.home).setOnClickListener(v -> {
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

//            HomeScreen homeScreen1 = new HomeScreen();
//            FragmentManager fragmentManager1 = getSupportFragmentManager();
//            FragmentTransaction transaction1 = fragmentManager1.beginTransaction();
//            transaction1.replace(R.id.fragment_container, homeScreen1);
//            transaction1.commit();

            s_home.setVisibility(View.VISIBLE);
            s_his.setVisibility(View.GONE);
            s_in.setVisibility(View.GONE);
            s_profile.setVisibility(View.GONE);
            u_home.setVisibility(View.GONE);
            u_his.setVisibility(View.VISIBLE);
            u_in.setVisibility(View.VISIBLE);
            u_profile.setVisibility(View.VISIBLE);

        });
        findViewById(R.id.his).setOnClickListener(v -> {
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
        });
        findViewById(R.id.in).setOnClickListener(v -> {
            s_home.setVisibility(View.GONE);
            s_his.setVisibility(View.GONE);
            s_in.setVisibility(View.VISIBLE);
            s_profile.setVisibility(View.GONE);
            u_home.setVisibility(View.VISIBLE);
            u_his.setVisibility(View.VISIBLE);
            u_in.setVisibility(View.GONE);
            u_profile.setVisibility(View.VISIBLE);
        });
        findViewById(R.id.profile).setOnClickListener(v -> {
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
        });

//        findViewById(R.id.in).setOnClickListener(v -> {
//            ComingSoonActivity profileFragment = new ComingSoonActivity();
//            FragmentManager fragmentManager1 = getSupportFragmentManager();
//            FragmentTransaction transaction1 = fragmentManager1.beginTransaction();
//            transaction1.replace(R.id.fragment_container, profileFragment);
//            transaction1.commit();
//
//            s_home.setVisibility(View.GONE);
//            s_his.setVisibility(View.GONE);
//            s_in.setVisibility(View.VISIBLE);
//            s_profile.setVisibility(View.GONE);
//            u_home.setVisibility(View.VISIBLE);
//            u_his.setVisibility(View.VISIBLE);
//            u_in.setVisibility(View.GONE);
//            u_profile.setVisibility(View.VISIBLE);
//        });

    }

    private void countryDialoglogout() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityHome.this, R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.logout_bottom_sheet_dialog);
        bottomSheetDialog.show();


        TextView logout = bottomSheetDialog.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ed.clear();
                ed.commit();

                startActivity(new Intent(ActivityHome.this, ActivityCheckMobileNumber.class));
                ActivityHome.this.finish();

            }
        });


        TextView delete = bottomSheetDialog.findViewById(R.id.delete);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_logout();


            }
        });


    }

    public void get_logout() {
        ProgressDialog  dialog = new ProgressDialog(ActivityHome.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
//
//        sp = PreferenceManager.getDefaultSharedPreferences(activity);
//        ed = sp.edit();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

//        if (token != null) {
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer " + sp.getString("token", ""))
                    .method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
//        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        Api loginservice = retrofit.create(Api.class);
        Call<DeletModel> call = loginservice.get_deleteaccount();
        call.enqueue(new Callback<DeletModel>() {
            @Override
            public void onResponse(Call<DeletModel> call, Response<DeletModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {
                    ed.clear();
                    ed.commit();
                    Toast.makeText(ActivityHome.this, "Your Account is Deleted!!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(ActivityHome.this, ActivityIntroScreen.class));
                    ActivityHome.this.finish();

                } else {
                    Toast.makeText(ActivityHome.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<DeletModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityHome.this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityHome.this.finishAffinity();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}