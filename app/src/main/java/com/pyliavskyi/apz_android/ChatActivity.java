package com.pyliavskyi.apz_android;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.microsoft.signalr.HubConnectionBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

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

    private class ConnectionAsync extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                connectionRun();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {}

    }

    private void connectionRun() {

        // Create a new console logger
        Logger logger = new Logger() {

            @Override
            public void log(String message, LogLevel level) {
                System.out.println(message);
            }
        };
        Resources res = getResources();
        String chatConnectionUrl = res.getString(R.string.chatConnectionUrl);
        String url1 = chatConnectionUrl + "/hubs/chat";

        connection = HubConnectionBuilder.create(url1).build();

        if (connection.getConnectionState() != null) {
            connection.start().blockingAwait();
        }

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> userinfo = session.getUserDetails();

        connection.invoke(String.class,"JoinGroup", userinfo.get("chatToken"));

        Toast.makeText(getApplicationContext(), connection.toString(), Toast.LENGTH_SHORT).show();
    }

    private void off() {
        connection.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        new ConnectionAsync().execute();
    }
}