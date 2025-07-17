package com.dfc.agsolutions.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.dfc.agsolutions.Model.TodoListDataModel;
import com.dfc.agsolutions.R;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompleteTask extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    ProgressDialog dialog;
    SharedPreferences sp;
    LottieAnimationView nodata;
    CardView cd;
    SharedPreferences.Editor ed;
    public static Fragment newInstance(int i) {
        CompleteTask fragment = new CompleteTask();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, i);
        fragment.setArguments(args);
        return fragment;
    }

    RecyclerView rv;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_previous_history, container, false);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ed = sp.edit();
        nodata = view.findViewById(R.id.nodata);
//        cd = view.findViewById(R.id.cd);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        rv = view.findViewById(R.id.rv);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Implement your refresh logic here
                // This is usually where you would fetch new data
                nodata.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
                previousHistory();
            }
        });
        previousHistory();

        return view;

    }



    String id;

    public void previousHistory() {

        dialog.show();
//        fullname.clear();
//        mobile.clear();
//        dl_expiry.clear();
//        user_status.clear();
//        user_image.clear();
//        milageaaray.clear();

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
        Call<TodoListDataModel> call = loginservice.get_TodoList(sp.getString("userBranch", ""),String.valueOf(2));
        call.enqueue(new Callback<TodoListDataModel>() {
            @Override
            public void onResponse(Call<TodoListDataModel> call, Response<TodoListDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {
//
                    ArrayList<TodoListDataModel> branches = response.body().getData();

                    Log.e("Respone---------", "onResponse: "+response.body().getData().size());

//                    for (DriverListDataModel branch : branches) {
//
//                        fullname.add(branch.getFull_name());
//                        mobile.add(branch.getMobile());
//                        dl_expiry.add(branch.getDl_expiry());
//                        user_status.add(branch.getUser_status());
//                        user_image.add(branch.getUser_image());
//
//                    }


                    if(response.body().getData().size() == 0)
                    {
                        nodata.setVisibility(View.VISIBLE);
                        rv.setVisibility(View.GONE);
                    }else {
                        nodata.setVisibility(View.GONE);
                        rv.setVisibility(View.VISIBLE);
                        Home_Today_list_Adapter adapter = new Home_Today_list_Adapter(getActivity(), response.body().getData());
                        rv.setAdapter(adapter);
                    }

//
////response.body().getData().get(0).getReg_no();
////                    for (ServiceFatchVhicalDataModel branch : branches) {
////                        vhicalarray.add(branch.getReg_no());
//////                        vhicaldraiverarray.add(branch.getVehicle_driver());
//////                        milageaaray.add(branch.getVehicle_mileage());
////                    }
//                    for (ServiceFatchVhicalDataModel branch : branches) {
//                        vhicalarray.add(branch.getReg_no());
//                    }
////
//                    ArrayAdapter<String> adapter = new ArrayAdapter<>(DriverListActivity.this, R.layout.simple_spinner_item, vhicalarray);
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinner.setAdapter(adapter);

//
//                    ArrayAdapter<String> adapterdriver = new ArrayAdapter<>(CreatTrip.this, R.layout.simple_spinner_item, vhicaldraiverarray);
//                    adapterdriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinnerdriver.setAdapter(adapterdriver);


//                    setupSpinner(branchNames);
                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(getActivity(),  "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<TodoListDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    public void Update_Todolist(String idd) {

        dialog.show();
//        fullname.clear();
//        mobile.clear();
//        dl_expiry.clear();
//        user_status.clear();
//        user_image.clear();
//        milageaaray.clear();

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
        Call<TodoListDataModel> call = loginservice.get_CompleteUpdateList(idd,sp.getString("userBranch", ""));
        call.enqueue(new Callback<TodoListDataModel>() {
            @Override
            public void onResponse(Call<TodoListDataModel> call, Response<TodoListDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {
//
                    ArrayList<TodoListDataModel> branches = response.body().getData();

                    Log.e("Respone---------", "onResponse: "+response.body().getData().size());

//                    for (DriverListDataModel branch : branches) {
//
//                        fullname.add(branch.getFull_name());
//                        mobile.add(branch.getMobile());
//                        dl_expiry.add(branch.getDl_expiry());
//                        user_status.add(branch.getUser_status());
//                        user_image.add(branch.getUser_image());
//
//                    }

                    previousHistory();

//                    if(response.body().getData().size() == 0)
//                    {
//                        nodata.setVisibility(View.VISIBLE);
//                        rv.setVisibility(View.GONE);
//                    }else {
//                        nodata.setVisibility(View.GONE);
//                        rv.setVisibility(View.VISIBLE);
//                        Home_Today_list_Adapter adapter = new Home_Today_list_Adapter(getActivity(), response.body().getData());
//                        rv.setAdapter(adapter);
//                    }

//
////response.body().getData().get(0).getReg_no();
////                    for (ServiceFatchVhicalDataModel branch : branches) {
////                        vhicalarray.add(branch.getReg_no());
//////                        vhicaldraiverarray.add(branch.getVehicle_driver());
//////                        milageaaray.add(branch.getVehicle_mileage());
////                    }
//                    for (ServiceFatchVhicalDataModel branch : branches) {
//                        vhicalarray.add(branch.getReg_no());
//                    }
////
//                    ArrayAdapter<String> adapter = new ArrayAdapter<>(DriverListActivity.this, R.layout.simple_spinner_item, vhicalarray);
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinner.setAdapter(adapter);

//
//                    ArrayAdapter<String> adapterdriver = new ArrayAdapter<>(CreatTrip.this, R.layout.simple_spinner_item, vhicaldraiverarray);
//                    adapterdriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinnerdriver.setAdapter(adapterdriver);


//                    setupSpinner(branchNames);
                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(getActivity(),  "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<TodoListDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }

    public class Home_Today_list_Adapter extends RecyclerView.Adapter<Home_Today_list_Adapter.Holder> {
        private FragmentActivity activity;

        ArrayList<TodoListDataModel> data;

//        public Home_Today_list_Adapter(Activity context, ArrayList<OngoingTruckTypeModel> data) {
//
//        }

        public Home_Today_list_Adapter(FragmentActivity activity, ArrayList<TodoListDataModel> data) {
            this.activity = activity;
            this.data = data;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo_pending1, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Holder holder, @SuppressLint("RecyclerView") final int position) {


            holder.description.setText(data.get(position).getTodo_description());
//            id = data.get(position).getId();
            holder.btn.setOnClickListener(v -> {

                Update_Todolist(data.get(position).getId());
                notifyDataSetChanged();

            });

//            System.out.println("Last Trip Date: " + lastTripDateStr);
//            System.out.println("New Date (" + daysBeforeLastTrip + " days before last trip): " + formattedNewDate);



        }

        class Holder extends RecyclerView.ViewHolder {
            TextView description,loacation,date,driver,distance,carname;
//            LinearLayout click;
            ImageView btn;
            public Holder(@NonNull View itemView) {
                super(itemView);

                description = itemView.findViewById(R.id.description);
                btn = itemView.findViewById(R.id.btn);
//                loacation = itemView.findViewById(R.id.loacation);
//                date = itemView.findViewById(R.id.date);
//                driver = itemView.findViewById(R.id.driver);
//                distance = itemView.findViewById(R.id.distance);
//                carname = itemView.findViewById(R.id.carname);

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        previousHistory();
    }
}