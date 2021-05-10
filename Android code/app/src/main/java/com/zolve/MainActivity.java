package com.zolve;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {
    TextView currAmt;
    Button credit;
    Button debit;
    Button refresh;
    Button logout;
    Button listAll;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currAmt = findViewById(R.id.currAmt);
        refresh = findViewById(R.id.getCurrAmt);
        credit = findViewById(R.id.creditAmt);
        debit = findViewById(R.id.debitAmt);
        logout = findViewById(R.id.logout);
        listAll = findViewById(R.id.listAll);
        requestQueue = Volley.newRequestQueue(this);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBalance();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(MainActivity.this).logout();
                finish();
            }
        });
        listAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,TransactionActivity.class));
            }
        });
        credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                final EditText input = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alert.setMessage("Credit");
                alert.setTitle("Enter balance to credit");

                alert.setView(input);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String val = input.getText().toString();
                        double amt = 0.0;
                        try {
                            amt = Double.parseDouble(val);
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Must be a real number", Toast.LENGTH_SHORT).show();
                        }
                        creditBalance(amt);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });

                alert.show();
            }
        });
        debit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                final EditText input = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alert.setMessage("Debit");
                alert.setTitle("Enter balance to debit");
                alert.setView(input);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String val = input.getText().toString();
                        double amt = 0.0;
                        try {
                            amt = Double.parseDouble(val);
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Must be a real number", Toast.LENGTH_SHORT).show();
                        }
                        debitBalance(amt);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });
                alert.show();
            }
        });
    }

    private void debitBalance(double amt) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerURL.DEBIT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Token", response);
                try {
                    JSONObject userObj = new JSONObject(response);
                    int status = userObj.optInt("data", -1);
                    if (status == 1)
                        Toast.makeText(MainActivity.this, "Balance debited successfully", Toast.LENGTH_SHORT).show();
                    else if (status == 0)
                        Toast.makeText(MainActivity.this, "Balance cannot be debited below minimum balance", Toast.LENGTH_SHORT).show();
                } catch (JSONException | NullPointerException e) {
                    Toast.makeText(MainActivity.this, "Error debiting balance", Toast.LENGTH_SHORT).show();
                }
                getBalance();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error == null || error.networkResponse == null)
                    return;
                String statusCode = String.valueOf(error.networkResponse.statusCode), body;
                body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                if (statusCode.equals("500"))
                    Toast.makeText(MainActivity.this, "Server could not be reached\nError code " + statusCode, Toast.LENGTH_SHORT).show();
                Log.e("PhoneError", body);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("amount", String.valueOf(amt));
                params.put("mobile", SharedPrefManager.getInstance(MainActivity.this).getLogin());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(4000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    private void creditBalance(double amt) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerURL.CREDIT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Token", response);
                try {
                    JSONObject userObj = new JSONObject(response);
                    if (userObj.optInt("data", 0) == 1)
                        Toast.makeText(MainActivity.this, "Balance credited successfully", Toast.LENGTH_SHORT).show();
                } catch (JSONException | NullPointerException e) {
                    Toast.makeText(MainActivity.this, "Error crediting balance", Toast.LENGTH_SHORT).show();
                }
                getBalance();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error == null || error.networkResponse == null)
                    return;
                String statusCode = String.valueOf(error.networkResponse.statusCode), body;
                body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                if (statusCode.equals("500"))
                    Toast.makeText(MainActivity.this, "Server could not be reached\nError code " + statusCode, Toast.LENGTH_SHORT).show();
                Log.e("PhoneError", body);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("amount", String.valueOf(amt));
                params.put("mobile", SharedPrefManager.getInstance(MainActivity.this).getLogin());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(4000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBalance();
    }

    private void getBalance() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerURL.BALANCE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Token", response);
                try {
                    JSONObject userObj = new JSONObject(response);
                    currAmt.setText(String.valueOf(userObj.optDouble("data", 0.0)));
                } catch (JSONException | NullPointerException e) {
                    Toast.makeText(MainActivity.this, "Error acquiring balance", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(MainActivity.this, "Server could not be reached\nError code " + statusCode, Toast.LENGTH_SHORT).show();
                Log.e("PhoneError", body);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", SharedPrefManager.getInstance(MainActivity.this).getLogin());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(4000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }
}