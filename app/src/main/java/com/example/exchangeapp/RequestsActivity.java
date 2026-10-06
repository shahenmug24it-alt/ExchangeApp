package com.example.exchangeapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity {

    private TextView txtNoRequests;
    private RecyclerView recyclerViewRequests;

    private RequestAdapter adapter;
    private List<ExchangeRequest> requestList;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance(AddProductActivity.DB_URL).getReference("Requests");

        txtNoRequests = findViewById(R.id.txtNoRequests);
        recyclerViewRequests = findViewById(R.id.recyclerViewRequests);

        recyclerViewRequests.setLayoutManager(new LinearLayoutManager(this));
        requestList = new ArrayList<>();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentUserId = currentUser != null ? currentUser.getUid() : "";

        adapter = new RequestAdapter(this, requestList, currentUserId);
        recyclerViewRequests.setAdapter(adapter);

        if (!currentUserId.isEmpty()) {
            loadRequests(currentUserId);
        } else {
            txtNoRequests.setVisibility(View.VISIBLE);
            recyclerViewRequests.setVisibility(View.GONE);
        }
    }

    private void loadRequests(String currentUserId) {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ExchangeRequest request = dataSnapshot.getValue(ExchangeRequest.class);
                    if (request != null) {
                        if (currentUserId.equals(request.getRequesterId()) || currentUserId.equals(request.getOwnerId())) {
                            requestList.add(request);
                        }
                    }
                }

                if (requestList.isEmpty()) {
                    txtNoRequests.setVisibility(View.VISIBLE);
                    recyclerViewRequests.setVisibility(View.GONE);
                } else {
                    txtNoRequests.setVisibility(View.GONE);
                    recyclerViewRequests.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RequestsActivity.this, "Failed to load requests: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
