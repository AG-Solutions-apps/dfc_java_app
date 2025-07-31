package com.dfc.agsolutions.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.dfc.agsolutions.model.TodoListDataModel;
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

    LottieAnimationView lav_no_data;

    SharedPreferences sp;
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
    public View onCreateView(LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_previous_history, container, false);

        sp = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        ed = sp.edit();
        lav_no_data = view.findViewById(R.id.lav_no_data);

//        cd = view.findViewById(R.id.cd);
        dialog = new ProgressDialog(requireActivity());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        rv = view.findViewById(R.id.rv);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Implement your refresh logic here
            // This is usually where you would fetch new data
            lav_no_data.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
            previousHistory();
        });
        previousHistory();

        return view;

    }

    public void previousHistory() {

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

        Call<TodoListDataModel> call = loginService.getToDoList(sp.getString("userBranch", ""),
                String.valueOf(2));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TodoListDataModel> call,
                                   @NonNull Response<TodoListDataModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    ArrayList<TodoListDataModel> branches = response.body().getData();

                    Log.e("Response---------", "onResponse: "+response.body().getData().size());


                    if(response.body().getData().isEmpty()) {
                        lav_no_data.setVisibility(View.VISIBLE);
                        rv.setVisibility(View.GONE);
                    } else {
                        lav_no_data.setVisibility(View.GONE);
                        rv.setVisibility(View.VISIBLE);
                        HomeTodayListAdapter adapter = new HomeTodayListAdapter(response.body().getData());
                        rv.setAdapter(adapter);
                    }

                    Log.e("response..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(requireActivity(),  "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<TodoListDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("TodoListDataModel: ", "" + t);
                dialog.dismiss();
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    public void updateToDoList(String idd) {

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

        Call<TodoListDataModel> call = loginService.getCompleteUpdateList(idd,
                sp.getString("userBranch", ""));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TodoListDataModel> call,
                                   @NonNull Response<TodoListDataModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    ArrayList<TodoListDataModel> branches = response.body().getData();

                    Log.e("Response---------", "onResponse: "+response.body().getData().size());
                    previousHistory();
                    Log.e("response..", "branches:-  " + branches.size());
                } else {
                    Toast.makeText(requireActivity(),  "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<TodoListDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("TodoListDataModel: ", "" + t);
                dialog.dismiss();
            }
        });
    }

    public class HomeTodayListAdapter extends
            RecyclerView.Adapter<HomeTodayListAdapter.Holder> {

        ArrayList<TodoListDataModel> data;

        public HomeTodayListAdapter(ArrayList<TodoListDataModel> data) {
            this.data = data;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo_pending1,
                    parent,
                    false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Holder holder, @SuppressLint("RecyclerView") final int position) {


            holder.tv_description.setText(data.get(position).getTodo_description());

            holder.btn.setOnClickListener(v -> {
                updateToDoList(data.get(position).getId());
                notifyDataSetChanged();
            });

        }

        class Holder extends RecyclerView.ViewHolder {

            TextView tv_description;

            ImageView btn;

            public Holder(@NonNull View itemView) {
                super(itemView);

                tv_description = itemView.findViewById(R.id.description);
                btn = itemView.findViewById(R.id.iv_btn_re_create);

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        previousHistory();
    }

}