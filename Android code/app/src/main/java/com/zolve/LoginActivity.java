package com.zolve;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Button login;
    EditText phNo;
    SwitchCompat remember;
    boolean status;
    String phoneNumber;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.login);
        phNo = findViewById(R.id.editTextPhone);
        remember = findViewById(R.id.remember);
        requestQueue = Volley.newRequestQueue(this);
        phNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phNo.setError(null);
            }
        });
        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                status=isChecked;
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = phNo.getText().toString();
                Log.e("yy",phoneNumber);
                if (phoneNumber.length() == 10 && "6789".indexOf(phoneNumber.charAt(0)) != -1) {
                    loginNow(phoneNumber);
                } else if (phoneNumber.length() != 10)
                    phNo.setError("Please enter 10 digit phone number");
                else
                    phNo.setError("Number cannot start with " + phoneNumber.charAt(0));
            }
        });
    }
    private void loginNow(String ph) {
        Log.e("yy1",phoneNumber);
        Log.e("yy1",ServerURL.LOGIN_URL);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerURL.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Token", response);
                try {
                    JSONObject userObj = new JSONObject(response);
                    int status = userObj.optInt("data", -1);
                    if (status == 1) {
                        Toast.makeText(LoginActivity.this, "Logging in", Toast.LENGTH_SHORT).show();
                        SharedPrefManager sharedPrefManager=SharedPrefManager.getInstance(LoginActivity.this);
                        sharedPrefManager.userLogin(ph);
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }
                    else if (status == 0)
                        Toast.makeText(LoginActivity.this, "Error logging in", Toast.LENGTH_SHORT).show();
                } catch (JSONException | NullPointerException e) {
                    Toast.makeText(LoginActivity.this, "Error logging in", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error == null || error.networkResponse == null)
                    return;
                String statusCode = String.valueOf(error.networkResponse.statusCode), body;
                body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                if (statusCode.equals("500"))
                    Toast.makeText(LoginActivity.this, "Server could not be reached\nError code " + statusCode, Toast.LENGTH_SHORT).show();
                Log.e("PhoneError", body);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", String.valueOf(ph));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(4000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
}