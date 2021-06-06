package com.pyliavskyi.apz_android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.ArrayList;
import java.util.List;

public class AllInstitutionActivity extends Activity {

    private List<View> institutionList;

    private LinearLayout linear;

    class GetInstitutions extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = getInstitutions(urls[0]);
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
                    final View view = getLayoutInflater().inflate(R.layout.all_institution_element, null);
                    EditText name = (EditText) view.findViewById(R.id.editText1);
                    EditText location = (EditText) view.findViewById(R.id.editText2);

                    try {
                        name.setText(String.valueOf(json.getString("name")));
                        location.setText(json.getString("location"));
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


    public String getInstitutions(URL url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
            respp += inputLine;
        in.close();
        return respp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_institution);


        institutionList = new ArrayList<View>();

        linear = findViewById(R.id.InstitutionList1);

        Resources res = getResources();
        String base = res.getString(R.string.baseUrl);
        String url1 = base + "/Institution/Get";
        URL url = null;
        try {
            url = new URL(url1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new GetInstitutions().execute(url);
    }

}