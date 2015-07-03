package com.rvce.rvce8thmile.first;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;

public class AndroidGPSTrackingActivity extends Activity {

    Button btnShowLocation;
    EditText et1;
    // GPSTracker class
    GPSTracker gps;
    String response;
    AutoCompleteTextView spinnerDropDown;
    private ArrayAdapter<String> adapter;
    List<String> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list=new ArrayList<String>();

        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpClient=new DefaultHttpClient();
                //HttpPost httpPost=new HttpPost("https://rotaractrvce.com/bidn/updateuser.php");
                HttpPost httpPost=new HttpPost("http://ibmhackblind.mybluemix.net/getalldestination.php");
                BasicResponseHandler responseHandler = new BasicResponseHandler();

                try {
                    response= httpClient.execute(httpPost, responseHandler);
                    //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        response=response.trim();
        list= Arrays.asList(response.split(","));


        spinnerDropDown =(AutoCompleteTextView)findViewById(R.id.autoComplete);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        spinnerDropDown.setAdapter(adapter);
        spinnerDropDown.setThreshold(1);
        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);

        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(AndroidGPSTrackingActivity.this);

                // check if GPS enabled
                if(gps.canGetLocation()){

                    final Handler h=new Handler();
                    Runnable r=new Runnable() {
                        @Override
                        public void run() {
                            final double latitude = gps.getLatitude();
                            final double longitude = gps.getLongitude();

                            // \n is for new line
                            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_SHORT).show();


                            Thread thread=new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    HttpClient httpClient=new DefaultHttpClient();
                                    //HttpPost httpPost=new HttpPost("https://rotaractrvce.com/bidn/updateuser.php");
                                    HttpPost httpPost=new HttpPost("http://rotaractrvce.com/bidn/updateusertest.php");
                                    BasicResponseHandler responseHandler = new BasicResponseHandler();
                                    List<NameValuePair> nameValuePair=new ArrayList<NameValuePair>(4);
                                    nameValuePair.add(new BasicNameValuePair("email",statictry.email));
                                    nameValuePair.add(new BasicNameValuePair("dest",spinnerDropDown.getText().toString().trim()));
                                    nameValuePair.add(new BasicNameValuePair("x",Double.toString(latitude)));
                                    nameValuePair.add(new BasicNameValuePair("y",Double.toString(longitude)));
                                    try {
                                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                                    } catch (UnsupportedEncodingException e) {
                                        // log exception
                                        e.printStackTrace();
                                    }

                                    try {
                                        response= httpClient.execute(httpPost, responseHandler);
                                        //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                            thread.start();
                            try {
                                thread.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                            response="";




                            h.postDelayed(this, 5000);
                        }
                    };
                    h.post(r);


                  }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            }
        });
    }

}