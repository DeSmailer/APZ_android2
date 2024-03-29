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

public class RegistrationActivity extends Activity {

    private String lan;

    private EditText name;
    private EditText surname;
    private EditText email;
    private EditText password;
    private EditText repeatedPassword;
    private EditText feature;


    private Button signUpButton;

    SessionManager session;

    private class SignUpRequest extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = getSignUpResponse(urls[0]);
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
                    Toast.makeText(getApplicationContext(), json.toString(), Toast.LENGTH_SHORT)
                            .show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent i = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "User unknown", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getSignUpResponse(URL url) throws IOException {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(
                    "{\"token\":" + name.getText().toString() +
                            ",\"surname\": " + surname.getText().toString() +
                            ",\"email\": " + email.getText().toString() +
                            ",\"password\": " + password.getText().toString() +
                            ",\"feature\": " + feature.getText().toString() + "}");
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
        String response = "";
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response += inputLine;
        in.close();
        return response;
    }

    private boolean isValid() {
        String mailRegex = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
        boolean isValidEmail = Pattern.matches(mailRegex, email.getText().toString());
        boolean validPassword = (
                (password.getText().toString().equals(repeatedPassword.getText().toString())) &&
                        password.getText().toString().length() > 1);
        boolean validSurname = (surname.getText().toString().length() > 1);
        boolean validName = (name.getText().toString().length() > 1);
        boolean validFeature = (feature.getText().toString().length() > 1);

        if (isValidEmail && validPassword && validSurname && validName && validFeature) {
            return true;
        }
        return false;
    }

    private void SignUpRequestRun() {

        session = new SessionManager(getApplicationContext());

        name = findViewById(R.id.editTextTextPersonName1);
        surname = findViewById(R.id.editTextTextPersonName2);
        email = findViewById(R.id.editTextTextEmailAddress3);
        password = findViewById(R.id.editTextTextPassword3);
        repeatedPassword = findViewById(R.id.editTextTextPassword4);
        feature = findViewById(R.id.editTextTextPersonName3);

        name.setText("myName");
        surname.setText("mySurname");
        email.setText("email3@gmail.com");
        password.setText("password");
        repeatedPassword.setText("password");
        feature.setText("немає");


        signUpButton = findViewById(R.id.button3);

        signUpButton.setOnClickListener(v -> {

            if (isValid()) {
                Resources res = getResources();
                String baseUrl = res.getString(R.string.baseUrl);
                String url1 = baseUrl + "/User/Register";
                URL url = null;
                try {
                    url = new URL(url1);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                new SignUpRequest().execute(url);
            } else {
                Toast.makeText(getApplicationContext(), "невалидно", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void ToLogIn() {

        TextView toRegister = findViewById(R.id.textToLogIn);

        toRegister.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(i);
            finish();
        });
        ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        SignUpRequestRun();
        ToLogIn();
    }
}