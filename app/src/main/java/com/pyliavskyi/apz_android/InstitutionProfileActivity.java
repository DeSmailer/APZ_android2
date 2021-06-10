package com.pyliavskyi.apz_android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class InstitutionProfileActivity extends Activity {

    private EditText institutionId;
    private EditText institutionName;
    private EditText institutionLocation;


    private EditText chatCode;

    private Button toMyChats;

    SessionManager session;
    HashMap<String, String> userinfo;

    private class GetInstitutionInfoRequest extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = getInstitutionInfoResponse(urls[0]);
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

                    institutionId = findViewById(R.id.editTextTextPersonName81);
                    institutionName = findViewById(R.id.editTextTextPersonName91);
                    institutionLocation = findViewById(R.id.editTextTextPersonName101);

                    institutionId.setText(json.getString("id"));
                    institutionName.setText(json.getString("name"));
                    institutionLocation.setText(json.getString("location"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "User unknown", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public String getInstitutionInfoResponse(URL url) throws IOException {
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

    private void GetInstitutionInfoRun() {
        Resources res = getResources();
        String base = res.getString(R.string.baseUrl);
        String url1 = base + "/Institution/GetById";
        URL url = null;
        try {
            url = new URL(url1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new GetInstitutionInfoRequest().execute(url);
    }

    //чат код

    private class GetChatCodeRequest extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = getChatCodeResponse(urls[0]);
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

                    chatCode = findViewById(R.id.editTextTextPersonName131);

                    chatCode.setText(json.getString("chatCode"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "User unknown", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public String getChatCodeResponse(URL url) throws IOException {
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

    private void GetChatCodeRun() {
        Resources res = getResources();
        String base = res.getString(R.string.baseUrl);
        String url1 = base + "/Chat/CreateChatCode";
        URL url = null;
        try {
            url = new URL(url1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new GetChatCodeRequest().execute(url);
    }

    //кнопка перехода к чатам
    private void ToMyInstitutionsRun() {

        toMyChats = findViewById(R.id.button12);

        toMyChats.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), MyChatsActivity.class);
            startActivity(i);
        });
    }

    public void NavBar(){
        ImageButton buttonToAllInstitutions = findViewById(R.id.buttonToAllInstitutions);
        ImageButton buttonToChatCode = findViewById(R.id.buttonToChatCode);
        ImageButton buttonToProfile = findViewById(R.id.buttonToProfile);

        buttonToAllInstitutions.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), AllInstitutionActivity.class);
            startActivity(i);
            finishAffinity();
        });

        buttonToChatCode.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), ChatCodeFieldActivity.class);
            startActivity(i);
            finishAffinity();
        });

        buttonToProfile.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), UserProfileActivity.class);
            startActivity(i);
            finishAffinity();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institution_profile);

        GetInstitutionInfoRun();
        GetChatCodeRun();
        ToMyInstitutionsRun();
        NavBar();
    }
}