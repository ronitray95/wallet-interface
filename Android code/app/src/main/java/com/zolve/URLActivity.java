package com.zolve;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;

public class URLActivity extends AppCompatActivity {
private EditText setURL;
private Button set;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url);
        setURL=findViewById(R.id.setURL);
        set=findViewById(R.id.set);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url =setURL.getText().toString();
                if(url.length()==0){
                    Toast.makeText(URLActivity.this,"URL is empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                ServerURL.setBaseUrl(url);
                if(SharedPrefManager.getInstance(URLActivity.this).isLoggedIn())
                    startActivity(new Intent(URLActivity.this,MainActivity.class));
                else
                    startActivity(new Intent(URLActivity.this,LoginActivity.class));
                finish();
            }
        });
    }
}