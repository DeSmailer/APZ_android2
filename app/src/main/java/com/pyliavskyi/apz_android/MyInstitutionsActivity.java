package com.pyliavskyi.apz_android;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyInstitutionsActivity extends Activity {

    private HashMap<String, String> userinfo;

    private List<View> institutionList;

    private LinearLayout linear;

    SessionManager session;

    private String role;
    private String institutionId;

    class SignInLikeEmployee extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = signInLikeEmployee(urls[0]);
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

                session = new SessionManager(getApplicationContext());
                HashMap<String, String> userinfo = session.getUserDetails();
                try {
                    Toast.makeText(getApplicationContext(), json.getString("token"), Toast.LENGTH_SHORT).show();

                    session.putToken(json.getString("token"));
                    session.putInstitutionId(json.getString("institutionId"));
                    session.putRole(json.getString("role"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                userinfo = session.getUserDetails();

                Toast.makeText(getApplicationContext(), userinfo.get("token"), Toast.LENGTH_SHORT).show();

                //Intent i = new Intent(getApplicationContext(), UserProfileActivity.class);
                //startActivity(i);
                //finish();
            } else {
                Toast.makeText(getApplicationContext(), "Eror", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public String signInLikeEmployee(URL url) throws IOException {


        session = new SessionManager(getApplicationContext());
        HashMap<String, String> userinfo = session.getUserDetails();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(
                    "{\"token\":" + "\"" + userinfo.get("token") + "\"" +
                            ",\"role\": " + "\"" + role + "\"" +
                            ",\"institutionId\": " + "\"" + institutionId + "\"" + "}");
        } catch (JSONException err) {
            Toast.makeText(getApplicationContext(), err.toString(), Toast.LENGTH_SHORT).show();

        }
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
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
        String respp = "";
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            respp += inputLine;
        in.close();
        return respp;
    }

    private void SignInLikeEmployeeRun() {

        Resources res = getResources();
        String base = res.getString(R.string.baseUrl);
        String url1 = base + "/User/LoginLikeEmployee";
        URL url = null;
        try {
            url = new URL(url1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new SignInLikeEmployee().execute(url);
    }

    class GetMyInstitutions extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = getMyInstitutions(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                JSONArray res = null;
                try {
                    res = new JSONArray(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < res.length(); i++) {
                    JSONObject json = null;
                    try {
                        json = res.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final View view = getLayoutInflater().inflate(R.layout.my_institution_element, null);
                    EditText idText = (EditText) view.findViewById(R.id.editTextTextPersonName11);
                    EditText nameText = (EditText) view.findViewById(R.id.editText13);
                    EditText roleText = (EditText) view.findViewById(R.id.editText22);
                    Button button = (Button) view.findViewById(R.id.button2);

                    button.setOnClickListener(v -> {
                        try {
                            role = roleText.getText().toString();
                            institutionId = idText.getText().toString();
                            SignInLikeEmployeeRun();
                        } catch (IndexOutOfBoundsException ex) {
                            ex.printStackTrace();
                        }
                    });

                    try {
                        idText.setText(String.valueOf(json.getInt("institutionId")));
                        nameText.setText(json.getString("institutionName"));
                        roleText.setText(json.getString("role"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    institutionList.add(view);

                    linear.addView(view);

                }

            } else {
                Toast.makeText(getApplicationContext(), "...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getMyInstitutions(URL url) throws IOException {

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
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("token", userinfo.get("token"));
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

    private void GetMyInstitutionsRequestRun() {

        institutionList = new ArrayList<View>();

        SessionManager session = new SessionManager(getApplicationContext());

        userinfo = session.getUserDetails();
        linear = findViewById(R.id.MyInstitutionList1);

        Resources res = getResources();
        String base = res.getString(R.string.baseUrl);
        String url1 = base + "/InstitutionEmployee/GetUserJobs";
        URL url = null;
        try {
            url = new URL(url1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new GetMyInstitutions().execute(url);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_institutions);

        GetMyInstitutionsRequestRun();
    }
}