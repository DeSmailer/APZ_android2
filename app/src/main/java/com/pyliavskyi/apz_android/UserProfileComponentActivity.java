package com.pyliavskyi.apz_android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Pattern;

public class UserProfileComponentActivity extends Activity {

    private EditText userId;
    private EditText userName;
    private EditText userSurname;
    private EditText userFeature;

    private Button confirmEditingButton;

    SessionManager session;
    HashMap<String, String> userinfo;

    private class GetUserInfoRequest extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = getUserInfoResponse(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {

                    userId = findViewById(R.id.editTextTextPersonName);
                    userName = findViewById(R.id.editTextTextPersonName4);
                    userSurname = findViewById(R.id.editTextTextPersonName5);
                    userFeature = findViewById(R.id.editTextTextPersonName6);

                    userId.setText(json.getString("id"));
                    userName.setText(json.getString("name"));
                    userSurname.setText(json.getString("surname"));
                    userFeature.setText(json.getString("feature"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Intent i = new Intent(getApplicationContext(), MainActivity.class);
                //startActivity(i);
                //finish();
            } else {
                Toast.makeText(getApplicationContext(), "User unknown", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public String getUserInfoResponse(URL url) throws IOException {
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> userinfo = session.getUserDetails();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(
                    "{\"token\":" + "\"" + userinfo.get("token") + "\"" + "}");
        } catch (JSONException err) {
            Toast.makeText(getApplicationContext(), err.toString(), Toast.LENGTH_SHORT).show();

        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        connection.setDoInput(true);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(jsonObject.toString());
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));
        String response = "";
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response += inputLine;
        in.close();
        return response;
    }

    private void GetUserInfoRun() {
        Resources res = getResources();
        String base = res.getString(R.string.baseUrl);
        String url1 = base + "/User/GetByToken";
        URL url = null;
        try {
            url = new URL(url1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new GetUserInfoRequest().execute(url);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_component);

        GetUserInfoRun();
    }
}