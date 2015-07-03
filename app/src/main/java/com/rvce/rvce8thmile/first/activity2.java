package com.rvce.rvce8thmile.first;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class activity2 extends Activity {
    public static final String MyPREFERENCES = "MyPrefs";
    public static Activity hut;


    public static Activity main;

    GoogleCloudMessaging gcm;

    String regId;

    Handler h;
    Runnable runnable;
    ShareExternalServer appUtil;

    AsyncTask<Void, Void, String> shareRegidTask;

    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";

    static final String TAG = "Register Activity";


    SharedPreferences sharedpreferences;
    String response,feedresponse,emailid1;


    Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);




        doaftersettingimage();
    }

    public void doaftersettingimage()
    {
        context = getApplicationContext();
        String accnts="";int i;
        AccountManager manager=(AccountManager) getSystemService(Context.ACCOUNT_SERVICE);
        Account[] list=manager.getAccounts();
        for(i=0;i<list.length;i++)
            if(list[i].type.equals("com.google")){
                accnts+=list[i].name;
                i=list.length+1;
            }
        emailid1=accnts;
        statictry.email=emailid1;


//0008001008055
        if(isNetworkAvaliable(this))
        {
            if (TextUtils.isEmpty(regId)) {
                regId = registerGCM();
                Log.d("RegisterActivity", "GCM RegId: " + regId);
            } else {
                //Toast.makeText(getApplicationContext(),"Already Registered with GCM Server!",Toast.LENGTH_LONG).show();
                Log.d("regidnotempty:","Already Registered with GCM Server!");
            }

            h=new Handler();
            runnable=new Runnable() {
                @Override
                public void run() {
                    if(TextUtils.isEmpty(regId)) h.postDelayed(this,200);
                    else
                    {
                        if (TextUtils.isEmpty(regId)) {
                            //Toast.makeText(getApplicationContext(), "RegId is empty!",Toast.LENGTH_LONG).show();
                            Log.d("inelsepart","regidempty");
                        } else {
                            //Intent i = new Intent(getApplicationContext(),MainActivity.class);
                            //i.putExtra("regId", regId);
                            Log.d("RegisterActivity",
                                    "onClick of Share: Before starting main activity.");
                            //startActivity(i);
                            //finish();



                            appUtil = new ShareExternalServer();

                            //regId = getIntent().getStringExtra("regId");
                            Log.d("MainActivity", "regId: " + regId);

                            //final Context context =getApplication();
                            shareRegidTask = new AsyncTask<Void, Void, String>() {
                                @Override
                                protected String doInBackground(Void... params) {
                                    return appUtil.shareRegIdWithAppServer(context, regId);
                                }

                                @Override
                                protected void onPostExecute(String result) {
                                    shareRegidTask = null;
                                    Toast.makeText(getApplicationContext(), result,Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(activity2.this,MainActivity.class));
                                    finish();
                                    Log.d("inappserverpostexecute",result);
                                }

                            };
                            shareRegidTask.execute(null, null, null);




                            Log.d("RegisterActivity", "onClick of Share: After finish.");
                        }
                    }

                }
            };
            h.post(runnable);

        }
        else Toast.makeText(getApplicationContext(),"Please connect to internet!!",Toast.LENGTH_LONG).show();

    }

    public static boolean isNetworkAvaliable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED);
    }



    public String registerGCM() {

        gcm = GoogleCloudMessaging.getInstance(this);
        regId = getRegistrationId(context);

        if (TextUtils.isEmpty(regId)) {

            registerInBackground();

            Log.d("RegisterActivity","registerGCM - successfully registered with GCM server - regId: "+ regId);

        } else {
            Toast.makeText(getApplicationContext(),"RegId already available. RegId: " + regId,Toast.LENGTH_LONG).show();
            Log.d("inregistergcm","RegId already available. RegId: " + regId);
        }
        //h.post(runnable);
        return regId;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
            //return "regnotfound";
        }
        int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
            //return "appversionchanged";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("RegisterActivity",
                    "I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(Config.GOOGLE_PROJECT_ID);
                    Log.d("RegisterActivity", "registerInBackground - regId: "
                            + regId);
                    msg = "Device registered, registration ID=" + regId;

                    storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    //failednointernet=1;
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                }
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //failednointernet=1;
                Toast.makeText(getApplicationContext(),"Registered with GCM Server." + msg, Toast.LENGTH_LONG).show();
                Log.d("ingcmonpostexecute","Registered with GCM Server." + msg);
                //Toast.makeText(getApplicationContext(),"Please check your Internet Connection!" + msg, Toast.LENGTH_LONG).show();
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putInt(APP_VERSION, appVersion);
        editor.apply();
    }
}
