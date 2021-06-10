package com.pyliavskyi.apz_android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class ChatCodeFieldActivity extends Activity {

    private EditText chatCode;

    private Button toChatButton;

    SessionManager session;

    private class OpenChatRequest extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = openChatResponse(urls[0]);
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

                try {

                    session.putChatToken(json.getString("chatToken"));
                    session.putChatId(json.getString("chatId").toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(i);
            } else {
                Toast.makeText(getApplicationContext(), "User unknown", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String openChatResponse(URL url) throws IOException {

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> userinfo = session.getUserDetails();

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(
                    "{\"chatCode\":" + "\"" + chatCode.getText().toString() + "\"" + "}");
        } catch (JSONException err) {
            Toast.makeText(getApplicationContext(), err.toString(), Toast.LENGTH_SHORT).show();
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

    private void openChatRun() {

        session = new SessionManager(getApplicationContext());

        chatCode = findViewById(R.id.editTextTextPersonName7);

        toChatButton = findViewById(R.id.button4);

        toChatButton.setOnClickListener(v -> {
            Resources res = getResources();
            String baseUrl = res.getString(R.string.baseUrl);
            String url1 = baseUrl + "/Chat/GetChatTokenByChatCode";
            URL url = null;
            try {
                url = new URL(url1);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            new OpenChatRequest().execute(url);
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_code_field);

        openChatRun();
    }
}