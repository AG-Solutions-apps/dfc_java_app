package com.dfc.agsolutions.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dfc.agsolutions.R;
import com.dfc.agsolutions.app_utils.NetworkCheck;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

public class HomeActivity extends
        AppCompatActivity {

    private static final int FLEXIBLE = AppUpdateType.FLEXIBLE;
    private static final int IMMEDIATE = AppUpdateType.IMMEDIATE;
    private AppUpdateManager appUpdateManager;
    private ActivityResultLauncher<IntentSenderRequest> updateLauncher;

    ImageView iv_s_home, iv_u_home, iv_s_his, iv_u_his, iv_s_in, iv_u_in, iv_s_profile, iv_u_profile;
    public String user_type;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    RelativeLayout rl_home, rl_his, rl_in, rl_profile, rl_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Prepare launcher for update flow
        updateLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() != RESULT_OK) {
                        Toast.makeText(this, "Update cancelled!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
        // Code to run after 3 seconds
        // for example
        new Handler(Looper.getMainLooper()).postDelayed(this::checkForAppUpdate, 3000); // 3000ms = 3 seconds

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();

        iv_s_home = findViewById(R.id.s_home);
        iv_u_home = findViewById(R.id.u_home);
        iv_s_his = findViewById(R.id.s_his);
        iv_u_his = findViewById(R.id.u_his);
        iv_s_in = findViewById(R.id.s_in);
        iv_u_in = findViewById(R.id.u_in);
        iv_s_profile = findViewById(R.id.s_profile);
        iv_u_profile = findViewById(R.id.u_profile);
        rl_home = findViewById(R.id.home);
        rl_his = findViewById(R.id.his);
        rl_in = findViewById(R.id.in);
        rl_profile = findViewById(R.id.profile);
        rl_logout = findViewById(R.id.logout);

        if (!NetworkCheck.isNetworkAvailable(this)) {
            NetworkCheck.noInternet(HomeActivity.this);
        }

        user_type = sp.getString("userType", "");

        if (user_type.equals("1")) {
            HomeScreenDriver homeScreendriver = new HomeScreenDriver();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, homeScreendriver);
            transaction.commit();
        } else if (user_type.equals("2")) {
            rl_in.setVisibility(View.GONE);
            rl_his.setVisibility(View.GONE);

            HomeScreen homeScreen = new HomeScreen();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, homeScreen);
            transaction.commit();

        } else {
            rl_in.setVisibility(View.GONE);
            rl_his.setVisibility(View.GONE);

            HomeScreen homeScreen = new HomeScreen();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, homeScreen);
            transaction.commit();
        }

        rl_logout.setOnClickListener(v -> logoutAction());

        findViewById(R.id.home).setOnClickListener(v -> {

            try {

                if (user_type.equals("1")) {
                    HomeScreenDriver homeScreendriver = new HomeScreenDriver();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment_container, homeScreendriver);
                    transaction.commit();
                } else if (user_type.equals("2")) {
                    rl_in.setVisibility(View.GONE);
                    rl_his.setVisibility(View.GONE);

                    HomeScreen homeScreen = new HomeScreen();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment_container, homeScreen);
                    transaction.commit();

                } else {
                    rl_in.setVisibility(View.GONE);
                    rl_his.setVisibility(View.GONE);

                    HomeScreen homeScreen = new HomeScreen();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment_container, homeScreen);
                    transaction.commit();

                }

                iv_s_home.setVisibility(View.VISIBLE);
                iv_s_his.setVisibility(View.GONE);
                iv_s_in.setVisibility(View.GONE);
                iv_s_profile.setVisibility(View.GONE);
                iv_u_home.setVisibility(View.GONE);
                iv_u_his.setVisibility(View.VISIBLE);
                iv_u_in.setVisibility(View.VISIBLE);
                iv_u_profile.setVisibility(View.VISIBLE);

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

                iv_s_home.setVisibility(View.GONE);
                iv_s_his.setVisibility(View.VISIBLE);
                iv_s_in.setVisibility(View.GONE);
                iv_s_profile.setVisibility(View.GONE);
                iv_u_home.setVisibility(View.VISIBLE);
                iv_u_his.setVisibility(View.GONE);
                iv_u_in.setVisibility(View.VISIBLE);
                iv_u_profile.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                Log.e("TAG", "his: " + e);
            }

        });

        findViewById(R.id.in).setOnClickListener(v -> {

            try {
                iv_s_home.setVisibility(View.GONE);
                iv_s_his.setVisibility(View.GONE);
                iv_s_in.setVisibility(View.VISIBLE);
                iv_s_profile.setVisibility(View.GONE);
                iv_u_home.setVisibility(View.VISIBLE);
                iv_u_his.setVisibility(View.VISIBLE);
                iv_u_in.setVisibility(View.GONE);
                iv_u_profile.setVisibility(View.VISIBLE);
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

                iv_s_home.setVisibility(View.GONE);
                iv_s_his.setVisibility(View.GONE);
                iv_s_in.setVisibility(View.GONE);
                iv_s_profile.setVisibility(View.VISIBLE);
                iv_u_home.setVisibility(View.VISIBLE);
                iv_u_his.setVisibility(View.VISIBLE);
                iv_u_in.setVisibility(View.VISIBLE);
                iv_u_profile.setVisibility(View.GONE);

            } catch (Exception e) {
                Log.e("TAG", "profile: " + e);
            }

        });

        findViewById(R.id.in).setOnClickListener(v -> {

            try {

                ComingSoonFragment profileFragment = new ComingSoonFragment();
                FragmentManager fragmentManager1 = getSupportFragmentManager();
                FragmentTransaction transaction1 = fragmentManager1.beginTransaction();
                transaction1.replace(R.id.fragment_container, profileFragment);
                transaction1.commit();

                iv_s_home.setVisibility(View.GONE);
                iv_s_his.setVisibility(View.GONE);
                iv_s_in.setVisibility(View.VISIBLE);
                iv_s_profile.setVisibility(View.GONE);
                iv_u_home.setVisibility(View.VISIBLE);
                iv_u_his.setVisibility(View.VISIBLE);
                iv_u_in.setVisibility(View.GONE);
                iv_u_profile.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.e("TAG", "in: " + e);
            }

        });

    }

    private void logoutAction() {

        // Show alert dialog to confirm deletion
        new AlertDialog.Builder(HomeActivity.this)
                .setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.are_you_sure_you_want_to_logout))
                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                    ed.clear();
                    ed.commit();
                    // Handle logout button click
                    startActivity(new Intent(HomeActivity.this, ActivityCheckMobileNumber.class));
                    HomeActivity.this.finish();
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();

    }

    private void checkForAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                if (appUpdateInfo.isUpdateTypeAllowed(FLEXIBLE)) {
                    startUpdate(appUpdateInfo, FLEXIBLE);
                } else if (appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                    startUpdate(appUpdateInfo, IMMEDIATE);
                }
            }
        });
    }

    private void startUpdate(AppUpdateInfo appUpdateInfo,
                             int appUpdateType) {
        try {
            AppUpdateOptions options = AppUpdateOptions.newBuilder(appUpdateType).build();
            // startUpdateFlowForResult returns void, and the IntentSender is handled internally
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    updateLauncher,
                    options
            );
        } catch (Exception e) {
            Log.e("TAG", "startUpdate: " + e);
        }
    }

}