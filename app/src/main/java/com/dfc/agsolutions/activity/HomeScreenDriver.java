package com.dfc.agsolutions.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.airbnb.lottie.LottieAnimationView;
import com.dfc.agsolutions.model.CurrantTripDataModel;
import com.dfc.agsolutions.model.DeletModel;
import com.dfc.agsolutions.model.TripCurrantDataModel;
import com.dfc.agsolutions.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeScreenDriver extends Fragment {

    TextView tv_branch_name, tv_branch_name_admin;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    BottomSheetDialog bottomSheetDialog;
    public String user_type;
    ImageView iv_logout;

    TextView tv_location, tv_car_name;
    TextView tv_date, tv_status, tv_user_name, tv_bhsd, tv_fhsd, tv_shsd, tv_advance_money;
    TextView tv_distance, tv_accept;
    LottieAnimationView lav_no_data;
    CardView cd;

    ProgressDialog dialog1;
    String id;

    TextView tv_no_data1;
    ImageView iv_rs;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_screen_driver,
                container,
                false);

        dialog1 = new ProgressDialog(requireActivity());
        dialog1.setMessage("Loading...");
        dialog1.setCancelable(false);

        sp = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        ed = sp.edit();

        tv_branch_name = view.findViewById(R.id.tv_branch_name);
        tv_bhsd = view.findViewById(R.id.bhsd);
        tv_fhsd = view.findViewById(R.id.fhsd);
        tv_shsd = view.findViewById(R.id.shsd);
        tv_advance_money = view.findViewById(R.id.advance_money);
        iv_rs = view.findViewById(R.id.rs);
        tv_branch_name_admin = view.findViewById(R.id.tv_branch_name_admin);

        String branchName = " " + sp.getString("userBranch", "");
        tv_branch_name.setText(branchName);

        String branchAdminName = " " + sp.getString("userBranch", "");
        tv_branch_name_admin.setText(branchAdminName);

        iv_logout = view.findViewById(R.id.logout);
        tv_location = view.findViewById(R.id.tv_location);
        tv_date = view.findViewById(R.id.tv_date);
        tv_distance = view.findViewById(R.id.tv_distance);
        tv_car_name = view.findViewById(R.id.tv_car_name);
        tv_status = view.findViewById(R.id.tv_status);
        tv_accept = view.findViewById(R.id.tv_text_accept);

        tv_user_name = view.findViewById(R.id.username);

        lav_no_data = view.findViewById(R.id.lav_no_data);
        tv_no_data1 = view.findViewById(R.id.tv_no_data1);
        cd = view.findViewById(R.id.cd);
        Log.e("TAG", "get_branch: +++ " + sp.getString("token", ""));

        user_type = sp.getString("userType", "");

        Log.e("user_types", "user_type:-- " + user_type);

        tv_branch_name_admin.setVisibility(View.GONE);
        tv_branch_name.setVisibility(View.VISIBLE);
        tv_user_name.setText(sp.getString("fullName", ""));
        tv_accept.setOnClickListener(v -> Accept(tv_accept.getText().toString()));

        tv_branch_name_admin.setOnClickListener(v -> bottomSheetDialog.show());

        iv_logout.setOnClickListener(v -> countryDialogLogout());

        getDriverTrip();

        return view;
    }

    ProgressDialog dialog;

    private void countryDialogLogout() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity(), R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
        bottomSheetDialog.show();

        TextView logout = bottomSheetDialog.findViewById(R.id.logout);
        if (logout != null) {
            logout.setOnClickListener(v -> {

                ed.clear();
                ed.commit();

                startActivity(new Intent(requireActivity(), ActivityCheckMobileNumber.class));
                requireActivity().finish();

            });
        }

        TextView delete = bottomSheetDialog.findViewById(R.id.tvDeleteMyAccountAction);

        if (delete != null) {
            delete.setOnClickListener(v -> getLogout());
        }


    }

    public void getLogout() {

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
                .baseUrl(getString(R.string.common_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        Api loginService = retrofit.create(Api.class);

        Call<DeletModel> call = loginService.getDeleteAccount();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<DeletModel> call,
                                   @NonNull Response<DeletModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {
                    ed.clear();
                    ed.commit();
                    Toast.makeText(requireActivity(), "Your Account is Deleted!!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(requireActivity(), ActivityIntroScreen.class));
                    requireActivity().finish();

                } else {
                    Toast.makeText(requireActivity(), "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<DeletModel> call,
                                  @NonNull Throwable t) {
                Log.e("DeleteModel", "" + t);
                dialog.dismiss();
            }
        });

    }

    public void Accept(String accept) {

        dialog1.show();

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
                .baseUrl(getString(R.string.common_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        Api loginService = retrofit.create(Api.class);

        Log.e("id", "ids: " + id);
        Log.e("id", "status: " + accept);
        Call<TripCurrantDataModel> call = loginService.get_TripCurrant(id, accept);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TripCurrantDataModel> call,
                                   @NonNull Response<TripCurrantDataModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode() == 200) {

                    TripCurrantDataModel apiResponse = response.body();

                    TripCurrantDataModel.UserData1 userData = apiResponse.getData();

                    Log.e("response", "onResponse: " + userData);

                    if (userData != null) {
                        lav_no_data.setVisibility(View.GONE);
                        tv_no_data1.setVisibility(View.GONE);
                        cd.setVisibility(View.VISIBLE);

                        getDriverTrip();

                    } else {
                        lav_no_data.setVisibility(View.VISIBLE);
                        tv_no_data1.setVisibility(View.VISIBLE);
                        cd.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(requireActivity(),
                            "Mobile Number is Not Registered",
                            Toast.LENGTH_SHORT).show();
                }
                dialog1.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<TripCurrantDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("TripCurrantDataModel: ", "" + t);
                dialog1.dismiss();
            }

        });

    }

    public void getDriverTrip() {

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
                .baseUrl(getString(R.string.common_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        Api loginService = retrofit.create(Api.class);

        Call<CurrantTripDataModel> call = loginService.getDriverCurrantTrip();

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<CurrantTripDataModel> call,
                                   @NonNull Response<CurrantTripDataModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode() == 200) {

                    CurrantTripDataModel apiResponse = response.body();

                    CurrantTripDataModel.UserData1 userData = apiResponse.getData();

                    Log.e("response", "onResponse: " + userData);

                    if (userData != null) {
                        lav_no_data.setVisibility(View.GONE);
                        tv_no_data1.setVisibility(View.GONE);
                        cd.setVisibility(View.VISIBLE);

                        id = userData.getId();

                        Log.e("id", "id: " + id);
                        Log.e("id", "status: " + userData.getTrip_status());
                        String e_vehicle = userData.getTrip_vehicle();
                        String e_status = userData.getTrip_status();
                        Log.e("e", "onResponse===============: " + e_status);
                        String e_agency = userData.getTrip_agency();
                        String e_date = userData.getTripDate();
                        String ekm = userData.getTrip_km();

                        Log.e("TAG", "onResponse: +++++" + userData);

                        try {
                            e_date = userData.getTripDate();
                            Log.e("TAG", "onResponse: +++++" + e_date);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                            // Parse the given date string
                            Date givenDate = sdf.parse(e_date);

                            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy",
                                    Locale.getDefault());

                            String dateFormate = null;
                            if (givenDate != null) {
                                dateFormate = outputDateFormat.format(givenDate);
                            }
                            tv_date.setText(dateFormate);
                        } catch (ParseException e) {
                            tv_date.setText(e_date);
                        }

                        String status = "Status : " + e_status;
                        tv_status.setText(status);
                        String distance = "Distance: " + ekm + " Km";
                        tv_distance.setText(distance);
                        tv_location.setText(e_agency);
                        tv_car_name.setText(e_vehicle);

                        String bhsd = userData.getTrip_bhsd() + " Ltr";
                        tv_bhsd.setText(bhsd);

                        String fhsd = userData.getTrip_hsd() + " Ltr";
                        tv_fhsd.setText(fhsd);

                        String shsd = userData.getTrip_hsd_supplied() + " Ltr";
                        tv_shsd.setText(shsd);

                        if (tv_advance_money !=null){
                            iv_rs.setVisibility(View.GONE);
                        }
                        iv_rs.setVisibility(View.GONE);

                        String advanceMoney = "Advance: " + "  ₹ "+ userData.getTrip_advance();
                        tv_advance_money.setText(advanceMoney);
                        Log.d("advance_money", "advance_money: " + tv_advance_money);
                        switch (e_status) {
                            case "Pending":
                                tv_accept.setText(getString(R.string.accept));
                                break;
                            case "Accept":
                                tv_accept.setText(getString(R.string.reached));
                                break;
                            case "Reached":
                                tv_accept.setText(getString(R.string.reached));
                            case "Return":
                                tv_accept.setText(getString(R.string.finish));
                                break;
                        }
                    } else {
                        lav_no_data.setVisibility(View.VISIBLE);
                        tv_no_data1.setVisibility(View.VISIBLE);
                        cd.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(requireActivity(),
                            "Mobile Number is Not Registered",
                            Toast.LENGTH_SHORT).show();
                }
                dialog1.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<CurrantTripDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("CurrantTripDataModel: ", "" + t);
                dialog1.dismiss();
            }
        });

    }

}