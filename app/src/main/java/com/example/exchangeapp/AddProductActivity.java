package com.example.exchangeapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddProductActivity extends AppCompatActivity {

    public static final String DB_URL = "https://exchange-app-ce922-default-rtdb.asia-southeast1.firebasedatabase.app";

    private EditText edtProductName, edtProductDescription, edtProductPrice;
    private Button btnAddProduct;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance(DB_URL).getReference("Products");

        edtProductName = findViewById(R.id.edtProductName);
        edtProductDescription = findViewById(R.id.edtProductDescription);
        edtProductPrice = findViewById(R.id.edtProductPrice);
        btnAddProduct = findViewById(R.id.btnAddProduct);

        btnAddProduct.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = edtProductName.getText().toString().trim();
        String description = edtProductDescription.getText().toString().trim();
        String price = edtProductPrice.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            edtProductName.setError("Product name is required");
            Toast.makeText(this, "Please enter product name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            edtProductDescription.setError("Description is required");
            Toast.makeText(this, "Please enter product description", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(price)) {
            edtProductPrice.setError("Price is required");
            Toast.makeText(this, "Please enter product price", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error: User not logged in. Please log in first.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        String userId = currentUser.getUid();
        String productId = mDatabase.push().getKey();

        if (productId == null) {
            Toast.makeText(this, "Error: Could not generate product ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Product product = new Product(productId, name, description, price, userId);

        btnAddProduct.setEnabled(false);

        mDatabase.child(productId).setValue(product)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddProductActivity.this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddProductActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnAddProduct.setEnabled(true);
                    String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                    if (errorMsg.contains("Permission denied")) {
                        Toast.makeText(AddProductActivity.this, "Permission Denied! Check Firebase Console -> Realtime Database -> Rules.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AddProductActivity.this, "Failed to save product: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
