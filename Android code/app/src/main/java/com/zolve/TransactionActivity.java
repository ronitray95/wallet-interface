package com.zolve;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TransactionActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_white_24);
//        toolbar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        recyclerView = findViewById(R.id.recyclerViewPresent);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.canScrollVertically();
        recyclerView.setLayoutManager(linearLayoutManager);
        listAll();

    }

    private void listAll() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerURL.TRANSACTIONS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Token", response);
                try {
                    //JSONObject userObj = new JSONObject(response);
                    JSONArray arr = new JSONArray(response);//userObj.getJSONArray("data"));
                    Log.e("arr",arr.toString());
                    ArrayList<Transaction> currentData = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        Log.e("obj",arr.getJSONObject(i).toString());
                        currentData.add(new Transaction(arr.getJSONObject(i).optString("type"), arr.getJSONObject(i).optString("_id"), arr.getJSONObject(i).optDouble("amount")));
                    }
                    TransactionAdapter adapter = new TransactionAdapter(currentData);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException | NullPointerException e) {
                    Toast.makeText(TransactionActivity.this, "Error logging in", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(TransactionActivity.this, "Server could not be reached\nError code " + statusCode, Toast.LENGTH_SHORT).show();
                Log.e("PhoneError", body);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", SharedPrefManager.getInstance(TransactionActivity.this).getLogin());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(4000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(stringRequest);
    }
}