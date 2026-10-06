package com.example.exchangeapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText edtName, edtPhone, edtAddress, edtEmail, edtPassword, edtConfirmPassword;
    Button btnRegister;

    FirebaseAuth mAuth;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        usersRef = FirebaseDatabase
                .getInstance("https://exchange-app-ce922-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users");

        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {

            String name = edtName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                edtName.setError("Name is required");
                return;
            }

            if (TextUtils.isEmpty(phone)) {
                edtPhone.setError("Phone number is required");
                return;
            }

            if (TextUtils.isEmpty(address)) {
                edtAddress.setError("Address is required");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                edtEmail.setError("Email is required");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                edtPassword.setError("Password is required");
                return;
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                edtConfirmPassword.setError("Please confirm your password");
                return;
            }

            if (!password.equals(confirmPassword)) {
                edtConfirmPassword.setError("Passwords do not match");
                return;
            }

            btnRegister.setEnabled(false);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {

                        if (task.isSuccessful() && mAuth.getCurrentUser() != null) {

                            String uid = mAuth.getCurrentUser().getUid();

                            Map<String, Object> userData = new HashMap<>();
                            userData.put("name", name);
                            userData.put("phone", phone);
                            userData.put("address", address);
                            userData.put("email", email);

                            usersRef.child(uid)
                                    .setValue(userData)
                                    .addOnSuccessListener(unused -> {

                                        Toast.makeText(
                                                RegisterActivity.this,
                                                "Registration successful",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        Intent intent = new Intent(
                                                RegisterActivity.this,
                                                HomeActivity.class
                                        );

                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {

                                        btnRegister.setEnabled(true);

                                        Toast.makeText(
                                                RegisterActivity.this,
                                                "Profile save failed: " + e.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    });

                        } else {

                            btnRegister.setEnabled(true);

                            String errorMsg = task.getException() != null
                                    ? task.getException().getMessage()
                                    : "Registration failed.";

                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Registration failed: " + errorMsg,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });
    }
}