package com.diresos.alihaider.logreg2;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    Button reg_bn;
    EditText Name, Email, Username, Password, ConPassword;
    String name, email, username, password, conPass;
    AlertDialog.Builder builder;
    String reg_url = "http://192.168.0.105/register.php";
    int i;

    String successfulRegisterMessage;

    String app_server_url = "http://192.168.0.105/fcmtest/fcm_insert.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        reg_bn = (Button) findViewById(R.id.bn_reg);
        Name = (EditText) findViewById(R.id.reg_name);
        Email = (EditText) findViewById(R.id.reg_email);
        Username = (EditText) findViewById(R.id.reg_username);
        Password = (EditText) findViewById(R.id.reg_password);
        ConPassword = (EditText) findViewById(R.id.reg_con_password);
        builder = new AlertDialog.Builder(Register.this);
        reg_bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = Name.getText().toString();
                email = Email.getText().toString();
                username = Username.getText().toString();
                password = Password.getText().toString();
                conPass = ConPassword.getText().toString();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (
                        name.equals("") ||
                                email.equals("") ||
                                username.equals("") ||
                                password.equals("") ||
                                conPass.equals("")) {

                    builder.setTitle("Something went wrong");
                    builder.setMessage("Please fill all the fields");
                    displayAlert("input_error");


                } else if (email.matches(emailPattern)) {

                    builder.setTitle("Something went wrong");
                    builder.setMessage("Please Enter a valid Email");
                    displayAlert("input_error");

                }
/*
                //check correct Email input
                else {
                    boolean correctEmail;
                    if(correctEmail){
                        
                    }

                    for (i = 0; i < email.length(); i++) {

                        if (email.charAt(i) != '@') {
                            builder.setTitle("Something went wrong");
                            builder.setMessage("Please Enter a valid Email");
                            displayAlert("input_error");
                        }
                    }
                }
                */


                else {
                    if (!(password.equals(conPass))) {
                        builder.setTitle("Something went wrong");
                        builder.setMessage("Your passwords do not match");
                        displayAlert("input_error");
                    } else {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, reg_url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        try {
                                            JSONArray jsonArray = new JSONArray(response);
                                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                                            String code = jsonObject.getString("code");
                                            String message = jsonObject.getString("message");
                                            successfulRegisterMessage = message;
                                            builder.setTitle("Server Response");
                                            builder.setMessage(message);
                                            displayAlert(code);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }
                        ) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("name", name);
                                params.put("email", email);
                                params.put("user_name", username);
                                params.put("password", password);
                                return params;
                            }
                        };
                        MySingleton.getmInstance(Register.this).addToRequestQueue(stringRequest);


                    }

                }
            }


            /*
            private boolean isEmailCorrect(String email) {
                for (i = 0; i < email.length(); i++) {

                    if (email.charAt(i) != '@') {
                        break;
                    }
                }
                return false;
            }

            */


        });
    }

    private void displayAlert(final String code) {

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (code.equals("input_error")) {
                    Password.setText("");
                    ConPassword.setText("");
                } else if (code == "reg_success") {


                    //Code for sending device token to database

                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
                    final String token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "");
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                            new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {

                                }
                            }
                            , new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("fcm_token", token);

                            return params;
                        }
                    };
                    MySingleton.getmInstance(Register.this).addToRequestQueue(stringRequest);

                    finish();

                } else if (code == "reg_failed") {
                    Name.setText("");
                    Email.setText("");
                    Username.setText("");
                    Password.setText("");
                    ConPassword.setText("");
                }

            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}