package com.pyliavskyi.apz_android;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.microsoft.signalr.HubConnectionBuilder;

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
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.MessageReceivedHandler;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;

public class ChatActivity extends Activity {

    com.microsoft.signalr.HubConnection connection;

    private TextView userFeature;

    private List<View> messagesList;

    private LinearLayout linear;

    private SessionManager session;
    private HashMap<String, String> userinfo;

    private TextView messageField;
    private Button messageButton;


    private class ConnectionAsync extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {

                if (connection.getConnectionState() != null) {
                    connection.start().blockingAwait();
                }

                SessionManager session = new SessionManager(getApplicationContext());
                HashMap<String, String> userinfo = session.getUserDetails();

                connection.invoke(String.class, "JoinGroup", userinfo.get("chatToken"));

            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
        }

    }


    class GetOldMessages extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = getOldMessages(urls[0]);
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
                linear.removeAllViews();
                for (int i = 0; i < res.length(); i++) {
                    JSONObject json = null;
                    try {
                        json = res.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final View view = getLayoutInflater().inflate(R.layout.chat_element, null);
                    TextView name = view.findViewById(R.id.textView15);
                    TextView time = view.findViewById(R.id.textView16);
                    TextView text = view.findViewById(R.id.textView19);

                    try {
                        name.setText(json.getString("userName"));
                        time.setText(json.getString("time"));
                        text.setText(json.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    messagesList.add(view);

                    linear.addView(view);

                }
            } else {
                Toast.makeText(getApplicationContext(), "...", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public String getOldMessages(URL url) throws IOException {

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> userinfo = session.getUserDetails();

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(
                    "{\"token\":" + "\"" + userinfo.get("chatToken") + "\"" + "}");
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

    private void getOldMessagesRun() {

        messagesList = new ArrayList<View>();

        linear = findViewById(R.id.myMessagesLinear1);

        Resources res = getResources();
        String base = res.getString(R.string.baseUrl);
        String url1 = base + "/Chat/GetAllMessages";
        URL url = null;
        try {
            url = new URL(url1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new GetOldMessages().execute(url);
    }

    class SendMessage extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = sendMessage(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            getOldMessagesRun();
        }
    }


    public String sendMessage(URL url) throws IOException {

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> userinfo = session.getUserDetails();

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(
                    "{\"chatToken\":" + "\"" + userinfo.get("chatToken") + "\"" +
                            ",\"message\": " + "\"" + messageField.getText().toString() + "\"" + "}");
        } catch (JSONException err) {

        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
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

    private void sendMessageRun() {

        messageField = findViewById(R.id.editTextTextPersonName12);

        messageButton = findViewById(R.id.button8);

        messageButton.setOnClickListener(v -> {
            if (isValid()) {
                Resources res = getResources();
                String base = res.getString(R.string.baseUrl);
                String url1 = base + "/Chat/Post";
                URL url = null;
                try {
                    url = new URL(url1);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                new SendMessage().execute(url);
            } else {
                Resources res = getResources();
                Toast.makeText(getApplicationContext(), res.getString(R.string.Empty), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toMyChats() {

        ImageButton toMyChatsButton = findViewById(R.id.imageButtonToBack);

        toMyChatsButton.setOnClickListener(v -> {
            finish();
        });

    }

    private boolean isValid() {
        boolean validField = (messageField.getText().toString().length() > 0);

        if (validField) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Resources res = getResources();
        String chatConnectionUrl = res.getString(R.string.chatConnectionUrl);
        String url1 = chatConnectionUrl + "/hubs/chat";

        connection = HubConnectionBuilder.create(url1).build();

        connection.on("ReceiveMessage", (message) -> {


            userFeature = findViewById(R.id.textView16);
            userFeature.setText("Working");
        }, String.class);

        new ConnectionAsync().execute();

        getOldMessagesRun();
        sendMessageRun();
        toMyChats();
    }
}