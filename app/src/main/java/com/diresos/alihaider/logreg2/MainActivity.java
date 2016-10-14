package com.diresos.alihaider.logreg2;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {
    TextView textView;
    Button login_button;
    EditText Username, Password;
    String username, password;
    String login_url="http://192.168.0.105/login.php";
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView=(TextView)findViewById(R.id.reg_txt);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,Register.class);
                startActivity(i);
            }
        });

        builder=new AlertDialog.Builder(MainActivity.this);

        login_button=(Button)findViewById(R.id.bn_login);
        Username=(EditText)findViewById(R.id.login_name);
        Password=(EditText)findViewById(R.id.login_password);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username=Username.getText().toString();
                password=Password.getText().toString();

                if(username.equals("") || password.equals("")){
                    builder.setTitle("Something went wrong");
                    displayAlert("Enter valid username and password");
                }
                else{
                    StringRequest stringRequest=new StringRequest(Request.Method.POST, login_url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONArray jsonArray=new JSONArray(response);
                                JSONObject jsonObject=jsonArray.getJSONObject(0);
                                String code=jsonObject.getString("code");

                                if(code=="login_failed"){
                                    builder.setTitle("Login error");
                                    displayAlert(jsonObject.getString("message"));
                                }
                                else{
                                    Intent intent=new Intent(MainActivity.this,LoginSuccess.class);
                                    Bundle bundle=new Bundle();
                                    bundle.putString("name",jsonObject.getString("name"));
                                    bundle.putString("email",jsonObject.getString("email"));
                                    intent.putExtras(bundle);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_LONG).show();
                            error.printStackTrace();

                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            Map<String,String> params=new HashMap<String, String>();
                            params.put("user_name",username);
                            params.put("password",password);

                            return params;
                        }
                    };


                    MySingleton.getmInstance(MainActivity.this).addToRequestQueue(stringRequest);
                }
            }
        });
    }

    public void displayAlert(String message){
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Username.setText("");
                Password.setText("");
            }
        });

        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
}
