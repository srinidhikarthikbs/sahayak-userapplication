package com.rvce.rvce8thmile.first;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this,AndroidGPSTrackingActivity.class));
        finish();
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpClient=new DefaultHttpClient();
                //HttpGet httpGet=new HttpGet("http://rotaractrvce.com/bidn/updateusertest.php");
                BasicResponseHandler responseHandler = new BasicResponseHandler();
                HttpPost httpPost=new HttpPost("http://ibmhackblind.mybluemix.net/updateusertest.php");
                List<NameValuePair> nameValuePair=new ArrayList<NameValuePair>(4);
                nameValuePair.add(new BasicNameValuePair("email","srinidhikarthikbs@gmail.com"));
                nameValuePair.add(new BasicNameValuePair("dest","dest"));
                nameValuePair.add(new BasicNameValuePair("x","10.9"));
                nameValuePair.add(new BasicNameValuePair("y","21.7"));
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                try {
                    response= httpClient.execute(httpPost, responseHandler);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        //thread.start();
        /*try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/





        //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
