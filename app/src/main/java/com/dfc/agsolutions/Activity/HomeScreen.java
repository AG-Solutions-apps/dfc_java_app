package com.dfc.agsolutions.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.dfc.agsolutions.Model.Branch;
import com.dfc.agsolutions.Model.DeletModel;
import com.dfc.agsolutions.Model.ResponseArrayModel;
import com.dfc.agsolutions.Model.ResponseTodoCount;
import com.dfc.agsolutions.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeScreen extends Fragment {

    TextView branchname, branchnameadmin,username;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    BottomSheetDialog bottomSheetDialog;
    public String user_type;
    ImageView creattrip;
    LinearLayout mainview;
    LottieAnimationView comingsun;
    ImageView logout;
    TextView totaltodo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_home_screen, container, false);

        username = view.findViewById(R.id.username);
        totaltodo = view.findViewById(R.id.totaltodo);


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home_screen);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ed = sp.edit();
        branchname = view.findViewById(R.id.branchname);
        branchnameadmin = view.findViewById(R.id.branchnameadmin);
        creattrip = view.findViewById(R.id.creattrip);
        mainview = view.findViewById(R.id.mainview);
        comingsun = view.findViewById(R.id.comingsun);
        branchname.setText("" + sp.getString("userBranch", ""));
        branchnameadmin.setText("" + sp.getString("userBranch", ""));
        logout = view.findViewById(R.id.logout);
        Log.e("TAG", "get_branch: +++ " + sp.getString("token", ""));
        username.setText(sp.getString("fullName",""));
        user_type = sp.getString("user_type", "");

        Log.e("user_types", "user_type:-- " + user_type);

        if (user_type.equals("1")) {
            mainview.setVisibility(View.GONE);
            comingsun.setVisibility(View.VISIBLE);
            branchnameadmin.setVisibility(View.GONE);
            creattrip.setVisibility(View.GONE);
            branchname.setVisibility(View.VISIBLE);

        } else if (user_type.equals("2")) {
            mainview.setVisibility(View.VISIBLE);

            branchnameadmin.setVisibility(View.VISIBLE);
            branchname.setVisibility(View.GONE);
            creattrip.setVisibility(View.GONE);
            comingsun.setVisibility(View.GONE);
            get_branch();
        } else {
            mainview.setVisibility(View.VISIBLE);
            comingsun.setVisibility(View.GONE);

            branchnameadmin.setVisibility(View.GONE);
            branchname.setVisibility(View.VISIBLE);
            creattrip.setVisibility(View.VISIBLE);

        }

        get_todotask();


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


        view.findViewById(R.id.vhivlelivk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (user_type.equals("1")) {
                    startActivity(new Intent(getActivity(), TripActivity.class));

                } else if (user_type.equals("2")) {
                    startActivity(new Intent(getActivity(), AdminTripActivity.class));

                } else {

                    startActivity(new Intent(getActivity(), AdminTripActivity.class));

                }
            }
        });

        view.findViewById(R.id.payment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), PaymentActivity.class).putExtra("header", "Payment"));


            }
        });

        view.findViewById(R.id.expence).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), ExpensesActicity.class).putExtra("header", "Expenses"));


            }
        });

        view.findViewById(R.id.driver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), DriverListActivity.class).putExtra("header", "Driver"));


            }
        });

        view.findViewById(R.id.service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), VehicleServiceActivity.class).putExtra("header", "Service"));

            }
        });

        view.findViewById(R.id.todolis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), ToDoListActivity.class).putExtra("header", "To Do list"));


            }
        });


        view.findViewById(R.id.creattrip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), CreatTrip.class).putExtra("header", "To Do list"));

            }
        });

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
                .baseUrl(getString(R.string.base_url))
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
    public void get_todotask() {
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
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        Api loginservice = retrofit.create(Api.class);
        Call<ResponseTodoCount> call = loginservice.get_gettodocount(sp.getString("userBranch", ""));
        call.enqueue(new Callback<ResponseTodoCount>() {
            @Override
            public void onResponse(Call<ResponseTodoCount> call, Response<ResponseTodoCount> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {


                    totaltodo.setText("" + response.body().getData());
//
//                    List<Branch> branches = response.body().getData();
//                    countryDialog(branches);


//                    setupSpinner(branchNames);
//                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(getActivity(), "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseTodoCount> call, Throwable t) {
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
        bottomSheetDialog.setContentView(R.layout.logout_bottom_sheet_dialog);
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


        TextView delete = bottomSheetDialog.findViewById(R.id.delete);

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

//    @Override
//    public void onBackPressed() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle(R.string.app_name);
//        builder.setIcon(R.mipmap.ic_launcher);
//        builder.setMessage("Do you want to exit?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        getActivity().finishAffinity();
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.show();
//
//    }

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
        public Home_Today_list_Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itam_branch, parent, false);
            return new Home_Today_list_Adapter.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Home_Today_list_Adapter.Holder holder, @SuppressLint("RecyclerView") final int position) {


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