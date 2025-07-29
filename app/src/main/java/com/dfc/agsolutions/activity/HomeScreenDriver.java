package com.dfc.agsolutions.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.dfc.agsolutions.model.Branch;
import com.dfc.agsolutions.model.CurrantTripDataModel;
import com.dfc.agsolutions.model.DeletModel;
import com.dfc.agsolutions.model.ResponseArrayModel;
import com.dfc.agsolutions.model.TripCurrantDataModel;
import com.dfc.agsolutions.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressWarnings("all")
public class HomeScreenDriver extends Fragment {

    TextView branchname, branchnameadmin;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    BottomSheetDialog bottomSheetDialog;
    public String user_type;
    ImageView logout;

    TextView loacation, carname;
    TextView date, status, username, bhsd, fhsd, shsd, advance_money;
    TextView distance, textaccept, reached, ereturn, finish;
    LottieAnimationView nodata;
    CardView cd;
    ImageView btn;
    ProgressDialog dialog1;
    String id;
    SwipeRefreshLayout swipeRefreshLayout;

    TextView nodata1;
    ImageView rs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_screen_driver, container, false);

        dialog1 = new ProgressDialog(getActivity());
        dialog1.setMessage("Loading...");
        dialog1.setCancelable(false);
//        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                get_DriverTrip();
//            }
//        });
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ed = sp.edit();
        branchname = view.findViewById(R.id.tv_branch_name);
        bhsd = view.findViewById(R.id.bhsd);
        fhsd = view.findViewById(R.id.fhsd);
        shsd = view.findViewById(R.id.shsd);
        advance_money = view.findViewById(R.id.advance_money);
        rs = view.findViewById(R.id.rs);
        branchnameadmin = view.findViewById(R.id.tv_branch_name_admin);
        branchname.setText("" + sp.getString("userBranch", ""));
        branchnameadmin.setText("" + sp.getString("userBranch", ""));
        logout = view.findViewById(R.id.logout);
        loacation = view.findViewById(R.id.tv_location);
        date = view.findViewById(R.id.date);
        distance = view.findViewById(R.id.distance);
        carname = view.findViewById(R.id.tv_car_name);
        status = view.findViewById(R.id.status);
        textaccept = view.findViewById(R.id.tv_text_accept);
        btn = view.findViewById(R.id.btn);
        username = view.findViewById(R.id.username);

        nodata = view.findViewById(R.id.lav_no_data);
        nodata1 = view.findViewById(R.id.tv_no_data1);
        cd = view.findViewById(R.id.cd);
        Log.e("TAG", "get_branch: +++ " + sp.getString("token", ""));

        user_type = sp.getString("userType", "");

        Log.e("user_types", "user_type:-- " + user_type);

        branchnameadmin.setVisibility(View.GONE);
        branchname.setVisibility(View.VISIBLE);
        username.setText(sp.getString("fullName", ""));
        textaccept.setOnClickListener(v -> {

            Accept(textaccept.getText().toString());

        });

        branchnameadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.show();

            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                countryDialoglogout();

            }
        });
//        branchname.setText("" + sp.getString("user_type",""));

        get_DriverTrip();

        return view;
    }

    ProgressDialog dialog;

    public void get_branch() {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
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
                .baseUrl(getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        Api loginservice = retrofit.create(Api.class);
        Call<ResponseArrayModel> call = loginservice.get_branch();
        call.enqueue(new Callback<ResponseArrayModel>() {
            @Override
            public void onResponse(Call<ResponseArrayModel> call, Response<ResponseArrayModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {

                    List<Branch> branches = response.body().getData();
                    countryDialog(branches);


//                    setupSpinner(branchNames);
                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(getActivity(), "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseArrayModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }

    private void countryDialog(List<Branch> branches) {
        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.branch_bottom_sheet_dialog);

        RecyclerView rvCountry = bottomSheetDialog.findViewById(R.id.rvCountry);


        rvCountry.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCountry.setHasFixedSize(true);


        Home_Today_list_Adapter home_today_list_adapter = new Home_Today_list_Adapter(getActivity(), branches);
        rvCountry.setAdapter(home_today_list_adapter);


//        LinearLayout copy = bottomSheetDialog.findViewById(R.id.copyLinearLayout);

    }

    private void countryDialoglogout() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
        bottomSheetDialog.show();


        TextView logout = bottomSheetDialog.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ed.clear();
                ed.commit();

                startActivity(new Intent(getActivity(), ActivityCheckMobileNumber.class));
                getActivity().finish();

            }
        });


        TextView delete = bottomSheetDialog.findViewById(R.id.tvDeleteMyAccountAction);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_logout();


            }
        });


    }

    public void get_logout() {
//        ProgressDialog  dialog = new ProgressDialog(activity);
//        dialog.setMessage("Loading...");
//        dialog.setCancelable(false);
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
                .baseUrl(getString(R.string.commn_url))
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
                    Toast.makeText(getActivity(), "Your Account is Deleted!!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(getActivity(), ActivityIntroScreen.class));
                    getActivity().finish();

                } else {
                    Toast.makeText(getActivity(), "Network Error!!", Toast.LENGTH_SHORT).show();
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

    String Accept = "Accept";
    String Reached = "Reached";
    String Return = "Return";
    String Finish = "Finish";


    public void Accept(String accept) {
        dialog1.show();
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


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        Api loginservice = retrofit.create(Api.class);
        Log.e("idddddddddddd", "idss: " + id);
        Log.e("idddddddddddd", "statusss: " + accept);
        Call<TripCurrantDataModel> call = loginservice.get_TripCurrant(id, accept);
        call.enqueue(new Callback<TripCurrantDataModel>() {
            @Override
            public void onResponse(Call<TripCurrantDataModel> call, Response<TripCurrantDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode() == 200) {

                    TripCurrantDataModel apiResponse = response.body();

                    TripCurrantDataModel.UserData1 userData = apiResponse.getData();

                    Log.e("response", "onResponse: " + userData);

                    if (userData != null) {
                        nodata.setVisibility(View.GONE);
                        nodata1.setVisibility(View.GONE);
                        cd.setVisibility(View.VISIBLE);

//                        id = userData.getId();
//                        String evehicle = userData.getTrip_vehicle();
//                        String estatus = userData.getTrip_status();
//                        String eagency = userData.getTrip_agency();
//                        String edate = userData.getTrip_date();
//                        String ekm = userData.getTrip_km();
//
////                        TextView loacation,carname;
////                        TextView date,status;
////                        TextView distance,textaccept,cancel;
//
//                        Log.e("TAG", "onResponse: +++++"+userData);
////                        TextView status,loacation,date,driver,distance,carname;
//
//                        status.setText("Status : "+estatus);
//                        date.setText(edate);
//                        distance.setText(eagency);
//                        loacation.setText(ekm);
//                        carname.setText(evehicle);

                        get_DriverTrip();
                    } else {
                        nodata.setVisibility(View.VISIBLE);
                        nodata1.setVisibility(View.VISIBLE);
                        cd.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(getActivity(), "Mobile Number is Not Registered", Toast.LENGTH_SHORT).show();
                }
                dialog1.dismiss();

            }

            @Override
            public void onFailure(Call<TripCurrantDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog1.dismiss();
            }
        });


    }

    public void get_DriverTrip() {

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


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        Api loginservice = retrofit.create(Api.class);
        Call<CurrantTripDataModel> call = loginservice.get_DriverCurrantTrip();
        call.enqueue(new Callback<CurrantTripDataModel>() {
            @Override
            public void onResponse(Call<CurrantTripDataModel> call, Response<CurrantTripDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode() == 200) {

                    CurrantTripDataModel apiResponse = response.body();

                    CurrantTripDataModel.UserData1 userData = apiResponse.getData();

                    Log.e("response", "onResponse: " + userData);

                    if (userData != null) {
                        nodata.setVisibility(View.GONE);
                        nodata1.setVisibility(View.GONE);
                        cd.setVisibility(View.VISIBLE);

                        id = userData.getId();

                        Log.e("idddddddddddd", "id: " + id);
                        Log.e("idddddddddddd", "status: " + userData.getTrip_status());
                        String evehicle = userData.getTrip_vehicle();
                        String estatus = userData.getTrip_status();
                        Log.e("e", "onResponse===============: " + estatus);
                        String eagency = userData.getTrip_agency();
                        String edate = userData.getTrip_date();
                        String ekm = userData.getTrip_km();

//                        TextView loacation,carname;
//                        TextView date,status;
//                        TextView distance,textaccept,cancel;

                        Log.e("TAG", "onResponse: +++++" + userData);
//                        TextView status,loacation,date,driver,distance,carname;

                        try {
                            String dateq = userData.getTrip_date();

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                            // Parse the given date string
                            Date givenDate = sdf.parse(dateq);

                            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            String dateformate = outputDateFormat.format(givenDate);
                            date.setText(dateformate);
                        } catch (ParseException e) {
                            date.setText(edate);
//                throw new RuntimeException(e);
                        }

                        status.setText("Status : " + estatus);
//                        date.setText(edate);
                        distance.setText("Distance: " + ekm + " Km");
                        loacation.setText(eagency);
                        carname.setText(evehicle);
                        bhsd.setText(userData.getTrip_bhsd() + " Ltr");
                        fhsd.setText(userData.getTrip_hsd() + " Ltr");
                        shsd.setText(userData.getTrip_hsd_supplied() + " Ltr");
                        if (advance_money!=null){
                            rs.setVisibility(View.GONE);
                        }
                        rs.setVisibility(View.GONE);

                        advance_money.setText("Advance: " + "  \u20B9 "+ userData.getTrip_advance() );
                        Log.d("advance_money", "advance_money: " + advance_money);
                        if (estatus.equals("Pending")) {
                            textaccept.setText("Accept");
                        } else if (estatus.equals("Accept")) {
                            textaccept.setText("Reached");
                        } else if (estatus.equals("Reached")) {
                            textaccept.setText("Return");
                        } else if (estatus.equals("Return")) {
                            textaccept.setText("Finish");
                        }
                    } else {
                        nodata.setVisibility(View.VISIBLE);
                        nodata1.setVisibility(View.VISIBLE);
                        cd.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(getActivity(), "Mobile Number is Not Registered", Toast.LENGTH_SHORT).show();
                }
                dialog1.dismiss();
//                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<CurrantTripDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog1.dismiss();
//                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    public class Home_Today_list_Adapter extends RecyclerView.Adapter<Home_Today_list_Adapter.Holder> {
        private Activity context;

        List<Branch> arrayListTopic;

        public Home_Today_list_Adapter(Activity context, List<Branch> arrayListTopic) {
            this.context = context;
            this.arrayListTopic = arrayListTopic;
        }

        @Override
        public int getItemCount() {
            return arrayListTopic.size();
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itam_branch, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Holder holder, @SuppressLint("RecyclerView") final int position) {


            holder.status.setText("" + arrayListTopic.get(position).getBranchName());

            holder.click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ed.putString("userBranch", arrayListTopic.get(position).getBranchName());
                    ed.commit();
                    branchnameadmin.setText("" + sp.getString("userBranch", ""));
                    bottomSheetDialog.dismiss();
                }
            });

        }

        class Holder extends RecyclerView.ViewHolder {

            TextView status;
            LinearLayout click;

            public Holder(@NonNull View itemView) {
                super(itemView);


                status = itemView.findViewById(R.id.status);
                click = itemView.findViewById(R.id.click);

            }
        }


    }


}