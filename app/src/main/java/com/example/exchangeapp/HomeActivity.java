package com.example.exchangeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    Button btnSell;
    Button btnBrowse;
    Button btnRequests;
    Button btnLogout;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        btnSell = findViewById(R.id.btnSell);
        btnBrowse = findViewById(R.id.btnBrowse);
        btnRequests = findViewById(R.id.btnRequests);
        btnLogout = findViewById(R.id.btnLogout);

        // Sell / Add Product
        btnSell.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AddProductActivity.class);
            startActivity(intent);
        });

        // Browse Products
        btnBrowse.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProductListActivity.class);
            startActivity(intent);
        });

        // My Requests
        btnRequests.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RequestsActivity.class);
            startActivity(intent);
        });

        // Logout
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
