package com.dfc.agsolutions.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
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
import com.airbnb.lottie.LottieAnimationView;
import com.dfc.agsolutions.model.Branch;
import com.dfc.agsolutions.model.ResponseArrayModel;
import com.dfc.agsolutions.model.ResponseTodoCount;
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

    TextView tv_branch_name, tv_branch_admin_name, tv_user_name;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    BottomSheetDialog bottomSheetDialog;
    public String user_type;
    ImageView iv_creat_trip;
    LinearLayout ll_main_view;
    LottieAnimationView lav_coming_soon;
    ImageView iv_logout;
    TextView tv_total_todo;
    LinearLayout ll_vehicle_trips;

    ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_home_screen,
                container,
                false);

        tv_user_name = view.findViewById(R.id.username);
        tv_total_todo = view.findViewById(R.id.tv_total_todo);

        ll_vehicle_trips = view.findViewById(R.id.ll_vehicle_trips);

        sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        ed = sp.edit();
        tv_branch_name = view.findViewById(R.id.tv_branch_name);
        tv_branch_admin_name = view.findViewById(R.id.tv_branch_name_admin);
        iv_creat_trip = view.findViewById(R.id.ic_creat_trip);
        ll_main_view = view.findViewById(R.id.ll_main_view);
        lav_coming_soon = view.findViewById(R.id.animation_coming_soon);

        String branchName = sp.getString("userBranch", "");
        tv_branch_name.setText(branchName);

        String branchAdmin = sp.getString("userBranch", "");
        tv_branch_admin_name.setText(branchAdmin);

        iv_logout = view.findViewById(R.id.logout);
        Log.e("TAG", "get_branch: +++ " + sp.getString("token", ""));
        tv_user_name.setText(sp.getString("fullName", ""));
        user_type = sp.getString("userType", "");

        Log.e("user_types", "user_type:-- " + user_type);

        if (user_type.equals("1")) {
            ll_main_view.setVisibility(View.GONE);
            lav_coming_soon.setVisibility(View.VISIBLE);
            tv_branch_admin_name.setVisibility(View.GONE);
            iv_creat_trip.setVisibility(View.GONE);
            tv_branch_name.setVisibility(View.VISIBLE);
        } else if (user_type.equals("2")) {
            ll_main_view.setVisibility(View.VISIBLE);
            tv_branch_admin_name.setVisibility(View.VISIBLE);
            tv_branch_name.setVisibility(View.GONE);
            iv_creat_trip.setVisibility(View.GONE);
            lav_coming_soon.setVisibility(View.GONE);
            getBranch();
        } else {
            ll_main_view.setVisibility(View.VISIBLE);
            lav_coming_soon.setVisibility(View.GONE);
            tv_branch_admin_name.setVisibility(View.GONE);
            tv_branch_name.setVisibility(View.VISIBLE);
            iv_creat_trip.setVisibility(View.VISIBLE);
        }

        getToDoTask();

        tv_branch_admin_name.setOnClickListener(v -> bottomSheetDialog.show());

        iv_logout.setOnClickListener(v -> logOutUser());

        view.findViewById(R.id.ll_vehicle_trips).setOnClickListener(v -> {

            if (user_type.equals("1")) {
                startActivity(new Intent(getActivity(), TripActivity.class));

            } else if (user_type.equals("2")) {
                startActivity(new Intent(getActivity(), AdminTripActivity.class));

            } else {

                startActivity(new Intent(getActivity(), AdminTripActivity.class));

            }
        });

        view.findViewById(R.id.ll_payment).setOnClickListener(v ->
                startActivity(new Intent(getActivity(),
                        PaymentActivity.class).putExtra("header", "Payment")));

        view.findViewById(R.id.ll_all_vehicle).setOnClickListener(v ->
                startActivity(new Intent(getActivity(),
                        AllVehicleListActivity.class).putExtra("header", "Payment")));

        view.findViewById(R.id.ll_expenses).setOnClickListener(v ->
                startActivity(new Intent(getActivity(),
                        ExpensesActicity.class).putExtra("header", "Expenses")));

        view.findViewById(R.id.ll_drivers).setOnClickListener(v ->
                startActivity(new Intent(getActivity(),
                        DriverListActivity.class).putExtra("header", "Driver")));

        view.findViewById(R.id.ll_services).setOnClickListener(v ->
                startActivity(new Intent(getActivity(),
                        VehicleServiceActivity.class).putExtra("header", "Service")));

        view.findViewById(R.id.ll_todo_list).setOnClickListener(v ->
                startActivity(new Intent(getActivity(),
                        ToDoListActivity.class).putExtra("header", "To Do list")));

        view.findViewById(R.id.ic_creat_trip).setOnClickListener(v ->
                startActivity(new Intent(getActivity(),
                        CreatTrip.class).putExtra("header", "To Do list")));

        return view;
    }

    public void getBranch() {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        Log.e("dialog", "dialog1: ");

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

        Call<ResponseArrayModel> call = loginService.getBranch();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseArrayModel> call,
                                   @NonNull Response<ResponseArrayModel> response) {

                ResponseArrayModel responseArrayModel = response.body();
                if (responseArrayModel != null && responseArrayModel.getCode().equalsIgnoreCase("200")) {

                    List<Branch> branches = responseArrayModel.getData();
                    countryDialog(branches);

                    Log.e("response..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(getActivity(), "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                Log.e("dismiss", "dismiss3: ");

            }

            @Override
            public void onFailure(@NonNull Call<ResponseArrayModel> call,
                                  @NonNull Throwable t) {
                dialog.dismiss();
                Log.e("dismiss", "dismiss3: ");
            }
        });
    }

    public void getToDoTask() {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        Log.e("dialog", "dialog2: ");

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

        Call<ResponseTodoCount> call = loginService.getToDoCount(sp.getString("userBranch", ""));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseTodoCount> call,
                                   @NonNull Response<ResponseTodoCount> response) {

                ResponseTodoCount responseTodoCount = response.body();
                if (responseTodoCount != null && responseTodoCount.getCode().equalsIgnoreCase("200")) {

                    tv_total_todo.setText(responseTodoCount.getData());

                } else {
                    Toast.makeText(getActivity(), "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                Log.e("dismiss", "dismiss1: ");

            }

            @Override
            public void onFailure(@NonNull Call<ResponseTodoCount> call,
                                  @NonNull Throwable t) {
                dialog.dismiss();
                Log.e("dismiss", "dismiss11: $t");
            }
        });
    }

    private void countryDialog(List<Branch> branches) {

        try {
            if (isAdded()) { // Check if the fragment is added to the activity
                bottomSheetDialog = new BottomSheetDialog(requireActivity(), R.style.SheetDialog);
                bottomSheetDialog.setContentView(R.layout.branch_bottom_sheet_dialog);

                RecyclerView rvCountry = bottomSheetDialog.findViewById(R.id.rvCountry);

                if (getActivity() != null & rvCountry != null) { // Check if getActivity() returns a non-null value
                    rvCountry.setLayoutManager(new LinearLayoutManager(getActivity()));
                    rvCountry.setHasFixedSize(true);

                    HomeTodayListAdapter home_today_list_adapter = new HomeTodayListAdapter(branches);
                    rvCountry.setAdapter(home_today_list_adapter);
                }
            }

        } catch (java.lang.IllegalStateException e) {
            Log.e("TAG", "countryDialog: "+e.getMessage() );
        }

    }

    private void logOutUser() {

        ed.clear();
        ed.commit();
        startActivity(new Intent(getActivity(), ActivityCheckMobileNumber.class));
        requireActivity().finish();

    }

    public class HomeTodayListAdapter extends RecyclerView.Adapter<HomeTodayListAdapter.Holder> {

        List<Branch> arrayListTopic;

        public HomeTodayListAdapter(List<Branch> arrayListTopic) {
            this.arrayListTopic = arrayListTopic;
        }

        @Override
        public int getItemCount() {
            return arrayListTopic.size();
        }

        @NonNull
        @Override
        public HomeTodayListAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent,
                                                              int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itam_branch,
                    parent,
                    false);
            return new HomeTodayListAdapter.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final HomeTodayListAdapter.Holder holder,
                                     @SuppressLint("RecyclerView") final int position) {

            String branchName = arrayListTopic.get(position).getBranchName();
            holder.status.setText(branchName);

            holder.click.setOnClickListener(v -> {
                ed.putString("userBranch", arrayListTopic.get(position).getBranchName());
                ed.commit();
                String branchUserName = sp.getString("userBranch", "");
                tv_branch_admin_name.setText(branchUserName);
                bottomSheetDialog.dismiss();
            });

        }

        class Holder extends RecyclerView.ViewHolder {

            TextView status;
            LinearLayout click;

            public Holder(@NonNull View itemView) {
                super(itemView);
                status = itemView.findViewById(R.id.tv_status);
                click = itemView.findViewById(R.id.click);

            }
        }

    }

}