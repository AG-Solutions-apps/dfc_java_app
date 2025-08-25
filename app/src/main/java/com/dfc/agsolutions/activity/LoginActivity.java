package com.dfc.agsolutions.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dfc.agsolutions.app_utils.NetworkCheck;
import com.dfc.agsolutions.model.MyResponseData;
import com.dfc.agsolutions.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    Dialog dialog;
    SharedPreferences sp;
    SharedPreferences.Editor ed;

    TextView tvMobileNo, tvResendOTP;
    private EditText etOtpView;

    private FirebaseAuth mAuth;

    String password;

    private String verificationId;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        tvMobileNo = findViewById(R.id.tv_mobile_no);
        password = getIntent().getStringExtra("password");

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();

        dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.dialog_progress);
        dialog.setCancelable(false);

        tvMobileNo.setText(sp.getString("mobile", ""));

        // method to send otp to provided number
        sendVerificationCode(sp.getString("mobile", ""));

        etOtpView = findViewById(R.id.otp_view);

        findViewById(R.id.continues).setOnClickListener(v -> {
            String enteredOtp = etOtpView.getText().toString();
            if (TextUtils.isEmpty(enteredOtp) && enteredOtp.length() < 6) {
                Toast.makeText(LoginActivity.this,
                        "Please enter valid otp",
                        Toast.LENGTH_SHORT).show();
            } else {
                if (!NetworkCheck.isNetworkAvailable(this)) {
                    NetworkCheck.noInternet(LoginActivity.this);
                } else {
                    getLogin();
                }
            }
        });

        tvResendOTP = findViewById(R.id.tvResendOTP);
        // method to send otp to provided number resend otp
        tvResendOTP.setOnClickListener(v -> {
            if (tvResendOTP.getText().equals(getString(R.string.resend_otp)))
            // method to send otp to provided number
            {sendVerificationCode(sp.getString("mobile", ""));}
        });

        etOtpView.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s,
                                                  int start,
                                                  int count,
                                                  int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s,
                                              int start,
                                              int before,
                                              int count) {
                        if (s.toString().length() == 6) {
                            // Hide keyboard
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(etOtpView.getWindowToken(), 0);
                            }
                            etOtpView.clearFocus();
                        }
                    }
                }
        );

    }

    // show progress dialog
    private void showProgressDialog() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    // hide progress dialog
    private void hideProgressDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s,
                               @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            etOtpView.setText("");
            // hide progress dialog
            hideProgressDialog();
            Toast.makeText(LoginActivity.this,
                    "OTP sent",
                    Toast.LENGTH_SHORT).show();
            // Start the countdown timer here after OTP is sent
            startResendOtpTimer();
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            // hide progress dialog
            hideProgressDialog();
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                etOtpView.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // hide progress dialog
            hideProgressDialog();
            Toast.makeText(LoginActivity.this,
                    e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            super.onCodeAutoRetrievalTimeOut(s);
            // hide progress dialog
            hideProgressDialog();
            Toast.makeText(LoginActivity.this,
                    "OTP expired",
                    Toast.LENGTH_SHORT).show();
        }

    };

    // New method to handle the countdown timer
    private void startResendOtpTimer() {
        tvResendOTP.setEnabled(false); // Disable button while timer is running
        countDownTimer = new CountDownTimer(59000, 1000) { // 59 seconds, tick every 1 second
            @Override
            public void onTick(long millisUntilFinished) {
                String timerText = "Resend OTP in " + millisUntilFinished / 1000 + "s";
                tvResendOTP.setText(timerText);
            }

            @Override
            public void onFinish() {
                tvResendOTP.setText(getString(R.string.resend_otp));
                tvResendOTP.setEnabled(true); // Enable button again
            }
        }.start();
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        if (!NetworkCheck.isNetworkAvailable(this)) {
            NetworkCheck.noInternet(LoginActivity.this);
        } else {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(task -> {
                        // hide progress dialog
                        hideProgressDialog();
                        if (task.isSuccessful()) {
                            getLogin();
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // method to send otp to provided number
    private void sendVerificationCode(String number) {
        if (!NetworkCheck.isNetworkAvailable(this)) {
            NetworkCheck.noInternet(LoginActivity.this);
        } else {

            // show progress dialog
            showProgressDialog();

            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(getString(R.string._91) + number)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this)                 // (optional) Activity for callback binding
                            // If no activity is passed, reCAPTCHA verification can not be used.
                            .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);

        }
    }

    public void getLogin() {
        // show progress dialog
        showProgressDialog();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.common_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api loginService = retrofit.create(Api.class);
        Call<MyResponseData> call = loginService.getLogin(sp.getString("mobile", ""),
                sp.getString("password", ""));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MyResponseData> call,
                                   @NonNull Response<MyResponseData> response) {
                // Handle success
                hideProgressDialog();
                if (response.isSuccessful()) {
                    MyResponseData responseData = response.body();
                    if (responseData != null) {
                        // Access the data from the response
                        String token = responseData.getData().getToken();
                        ed.putString("token", token);
                        ed.putBoolean("firstTime", true);
                        ed.commit();

                        Intent intent = new Intent(LoginActivity.this,
                                HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        // Inside your LoginActivity, where you start HomeActivity
                        Log.e("LOGIN_DEBUG",
                                "About to call finish() on LoginActivity. HashCode: " + this.hashCode());
                        finish();
                        Log.d("LOGIN_DEBUG", "finish() was called on LoginActivity.");
                    }
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Something went wrong",
                            Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<MyResponseData> call,
                                  @Nullable Throwable t) {
                // Handle failure
                hideProgressDialog();
                if (t!= null && t.getMessage() != null) {
                    Toast.makeText(LoginActivity.this,
                            t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                } else  {
                    Toast.makeText(LoginActivity.this,
                            "Something went wrong",
                            Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        // hide progress dialog
        hideProgressDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("LOGIN_DEBUG", "onDestroy LoginActivity. HashCode: " + this.hashCode());
    }

}