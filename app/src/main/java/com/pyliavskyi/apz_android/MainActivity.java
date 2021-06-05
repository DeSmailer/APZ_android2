package com.pyliavskyi.apz_android;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class MainActivity extends Activity {

    TextView testResponse;
    HashMap<String, String> userinfo;

    class GetTestOk extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try{
                response = getResponse(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response){

            if(response != null) {

                JSONObject json = null;

               try {
                    json = new JSONObject(response);
                   testResponse.setText(json.getString("myTestString"));
               } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else{
                Toast.makeText(getApplicationContext(),"...", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public String getResponse(URL url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoInput(true);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));
        String respp = "";
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            respp+=inputLine;
        in.close();
        return respp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*testResponse = findViewById(R.id.test);
        Resources res = getResources();
        String base = res.getString(R.string.baseUrl);
        String url1 = base + "/Test/Test";
        URL url = null;
        try {
            url = new URL(url1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new GetTestOk().execute(url);*/
        testResponse = findViewById(R.id.test);
        SessionManager session = new SessionManager(getApplicationContext());
        userinfo = session.getUserDetails();
        Toast.makeText(getApplicationContext(), userinfo.get("token"), Toast.LENGTH_SHORT)
                .show();
    }
}