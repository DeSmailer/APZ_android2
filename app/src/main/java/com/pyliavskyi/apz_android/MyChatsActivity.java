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
import android.widget.ImageButton;
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

public class MyChatsActivity extends Activity {

    private HashMap<String, String> userinfo;

    private List<View> chats;

    private LinearLayout linear;

    SessionManager session;

    private String chatId;
    private String initiatorId;
    private String recipientId;
    private String institutionId;


    class GetMyChats extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = getMyChats(urls[0]);
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
                    final View view = getLayoutInflater().inflate(R.layout.my_chats_element, null);

                    EditText chatIdText = (EditText) view.findViewById(R.id.editTextTextPersonName11);
                    EditText initiatorIdText = (EditText) view.findViewById(R.id.editTextTextPersonName8);
                    EditText recipientIdText = (EditText) view.findViewById(R.id.editTextTextPersonName9);
                    EditText institutionIdText = (EditText) view.findViewById(R.id.editTextTextPersonName10);

                    EditText initiatorNameText = (EditText) view.findViewById(R.id.editText13);
                    EditText initiatorSurnameText = (EditText) view.findViewById(R.id.editText22);

                    Button button = (Button) view.findViewById(R.id.button2);
                    button.setOnClickListener(v -> {
                        try {
                            chatId = chatIdText.getText().toString();
                            initiatorId = initiatorIdText.getText().toString();
                            recipientId = recipientIdText.getText().toString();
                            institutionId = institutionIdText.getText().toString();

                            GetChatTokenRun();

                        } catch (IndexOutOfBoundsException ex) {
                            ex.printStackTrace();
                        }
                    });

                    try {
                        chatIdText.setText(String.valueOf(json.getInt("id")));
                        initiatorIdText.setText(json.getString("initiatorId"));
                        recipientIdText.setText(json.getString("recipientId"));
                        institutionIdText.setText(json.getString("institutionId"));
                        initiatorNameText.setText(json.getString("initiatorName"));
                        initiatorSurnameText.setText(json.getString("initiatorSurname"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    chats.add(view);

                    linear.addView(view);

                }

            } else {
                Toast.makeText(getApplicationContext(), "...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getMyChats(URL url) throws IOException {

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> userinfo = session.getUserDetails();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(
                    "{\"token\":" + "\"" + userinfo.get("token") + "\"" +
                            ",\"role\": " + "\"" + userinfo.get("role") + "\"" +
                            ",\"institutionId\": " + "\"" + userinfo.get("institutionId") + "\"" + "}");
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

    class GetChatToken extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = getChatToken(urls[0]);
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
                userinfo = session.getUserDetails();
                try {

                    session.putChatToken(json.getString("chatToken"));
                    session.putChatId(json.getString("chatId").toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                userinfo = session.getUserDetails();

                Toast.makeText(getApplicationContext(), userinfo.get("chatToken") + " " + userinfo.get("chatId"), Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(i);
                //finish();
            } else {
                Toast.makeText(getApplicationContext(), "Eror", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public String getChatToken(URL url) throws IOException {


        session = new SessionManager(getApplicationContext());
        HashMap<String, String> userinfo = session.getUserDetails();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(
                    "{\"id\":" + Integer.parseInt(chatId) +
                            ",\"initiatorId\":" + Integer.parseInt(initiatorId) +
                            ",\"recipientId\":" + Integer.parseInt(recipientId) +
                            ",\"institutionId\":" + Integer.parseInt(institutionId) + "}");
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

    private void GetChatTokenRun() {

        Resources res = getResources();
        String base = res.getString(R.string.baseUrl);
        String url1 = base + "/Chat/GetChatToken";
        URL url = null;
        try {
            url = new URL(url1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new GetChatToken().execute(url);
    }

    private void GetMyChatsRequestRun() {

        chats = new ArrayList<View>();

        SessionManager session = new SessionManager(getApplicationContext());

        userinfo = session.getUserDetails();
        linear = findViewById(R.id.myChatsLinear1);

        Resources res = getResources();
        String base = res.getString(R.string.baseUrl);
        String url1 = base + "/Chat/UserChats";
        URL url = null;
        try {
            url = new URL(url1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new GetMyChats().execute(url);
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
        setContentView(R.layout.activity_my_chats);

        GetMyChatsRequestRun();
        NavBar();
    }
}