package com.pyliavskyi.apz_android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
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

public class SignInActivity extends Activity {

    private EditText email;
    private EditText password;

    private Button loginButton;

    SessionManager session;

    private class SignInRequest extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = getSignInResponse(urls[0]);
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

                    session.createLoginSession(
                            "",
                            json.getString("token"),
                            "",
                            json.getString("institutionId"),
                            json.getString("role"),
                            ""
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {

                    session.putChatId("123");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), "запрос вернуло", Toast.LENGTH_SHORT)
                        .show();
                HashMap<String, String> userinfo = session.getUserDetails();
                Toast.makeText(getApplicationContext(), userinfo.get("token"), Toast.LENGTH_SHORT)
                        .show();
                Toast.makeText(getApplicationContext(), userinfo.get("chatId"), Toast.LENGTH_SHORT)
                        .show();
                Intent i = new Intent(getApplicationContext(), UserProfileComponentActivity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "User unknown", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getSignInResponse(URL url) throws IOException {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(
                    "{\"email\":" + email.getText().toString() +
                            ",\"password\": " + password.getText().toString() + "}");
        } catch (JSONException err) {

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
        String respp = "";
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            respp += inputLine;
        in.close();
        return respp;
    }

    private boolean isValid() {
        String mailRegex = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
        boolean isValidEmail = Pattern.matches(mailRegex, email.getText().toString());
        boolean validPassword = (password.getText().toString().length() > 1);


        Toast.makeText(getApplicationContext(), isValidEmail ? "1true" : "1false", Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), validPassword ? "2true" : "2false", Toast.LENGTH_SHORT).show();


        if (isValidEmail && validPassword) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        session = new SessionManager(getApplicationContext());

        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        email.setText("email11@gmail.com");
        password.setText("password");

        loginButton = findViewById(R.id.button);

        loginButton.setOnClickListener(v -> {
            if (isValid()) {
                Resources res = getResources();
                String baseUrl = res.getString(R.string.baseUrl);
                String url1 = baseUrl + "/User/LoginLikeClient";
                URL url = null;
                try {
                    url = new URL(url1);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                new SignInRequest().execute(url);
            } else {
                Toast.makeText(getApplicationContext(), "ошибка", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}