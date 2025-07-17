package com.dfc.agsolutions.AppUtils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.FirebaseApp;

public class Myapplication  extends Application {
public  static Context context;



    @Override
    public void onCreate() {
        super.onCreate();
//        FirebaseApp.initializeApp(this);

        context = getApplicationContext();
    }


    public static Context getContext() {
        return context;
    }

//    private static boolean isNetworkConnected() {
//        ConnectivityManager connectivityManager = (ConnectivityManager)getContext(). getSystemService("connectivity");
//        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
//    }


    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }



    public static void noInternet(final Activity activity) {

        AlertDialog.Builder  builder = new AlertDialog.Builder(activity);
        //Setting message manually and performing action on button click
        builder.setMessage("Please Cheq Your Internet Connection")
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();

                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("No Internet");
        alert.show();
    }
}
